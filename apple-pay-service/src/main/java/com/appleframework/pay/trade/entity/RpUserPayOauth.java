package com.appleframework.pay.trade.entity;

import java.io.Serializable;

import com.appleframework.pay.common.core.entity.BaseEntity;

public class RpUserPayOauth extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * app类型
	 */
	private String appType;

	/**
	 * APP_ID
	 */
	private String appId;

	/**
	 * 商户ID
	 */
	private String merchantId;

	/**
	 * 用户NO
	 */
	private String userNo;

	/**
	 * authToken
	 */
	private String authToken;

	/**
	 * refreshToken
	 */
	private String refreshToken;

	/**
	 * expiresIn
	 */
	private Integer expiresIn;

	/**
	 * expiresIn
	 */
	private Integer reExpiresIn;

	/**
	 * payUserid
	 */
	private String payUserid;

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

	public Integer getReExpiresIn() {
		return reExpiresIn;
	}

	public void setReExpiresIn(Integer reExpiresIn) {
		this.reExpiresIn = reExpiresIn;
	}

	public String getPayUserid() {
		return payUserid;
	}

	public void setPayUserid(String payUserid) {
		this.payUserid = payUserid;
	}

}