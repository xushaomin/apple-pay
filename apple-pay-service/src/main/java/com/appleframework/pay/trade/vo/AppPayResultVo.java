/*
 * Copyright 2015-2102 RonCoo(http://www.appleframework.com) Group.
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
import java.util.Map;

/**
 * <b>功能说明:扫码支付结果展示实体
 * </b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class AppPayResultVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * 预付相关信息
     */
	private Map<String, String> prePay;
	
	/**
     * 支付方式编码
     */
    private String payWayCode;

    /** 金额 **/
    private BigDecimal orderAmount;

    /** 产品名称 **/
    private String productName;

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

	public Map<String, String> getPrePay() {
		return prePay;
	}

	public void setPrePay(Map<String, String> prePay) {
		this.prePay = prePay;
	}
    
}
