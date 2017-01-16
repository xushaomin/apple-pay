package com.appleframework.pay.trade.utils.apple;

import java.io.Serializable;

public class AppleVerifyBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer status;
	private String environment;
	private AppleReceiptBean receipt;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public AppleReceiptBean getReceipt() {
		return receipt;
	}

	public void setReceipt(AppleReceiptBean receipt) {
		this.receipt = receipt;
	}

}