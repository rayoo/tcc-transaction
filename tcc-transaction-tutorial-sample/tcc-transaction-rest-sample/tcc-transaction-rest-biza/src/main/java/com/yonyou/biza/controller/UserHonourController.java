package com.yonyou.biza.controller;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yonyou.biza.service.UserHonourService;
import com.yonyou.biza.utils.JsonUtils;

@Controller
@RequestMapping("")
public class UserHonourController {

	private static final String OK = "ok";

	@Autowired
	UserHonourService userHonourService;

	@RequestMapping(value = "/user/honour/del/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public String delUser(String tctx, @PathVariable long userId) {
		TransactionContext transactionContext = JsonUtils.readJson(tctx, TransactionContext.class);
		userHonourService.delUserHonour(transactionContext, userId);
		return OK;
	}

}
