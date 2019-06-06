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
package com.appleframework.pay.trade.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <b>功能说明:扫码支付结果展示实体 </b>
 * 
 * @author Cruise.Xu <a href=
 *         "http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class TransferResultVo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 交易流水
	 */
	private String txNo;
	
	/**
	 * 支付订单号
	 */
	private String orderNo;

	/**
	 * 支付方式编码
	 */
	private String payWayCode;

	/** 金额 **/
	private BigDecimal orderAmount;

	/** 产品名称 **/
	private String orderName;

	public String getPayWayCode() {
		return payWayCode;
	}

	public void setPayWayCode(String payWayCode) {
		this.payWayCode = payWayCode;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getTxNo() {
		return txNo;
	}

	public void setTxNo(String txNo) {
		this.txNo = txNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

}
