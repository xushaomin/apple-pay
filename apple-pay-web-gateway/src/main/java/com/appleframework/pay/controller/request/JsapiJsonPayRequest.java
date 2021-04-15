package com.appleframework.pay.controller.request;

import com.gitee.easyopen.doc.annotation.ApiDocField;

public class JsapiJsonPayRequest {

	@ApiDocField(name = "productName", description = "商品名称", example = "嘚啷")
	private String productName;

	@ApiDocField(name = "orderNo", description = "订单编号", example = "0.1617537002314")
	private String orderNo;

	@ApiDocField(name = "orderPrice", description = "订单金额 , 单位:元", example = "0.1")
	private String orderPrice;

	@ApiDocField(name = "payWayCode", description = "支付方式编码 支付宝: ALIPAY  微信:WEIXIN", example = "WEIXIN")
	private String payWayCode;

	@ApiDocField(name = "orderIp", description = "下单IP", example = "192.168.1.13")
	private String orderIp;

	@ApiDocField(name = "orderDate", description = "订单日期", example = "20210404")
	private String orderDate;

	@ApiDocField(name = "orderTime", description = "订单时间", example = "20210404195002")
	private String orderTime;

	@ApiDocField(name = "orderPeriod", description = "订单有效期", example = "5")
	private String orderPeriod;

	@ApiDocField(name = "returnUrl", description = "页面通知返回url", example = "http://www.leyye.com")
	private String returnUrl;

	@ApiDocField(name = "notifyUrl", description = "后台消息通知Url", example = "http://114.55.105.126:8002/pay/callback")
	private String notifyUrl;

	@ApiDocField(name = "remark", description = "支付备注", example = "test")
	private String remark;

	@ApiDocField(name = "field1", description = "扩展字段1", example = "1")
	private String field1;

	@ApiDocField(name = "field2", description = "扩展字段2", example = "1")
	private String field2;

	@ApiDocField(name = "field3", description = "扩展字段3", example = "%7B%22operateType%22%3A0%2C%22userId%22%3A0%2C%22businessTypeId%22%3A2%7D")
	private String field3;

	@ApiDocField(name = "field4", description = "扩展字段4", example = "1")
	private String field4;

	@ApiDocField(name = "field5", description = "扩展字段5", example = "o9kHBwu8h5DPPXsjW1_cwFvPO2hA")
	private String field5;

	@ApiDocField(name = "subMerchantNo", description = "子商户号", example = "1607855620")
	private String subMerchantNo;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(String orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getPayWayCode() {
		return payWayCode;
	}

	public void setPayWayCode(String payWayCode) {
		this.payWayCode = payWayCode;
	}

	public String getOrderIp() {
		return orderIp;
	}

	public void setOrderIp(String orderIp) {
		this.orderIp = orderIp;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getOrderPeriod() {
		return orderPeriod;
	}

	public void setOrderPeriod(String orderPeriod) {
		this.orderPeriod = orderPeriod;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getSubMerchantNo() {
		return subMerchantNo;
	}

	public void setSubMerchantNo(String subMerchantNo) {
		this.subMerchantNo = subMerchantNo;
	}

}