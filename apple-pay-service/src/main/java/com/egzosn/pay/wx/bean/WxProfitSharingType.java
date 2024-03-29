package com.egzosn.pay.wx.bean;

import com.egzosn.pay.common.bean.TransactionType;

public enum WxProfitSharingType implements TransactionType {
	
	/**
	 * 单一分账
	 */
	PROFITSHARING("secapi/pay/profitsharing"),
	ADDRECEIVER("pay/profitsharingaddreceiver");

	WxProfitSharingType(String method) {
		this.method = method;
	}

	private String method;

	@Override
	public String getType() {
		return this.name();
	}

	@Override
	public String getMethod() {
		return this.method;
	}

}
