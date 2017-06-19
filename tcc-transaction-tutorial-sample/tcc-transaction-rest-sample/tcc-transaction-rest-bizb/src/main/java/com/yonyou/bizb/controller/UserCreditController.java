package com.yonyou.bizb.controller;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yonyou.bizb.service.UserCreditService;
import com.yonyou.bizb.utils.JsonUtils;

@Controller
@RequestMapping("")
public class UserCreditController {
	private static final String OK = "ok";

	@Autowired
	UserCreditService userCreditService;

	@RequestMapping(value = "/user/credit/del/try/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public String delUser(String tctx, @PathVariable long userId) {
		TransactionContext transactionContext = JsonUtils.readJson(tctx, TransactionContext.class);
		userCreditService.delUserCredit(transactionContext, userId);
		return OK;
	}

	@RequestMapping(value = "/user/credit/del/confirm/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public String confirmDelUserCredit(String tctx, @PathVariable long userId) {
		TransactionContext transactionContext = JsonUtils.readJson(tctx, TransactionContext.class);
		userCreditService.confirmDelUserCredit(transactionContext, userId);
		return OK;
	}

	@RequestMapping(value = "/user/credit/del/cancel/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public String cancelDelUserCredit(String tctx, @PathVariable long userId) {
		TransactionContext transactionContext = JsonUtils.readJson(tctx, TransactionContext.class);
		userCreditService.cancelDelUserCredit(transactionContext, userId);
		return OK;
	}

}
