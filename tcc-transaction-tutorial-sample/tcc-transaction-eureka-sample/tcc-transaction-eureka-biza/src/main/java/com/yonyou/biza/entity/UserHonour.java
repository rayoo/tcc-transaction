package com.yonyou.biza.entity;

import java.io.Serializable;

public class UserHonour implements Serializable {

	private static final long serialVersionUID = 8848682231246289185L;

	private long userId;

	private int honourCount;

	private int dr = 0;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getHonourCount() {
		return honourCount;
	}

	public void setHonourCount(int honourCount) {
		this.honourCount = honourCount;
	}

	public int getDr() {
		return dr;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public UserHonour() {
		super();
	}

	public UserHonour(long userId, int honourCount, int dr) {
		super();
		this.userId = userId;
		this.honourCount = honourCount;
		this.dr = dr;
	}

}
