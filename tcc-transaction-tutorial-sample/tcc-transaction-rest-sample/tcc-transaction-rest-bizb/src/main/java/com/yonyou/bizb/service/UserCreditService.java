package com.yonyou.bizb.service;

import org.mengyun.tcctransaction.api.Idempotent;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonyou.bizb.dao.UserCreditDao;
import com.yonyou.bizb.entity.UserCredit;

@Service
public class UserCreditService {
	static final Logger logger = LoggerFactory.getLogger(UserCreditService.class);

	@Autowired
	UserCreditDao userCreditDao;

	// @Compensable(confirmMethod = "confirmDelUserCredit", cancelMethod = "cancelDelUserCredit", transactionContextEditor = MethodTransactionContextEditor.class)
	@Transactional
	public void delUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserCredit {}", transactionContext);
		UserCredit userCredit = new UserCredit();

		userCredit.setUserId(userId);
		userCredit.setDr(1);

		userCreditDao.update(userCredit);
	}

	@Idempotent
	@Transactional
	public void confirmDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> confirmDelUserCredit {}", transactionContext);
		UserCredit userCredit = userCreditDao.findByUserId(userId);
		if (null != userCredit && userCredit.getDr() == 1) {
			// int i = 1 / 0;
			userCreditDao.delete(userId);
		}
	}

	@Idempotent
	@Transactional
	public void cancelDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> cancelDelUserCredit {}", transactionContext);
		UserCredit userCredit = userCreditDao.findByUserId(userId);
		if (null != userCredit && userCredit.getDr() == 1) {
			userCredit.setDr(0);
			userCreditDao.update(userCredit);
		}
	}

}
