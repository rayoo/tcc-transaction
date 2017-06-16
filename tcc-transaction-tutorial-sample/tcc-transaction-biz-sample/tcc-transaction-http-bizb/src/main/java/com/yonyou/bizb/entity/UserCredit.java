package com.yonyou.bizb.entity;

import java.io.Serializable;

public class UserCredit implements Serializable {

	private static final long serialVersionUID = 8848682231246289185L;

	private long userId;

	private int creditCount;

	private int dr = 0;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getCreditCount() {
		return creditCount;
	}

	public void setCreditCount(int creditCount) {
		this.creditCount = creditCount;
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public UserCredit() {
		super();
	}

	public UserCredit(long userId, int creditCount, int dr) {
		super();
		this.userId = userId;
		this.creditCount = creditCount;
		this.dr = dr;
	}

}
