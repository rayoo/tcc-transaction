package com.yonyou.biz.utils.http;

/**
 * 连接类型
 */
public enum EnumConntionType {
	SHORT("SHORT", "短连接"), LONG("LONG", "长连接");

	private String code;
	private String name;

	private EnumConntionType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static EnumConntionType getEnum(String value) {
		for (EnumConntionType e : EnumConntionType.values()) {
			if (value.equals(e.getCode())) {
				return e;
			}
		}
		return null;
	}

}