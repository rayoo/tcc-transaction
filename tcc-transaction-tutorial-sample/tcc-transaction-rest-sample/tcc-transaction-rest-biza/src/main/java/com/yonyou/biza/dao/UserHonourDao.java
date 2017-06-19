package com.yonyou.biza.dao;

import org.springframework.stereotype.Repository;

import com.yonyou.biza.entity.UserHonour;

@Repository
public interface UserHonourDao {

	public void update(UserHonour userHonour);

	public void delete(Long userId);

	public UserHonour findByUserId(long userId);

}
