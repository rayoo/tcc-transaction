package org.mengyun.tcctransaction.recover;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.OptimisticLockException;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.TransactionRepository;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.repository.helper.ZooKeeperHelper;
import org.mengyun.tcctransaction.support.TransactionConfigurator;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Created by changmingxie on 11/10/15.
 */
public class TransactionRecovery {

	static final Logger logger = Logger.getLogger(TransactionRecovery.class.getSimpleName());

	private TransactionConfigurator transactionConfigurator;
	private static final ExecutorService recoveryExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
			new ThreadFactoryBuilder().setDaemon(true).setNameFormat("TransactionRecoveryExecutor-%d").build());

	public void startRecover() {
		InterProcessMutex lock = null;
		try {
			try {
				if (StringUtils.isNotBlank(transactionConfigurator.getRecoverConfig().getZookeeperNamespace())) {
					String[] zkServerNamespace = transactionConfigurator.getRecoverConfig().getZookeeperNamespace().split("/");
					lock = new InterProcessMutex(ZooKeeperHelper.getZKClient(zkServerNamespace[0], "TCC/" + zkServerNamespace[1], false), "/TransactionRecovery");
					if (!lock.acquire(800, TimeUnit.MILLISECONDS)) { // n秒内没有获取到锁, 就放弃
						logger.info("concurrent execution, exit.");
						return;
					}
				} else {
					logger.warn("The transaction recover task is not executed using a distributed lock.");
				}
			} catch (Exception e) {
				logger.error("zookeeper 同步锁异常", e);
			}
			List<Transaction> transactions = loadErrorTransactions();
			recoverErrorTransactions(transactions);
		} finally {
			try {
				if (null != lock) {
					lock.release();
				}
			} catch (Exception e2) {
				logger.error("", e2);
			}
		}

	}

	private List<Transaction> loadErrorTransactions() {

		long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();

		TransactionRepository transactionRepository = transactionConfigurator.getTransactionRepository();
		RecoverConfig recoverConfig = transactionConfigurator.getRecoverConfig();

		List<Transaction> transactions = transactionRepository.findAllUnmodifiedSince(new Date(currentTimeInMillis - recoverConfig.getRecoverDuration() * 1000));

		return transactions;
	}

	private void recoverErrorTransactions(List<Transaction> transactions) {
		final CountDownLatch countDownLatch = new CountDownLatch(transactions.size());

		for (final Transaction transaction : transactions) {
			recoveryExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if (transaction.getRetriedCount() > transactionConfigurator.getRecoverConfig().getMaxRetryCount()) {

							logger.error(String.format("recover failed with max retry count,will not try again. txid:%s, status:%s,retried count:%d,transaction content:%s",
									transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)));
							return;
						}

						if (transaction.getTransactionType().equals(TransactionType.BRANCH) && (transaction.getCreateTime().getTime()
								+ transactionConfigurator.getRecoverConfig().getMaxRetryCount() * transactionConfigurator.getRecoverConfig().getRecoverDuration() * 1000 > System
										.currentTimeMillis())) {
							return;
						}

						try {
							transaction.addRetriedCount();

							if (transaction.getStatus().equals(TransactionStatus.CONFIRMING)) {

								transaction.changeStatus(TransactionStatus.CONFIRMING);
								transactionConfigurator.getTransactionRepository().update(transaction);
								transaction.commit();
								transactionConfigurator.getTransactionRepository().delete(transaction);

							} else if (transaction.getStatus().equals(TransactionStatus.CANCELLING) || transaction.getTransactionType().equals(TransactionType.ROOT)) {

								transaction.changeStatus(TransactionStatus.CANCELLING);
								transactionConfigurator.getTransactionRepository().update(transaction);
								transaction.rollback();
								transactionConfigurator.getTransactionRepository().delete(transaction);
							}

						} catch (Throwable throwable) {

							if (throwable instanceof OptimisticLockException || ExceptionUtils.getRootCause(throwable) instanceof OptimisticLockException) {
								logger.warn(String.format("optimisticLockException happened while recover. txid:%s, status:%s,retried count:%d,transaction content:%s",
										transaction.getXid(), transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)), throwable);
							} else {
								logger.error(String.format("recover failed, txid:%s, status:%s,retried count:%d,transaction content:%s", transaction.getXid(),
										transaction.getStatus().getId(), transaction.getRetriedCount(), JSON.toJSONString(transaction)), throwable);
							}
						}
					} finally {
						countDownLatch.countDown();
					}

				}
			});
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			logger.error("", e);
		}
	}

	public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
		this.transactionConfigurator = transactionConfigurator;
	}
}
