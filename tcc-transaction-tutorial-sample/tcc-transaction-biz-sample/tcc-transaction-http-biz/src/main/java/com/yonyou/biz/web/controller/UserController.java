package com.yonyou.biz.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.yonyou.biz.entity.UserInfo;
import com.yonyou.biz.service.UserService;

/**
 * Created by changming.xie on 4/1/16.
 */
@Controller
@RequestMapping("")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView mv = new ModelAndView("/index");

		List<UserInfo> lst = userService.findAll();

		mv.addObject("userLst", lst);

		return mv;
	}

	@RequestMapping(value = "/del/user/{userId}", method = RequestMethod.GET)
	public ModelAndView delUser(@PathVariable long userId) {
		userService.delUserInfo(null, userId);

		ModelAndView mv = new ModelAndView("success");

		UserInfo userInfo = userService.findByUserId(userId);

		mv.addObject("result", (null == userInfo) ? "成功" : "失败");

		return mv;
	}

}
