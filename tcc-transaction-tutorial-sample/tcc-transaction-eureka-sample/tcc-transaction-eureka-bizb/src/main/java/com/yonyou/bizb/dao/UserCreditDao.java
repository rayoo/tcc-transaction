package com.yonyou.bizb.dao;

import org.springframework.stereotype.Repository;

import com.yonyou.bizb.entity.UserCredit;

@Repository
public interface UserCreditDao {

	public void update(UserCredit userCredit);

	public void delete(Long userId);

	public UserCredit findByUserId(long userId);

}
