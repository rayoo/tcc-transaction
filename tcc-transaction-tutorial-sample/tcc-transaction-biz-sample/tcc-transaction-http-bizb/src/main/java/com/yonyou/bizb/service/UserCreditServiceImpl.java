package com.yonyou.bizb.service;

import org.mengyun.tcctransaction.api.Idempotent;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yonyou.bizb.api.UserCreditService;
import com.yonyou.bizb.dao.UserCreditDao;
import com.yonyou.bizb.entity.UserCredit;

//@Service
public class UserCreditServiceImpl implements UserCreditService {
	static final Logger logger = LoggerFactory.getLogger(UserCreditServiceImpl.class);

	@Autowired
	UserCreditDao userCreditDao;

	@Override
	// @Compensable(confirmMethod = "confirmDelUserCredit", cancelMethod = "cancelDelUserCredit", transactionContextEditor = MethodTransactionContextEditor.class)
	@Transactional
	public void delUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserCredit {}", JSON.toJSON(transactionContext));
		UserCredit userCredit = new UserCredit();

		userCredit.setUserId(userId);
		userCredit.setDr(1);

		userCreditDao.update(userCredit);
	}

	@Idempotent
	@Transactional
	public void confirmDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> confirmDelUserCredit {}", JSON.toJSON(transactionContext));
		UserCredit userCredit = userCreditDao.findByUserId(userId);
		if (null != userCredit && userCredit.getDr() == 1) {
			// int i = 1 / 0;
			userCreditDao.delete(userId);
		}
	}

	@Idempotent
	@Transactional
	public void cancelDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> cancelDelUserCredit {}", JSON.toJSON(transactionContext));
		UserCredit userCredit = userCreditDao.findByUserId(userId);
		if (null != userCredit && userCredit.getDr() == 1) {
			userCredit.setDr(0);
			userCreditDao.update(userCredit);
		}
	}

}
