package org.mengyun.tcctransaction.server.vo;

public class IdempotentVo {

	private String domain;

	private String gtxid;

	private String btxid;

	private String method;

	private Long createTime;

	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getGtxid() {
		return gtxid;
	}

	public void setGtxid(String gtxid) {
		this.gtxid = gtxid;
	}

	public String getBtxid() {
		return btxid;
	}

	public void setBtxid(String btxid) {
		this.btxid = btxid;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public IdempotentVo() {
		super();
	}

	public IdempotentVo(String domain, String gtxid, String btxid, String method, Long createTime) {
		super();
		this.domain = domain;
		this.gtxid = gtxid;
		this.btxid = btxid;
		this.method = method;
		this.createTime = createTime;
	}

}
