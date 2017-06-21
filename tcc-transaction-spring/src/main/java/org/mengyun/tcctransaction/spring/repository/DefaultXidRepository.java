package org.mengyun.tcctransaction.spring.repository;

import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.XidRepository;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认空实现.
 * 
 * @author rayoo
 */
public class DefaultXidRepository implements XidRepository {
	private static final Logger logger = LoggerFactory.getLogger(DefaultXidRepository.class);

	public static final DefaultXidRepository INSTANCE = new DefaultXidRepository();

	@Override
	public int createXid(TransactionContext tctx, String methodName) {
		return 0;
	}

	@Override
	public int deleteXid(Transaction transaction) {
		return 0;
	}

	public DefaultXidRepository() {
		logger.warn("No spring bean XidRepository defined, using the DefaultXidRepository.");
	}

}
