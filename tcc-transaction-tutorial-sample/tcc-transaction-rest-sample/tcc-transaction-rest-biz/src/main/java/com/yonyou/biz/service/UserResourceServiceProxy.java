package com.yonyou.biz.service;

import java.util.List;

import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Idempotent;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yonyou.biz.utils.http.HttpClient4Utils;
import com.yonyou.biz.utils.http.HttpResult;
import com.yonyou.biz.utils.json.JsonUtils;

@Service
public class UserResourceServiceProxy {
	private static final String OK = "ok";
	private static final String HONOUR_SERVER_URL = "http://localhost:10010/user/honour";
	private static final String CREDIT_SERVER_URL = "http://localhost:10020/user/credit";

	static final Logger logger = LoggerFactory.getLogger(UserResourceServiceProxy.class);

	@Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "delUserHonour", cancelMethod = "delUserHonour", transactionContextEditor = MethodTransactionContextEditor.class)
	@Idempotent
	@Transactional
	public void delUserHonour(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserHonour");
		// userHonourService.delUserHonour(transactionContext, userId);
		String url = HONOUR_SERVER_URL + "/del/" + userId;
		requestServer(transactionContext, url);
	}

	public void requestServer(TransactionContext transactionContext, String url) {
		List<NameValuePair> paramsPair = Lists.newArrayList();
		paramsPair.add(new BasicNameValuePair("tctx", JsonUtils.toJson(transactionContext)));
		HttpResult ret = HttpClient4Utils.postForm(url, paramsPair, null);
		if (!OK.equalsIgnoreCase(ret.getResponseString())) {
			throw new RuntimeException("调用" + url + "异常,返回字符串：" + JsonUtils.toJson(ret));
		}
	}

	@Compensable(propagation = Propagation.SUPPORTS, confirmMethod = "confirmDelUserCredit", cancelMethod = "cancelDelUserCredit", transactionContextEditor = MethodTransactionContextEditor.class)
	public void delUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> delUserCredit");
		// userCreditService.delUserCredit(transactionContext, userId);
		String url = CREDIT_SERVER_URL + "/del/try/" + userId;
		requestServer(transactionContext, url);
	}

	@Idempotent
	@Transactional
	public void confirmDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> confirmDelUserCredit");
		// userCreditService.confirmDelUserCredit(transactionContext, userId);
		String url = CREDIT_SERVER_URL + "/del/confirm/" + userId;
		requestServer(transactionContext, url);
	}

	@Idempotent
	@Transactional
	public void cancelDelUserCredit(TransactionContext transactionContext, long userId) {
		logger.info(">> cancelDelUserCredit");
		// userCreditService.cancelDelUserCredit(transactionContext, userId);
		String url = CREDIT_SERVER_URL + "/del/cancel/" + userId;
		requestServer(transactionContext, url);
	}

}
