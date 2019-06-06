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

import com.appleframework.pay.common.core.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <b>功能说明:商户转账订单实体类</b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class RpTradeTransferOrder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商户订单编号 **/
    private String merchantOrderNo;
    
    /** 商户名称 **/
    private String merchantName;

    /** 商户编号 **/
    private String merchantNo;

    /** 转账名称 **/
    private String orderName;

    /** 订单金额 **/
    private BigDecimal orderAmount;
    
    /** 订单时间 **/
    private Date orderTime;

    /** 订单日期 **/
    private Date orderDate;

    /** 订单来源IP **/
    private String orderIp;
    
    /** 订单有效期 **/
    private Integer orderPeriod;

    /** 支付通道编号 **/
    private String payWayCode;

    /** 支付方式名称 **/
    private String payWayName;

    /** 交易业务类型 **/
    private String trxType;

    /** 支付流水 **/
    private String trxNo;

    /** 订单撤销原因 **/
    private String cancelReason;

    /** 订单到期时间 **/
    private Date expireTime;

    /** 备注 **/
    private String remark;

    /** 资金流出类型 **/
    private String fundOutType;
    
    /** 支付方式类型编码 **/
    private String payTypeCode;

    /** 支付方式类型名称 **/
    private String payTypeName;
    
    
    /** 付款方编号 **/
    private String payerUserNo;

    /** 付款方名称 **/
    private String payerName;

    /** 付款方账户类型 **/
    private String payerAccountType;
    
    /** 收款方编号 **/
    private String receiverUserNo;

    /** 收款方名称 **/
    private String receiverName;

    /** 收款方账户类型 **/
    private String receiverAccountType;

    
    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName == null ? null : orderName.trim();
    }

    public String getMerchantOrderNo() {
        return merchantOrderNo;
    }

    public void setMerchantOrderNo(String merchantOrderNo) {
        this.merchantOrderNo = merchantOrderNo == null ? null : merchantOrderNo.trim();
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }
    
    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName == null ? null : merchantName.trim();
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderIp() {
        return orderIp;
    }

    public void setOrderIp(String orderIp) {
        this.orderIp = orderIp == null ? null : orderIp.trim();
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? null : cancelReason.trim();
    }

    public Integer getOrderPeriod() {
        return orderPeriod;
    }

    public void setOrderPeriod(Integer orderPeriod) {
        this.orderPeriod = orderPeriod;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getPayWayCode() {
        return payWayCode;
    }

    public void setPayWayCode(String payWayCode) {
        this.payWayCode = payWayCode == null ? null : payWayCode.trim();
    }

    public String getPayWayName() {
        return payWayName;
    }

    public void setPayWayName(String payWayName) {
        this.payWayName = payWayName == null ? null : payWayName.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getTrxType() {
        return trxType;
    }

    public void setTrxType(String trxType) {
        this.trxType = trxType == null ? null : trxType.trim();
    }


    public String getFundOutType() {
        return fundOutType;
    }

    public void setFundOutType(String fundOutType) {
        this.fundOutType = fundOutType == null ? null : fundOutType.trim();
    }

    public String getTrxNo() {
        return trxNo;
    }

    public void setTrxNo(String trxNo) {
        this.trxNo = trxNo;
    }
    
    public String getPayTypeCode() {
        return payTypeCode;
    }

    public void setPayTypeCode(String payTypeCode) {
        this.payTypeCode = payTypeCode == null ? null : payTypeCode.trim();
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName == null ? null : payTypeName.trim();
    }
    
    public String getPayerUserNo() {
		return payerUserNo;
	}

	public void setPayerUserNo(String payerUserNo) {
		this.payerUserNo = payerUserNo;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	
	public String getPayerAccountType() {
		return payerAccountType;
	}

	public void setPayerAccountType(String payerAccountType) {
		this.payerAccountType = payerAccountType;
	}

	public String getReceiverUserNo() {
		return receiverUserNo;
	}

	public void setReceiverUserNo(String receiverUserNo) {
		this.receiverUserNo = receiverUserNo;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	
	public String getReceiverAccountType() {
		return receiverAccountType;
	}

	public void setReceiverAccountType(String receiverAccountType) {
		this.receiverAccountType = receiverAccountType;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(super.getId());
        sb.append(", version=").append(super.getVersion());
        sb.append(", createTime=").append(super.getCreateTime());
        sb.append(", editor=").append(super.getEditor());
        sb.append(", creater=").append(super.getCreater());
        sb.append(", editTime=").append(super.getEditTime());
        sb.append(", status=").append(super.getStatus());
        sb.append(", orderName=").append(orderName);
        sb.append(", merchantOrderNo=").append(merchantOrderNo);
        sb.append(", orderAmount=").append(orderAmount);
        sb.append(", merchantName=").append(merchantName);
        sb.append(", merchantNo=").append(merchantNo);
        sb.append(", orderTime=").append(orderTime);
        sb.append(", orderDate=").append(orderDate);
        sb.append(", orderIp=").append(orderIp);
        sb.append(", cancelReason=").append(cancelReason);
        sb.append(", orderPeriod=").append(orderPeriod);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", payWayCode=").append(payWayCode);
        sb.append(", payWayName=").append(payWayName);
        sb.append(", remark=").append(remark);
        sb.append(", trxType=").append(trxType);
        sb.append(", fundOutType=").append(fundOutType);
        sb.append(", trxNo=").append(trxNo);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}