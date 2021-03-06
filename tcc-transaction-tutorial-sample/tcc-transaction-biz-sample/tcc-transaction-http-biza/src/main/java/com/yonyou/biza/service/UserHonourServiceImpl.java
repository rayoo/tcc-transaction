package com.yonyou.biza.service;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Idempotent;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.yonyou.biza.api.UserHonourService;
import com.yonyou.biza.dao.UserHonourDao;
import com.yonyou.biza.entity.UserHonour;

//@Service
public class UserHonourServiceImpl implements UserHonourService {
	static final Logger logger = LoggerFactory.getLogger(UserHonourServiceImpl.class);

	@Autowired
	UserHonourDao userHonourDao;

	@Override
	@Compensable(confirmMethod = "confirmDelUserHonour", cancelMethod = "cancelDelUserHonour", transactionContextEditor = MethodTransactionContextEditor.class)
	@Transactional
	public void delUserHonour(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserHonour {}", transactionContext);
		UserHonour userHonour = new UserHonour();

		userHonour.setUserId(userId);
		userHonour.setDr(1);

		userHonourDao.update(userHonour);
	}

	@Idempotent
	@Transactional
	public void confirmDelUserHonour(TransactionContext transactionContext, long userId) {
		logger.info(">> confirmDelUserHonour {}", transactionContext);
		// int i = 1 / 0;
		UserHonour userHonour = userHonourDao.findByUserId(userId);
		if (null != userHonour && userHonour.getDr() == 1) {
			userHonourDao.delete(userId);
		}
	}

	@Idempotent
	@Transactional
	public void cancelDelUserHonour(TransactionContext transactionContext, long userId) {
		logger.info(">> cancelDelUserHonour {}", transactionContext);
		UserHonour userHonour = userHonourDao.findByUserId(userId);
		if (null != userHonour && userHonour.getDr() == 1) {
			userHonour.setDr(0);
			userHonourDao.update(userHonour);
		}
	}

}
