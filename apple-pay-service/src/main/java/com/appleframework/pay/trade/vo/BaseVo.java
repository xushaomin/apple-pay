package com.appleframework.pay.trade.vo;

import java.io.Serializable;

public class BaseVo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer code = 0;

	private String message = "SUCCESS";

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
