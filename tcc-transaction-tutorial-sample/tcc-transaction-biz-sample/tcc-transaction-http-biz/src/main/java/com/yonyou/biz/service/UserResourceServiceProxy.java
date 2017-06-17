package com.yonyou.biz.service;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Idempotent;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonyou.biza.api.UserHonourService;
import com.yonyou.bizb.api.UserCreditService;

@Service
public class UserResourceServiceProxy {
	static final Logger logger = LoggerFactory.getLogger(UserResourceServiceProxy.class);

	@Autowired
	UserHonourService userHonourService;

	@Autowired
	UserCreditService userCreditService;

	@Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "delUserHonour", cancelMethod = "delUserHonour", transactionContextEditor = MethodTransactionContextEditor.class)
	public void delUserHonour(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserHonour");
		userHonourService.delUserHonour(transactionContext, userId);
	}

	// @Idempotent
	// @Transactional
	// public void confirmDelUserHonour(TransactionContext transactionContext, long userId) {
	// logger.info(">> confirmDelUserHonour");
	// userHonourService.confirmDelUserHonour(transactionContext, userId);
	// }
	//
	// @Idempotent
	// @Transactional
	// public void cancelDelUserHonour(TransactionContext transactionContext, long userId) {
	// logger.info(">> cancelDelUserHonour");
	// userHonourService.cancelDelUserHonour(transactionContext, userId);
	// }

	@Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "confirmDelUserCredit", cancelMethod = "cancelDelUserCredit", transactionContextEditor = MethodTransactionContextEditor.class)
	public void delUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserCredit");
		userCreditService.delUserCredit(transactionContext, userId);
	}

	@Idempotent
	@Transactional
	public void confirmDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> confirmDelUserCredit");
		userCreditService.confirmDelUserCredit(transactionContext, userId);
	}

	@Idempotent
	@Transactional
	public void cancelDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> cancelDelUserCredit");
		userCreditService.cancelDelUserCredit(transactionContext, userId);
	}

}
