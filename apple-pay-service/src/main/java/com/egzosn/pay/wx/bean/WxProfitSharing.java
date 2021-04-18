package com.egzosn.pay.wx.bean;

import java.math.BigDecimal;

/**
 * 分账
 */
public class WxProfitSharing {

	private String transactionId; // 微信支付订单号

	private String outOrderNo; // 商户分账单号

	private String type; // MERCHANT_ID, PERSONAL_OPENID

	private String account;

	private BigDecimal amount;

	private String description;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOutOrderNo() {
		return outOrderNo;
	}

	public void setOutOrderNo(String outOrderNo) {
		this.outOrderNo = outOrderNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
