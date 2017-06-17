package org.mengyun.tcctransaction.interceptor;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.repository.JdbcXidRepository;
import org.mengyun.tcctransaction.utils.CompensableMethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rayoo
 */
public class IdempotentInterceptor {

	static final Logger logger = LoggerFactory.getLogger(IdempotentInterceptor.class.getSimpleName());

	private JdbcXidRepository jdbcXidRepository;

	public void setJdbcXidRepository(JdbcXidRepository jdbcXidRepository) {
		this.jdbcXidRepository = jdbcXidRepository;
	}

	public Object interceptIdempotentMethod(ProceedingJoinPoint pjp) throws Throwable {
		Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

		int position = CompensableMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());

		if (position == -1) {
			throw new RuntimeException("no parameter TransactionContext found.");
		}

		TransactionContext transactionContext = (TransactionContext) pjp.getArgs()[position];

		TransactionStatus transactionStatus = TransactionStatus.valueOf(transactionContext.getStatus());

		logger.info("interceptor idempotent method, method:{}, transactionContext:{}", method.getName(), transactionContext);

		if (transactionStatus == TransactionStatus.TRYING) { // try 阶段不做拦截
			return pjp.proceed();
		}

		int xidRet = jdbcXidRepository.createXid(transactionContext, method.getName());
		if (xidRet == -1) {
			logger.error("repetitive execution, method:{}, transactionStatus:{}", method.getName(), transactionStatus.toString());
			return null;
		}
		return pjp.proceed();
	}

}
