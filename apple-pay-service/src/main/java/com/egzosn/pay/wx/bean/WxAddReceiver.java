package com.egzosn.pay.wx.bean;

/**
 * 分账账号
 */
public class WxAddReceiver {

	private String type; // 分账接收方类型 MERCHANT_ID, PERSONAL_OPENID

	private String account; // 分账接收方帐号

	private String name; // 分账接收方全称

	private String relationType; // 与分账方的关系类型

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

}
