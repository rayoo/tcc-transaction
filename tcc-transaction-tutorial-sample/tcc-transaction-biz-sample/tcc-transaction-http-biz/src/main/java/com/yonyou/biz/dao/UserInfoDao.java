package com.yonyou.biz.dao;

import org.springframework.stereotype.Repository;

import com.yonyou.biz.entity.UserInfo;

@Repository
public interface UserInfoDao {

	public void update(UserInfo userInfo);

	public void delete(Long userId);

	public UserInfo findByUserId(long userId);

}
