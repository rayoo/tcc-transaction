package com.yonyou.biz.utils.http;

public class HttpResult {

	private int statusCode;

	private String responseString;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public HttpResult() {
	}

	public HttpResult(int statusCode, String responseString) {
		this.statusCode = statusCode;
		this.responseString = responseString;
	}

	public boolean isOK() {
		return 200 == getStatusCode();
	}

}
