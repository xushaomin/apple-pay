/*
 * Copyright 2016-2102 Appleframework(http://www.appleframework.com) Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appleframework.pay.trade.entity;

import java.io.Serializable;

import com.appleframework.pay.common.core.entity.BaseEntity;

/**
 * <b>功能说明:分账实体类</b>
 * 
 * @author Cruise.Xu <a href=
 *         "http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class RpTradeProfitsSharingReceiver extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 服务商名称 **/
	private String userName;

	/** 服务商编号 **/
	private String userNo;

	/** 子商户名称 **/
	private String subUserName;

	/** 子商户编号 **/
	private String subUserNo;

	/** 支付通道编码 **/
	private String payWayCode;

	/** 支付通道名称 **/
	private String payWayName;

	private String psType; // 分账接收方类型 MERCHANT_ID, PERSONAL_OPENID

	private String psAccount; // 分账接收方帐号

	private String psName; // 分账接收方全称

	private String psRelationType; // 与分账方的关系类型
	
	private String contentMd5;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getSubUserName() {
		return subUserName;
	}

	public void setSubUserName(String subUserName) {
		this.subUserName = subUserName;
	}

	public String getSubUserNo() {
		return subUserNo;
	}

	public void setSubUserNo(String subUserNo) {
		this.subUserNo = subUserNo;
	}

	public String getPayWayCode() {
		return payWayCode;
	}

	public void setPayWayCode(String payWayCode) {
		this.payWayCode = payWayCode;
	}

	public String getPayWayName() {
		return payWayName;
	}

	public void setPayWayName(String payWayName) {
		this.payWayName = payWayName;
	}

	public String getPsType() {
		return psType;
	}

	public void setPsType(String psType) {
		this.psType = psType;
	}

	public String getPsAccount() {
		return psAccount;
	}

	public void setPsAccount(String psAccount) {
		this.psAccount = psAccount;
	}

	public String getPsName() {
		return psName;
	}

	public void setPsName(String psName) {
		this.psName = psName;
	}

	public String getPsRelationType() {
		return psRelationType;
	}

	public void setPsRelationType(String psRelationType) {
		this.psRelationType = psRelationType;
	}

	public String getContentMd5() {
		return contentMd5;
	}

	public void setContentMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}

}