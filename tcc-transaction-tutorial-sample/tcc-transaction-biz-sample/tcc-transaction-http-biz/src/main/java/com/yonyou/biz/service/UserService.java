package com.yonyou.biz.service;

import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Idempotent;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonyou.biz.dao.UserInfoDao;
import com.yonyou.biz.entity.UserInfo;

@Service
public class UserService {
	static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserInfoDao userInfoDao;

	@Autowired
	UserResourceServiceProxy userResServiceProxy;

	@Transactional
	@Compensable(confirmMethod = "confirmDelUserInfo", cancelMethod = "cancelDelUserInfo", transactionContextEditor = MethodTransactionContextEditor.class)
	public void delUserInfo(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserInfo");
		UserInfo userInfo = new UserInfo();

		userInfo.setUserId(userId);
		userInfo.setDr(1);

		userInfoDao.update(userInfo);

		userResServiceProxy.delUserHonour(null, userId);
		userResServiceProxy.delUserCredit(null, userId);
	}

	@Transactional
	@Idempotent
	public void confirmDelUserInfo(TransactionContext transactionContext, long userId) {
		logger.info(">> confirmDelUserInfo");
		UserInfo userInfo = userInfoDao.findByUserId(userId);
		if (null != userInfo && userInfo.getDr() == 1) {
			// int i = 1 / 0;
			userInfoDao.delete(userId);
		}
	}

	@Transactional
	@Idempotent
	public void cancelDelUserInfo(TransactionContext transactionContext, long userId) {
		logger.info(">> cancelDelUserInfo");
		UserInfo userInfo = userInfoDao.findByUserId(userId);
		if (null != userInfo && userInfo.getDr() == 1) {
			userInfo.setDr(0);
			userInfoDao.update(userInfo);
		}
	}

	public UserInfo findByUserId(long userId) {
		return userInfoDao.findByUserId(userId);
	}

	// @Compensable
	// public void delAllUserInfo(long userId) {
	// delUserInfo(null, userId);
	// userHonourService.delUserHonour(null, userId);
	// userCreditService.delUserCredit(null, userId);
	// }

}
