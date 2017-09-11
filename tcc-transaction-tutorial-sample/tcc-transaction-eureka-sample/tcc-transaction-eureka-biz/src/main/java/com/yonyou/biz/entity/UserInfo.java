package com.yonyou.biz.entity;

import java.io.Serializable;

/**
 * @author rayoo
 *
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = -4350383101451476129L;

	private long userId;

	private String userName;

	private int dr;

	public long getUserId() {
		return userId;
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserInfo() {
		super();
	}

	public UserInfo(long userId, String userName) {
		super();
		this.userId = userId;
		this.userName = userName;
	}

}
