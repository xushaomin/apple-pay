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
package com.appleframework.pay.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.appleframework.pay.common.core.utils.DateUtils;
import com.appleframework.pay.controller.common.BaseController;
import com.appleframework.pay.controller.request.JsapiJsonPayRequest;
import com.appleframework.pay.trade.model.OrderPayBo;
import com.appleframework.pay.trade.service.RpTradePaymentManagerService;
import com.appleframework.pay.trade.vo.AppPayResultVo;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;
import com.gitee.easyopen.annotation.Api;
import com.gitee.easyopen.annotation.ApiService;
import com.gitee.easyopen.doc.annotation.ApiDoc;

/**
 * <b>功能说明:扫码支付控制类
 * </b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
@ApiService
@ApiDoc("JSAPI支付")
public class JsapiPayApi extends BaseController {
	
    private static final Logger LOG = LoggerFactory.getLogger(JsapiPayApi.class);

    @Autowired
    private RpTradePaymentManagerService rpTradePaymentManagerService;
    
    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;

    /**
     * 扫码支付,预支付页面
     * 用户进行扫码支付时,商户后台调用该接口
     * 支付平台根据商户传入的参数是否包含支付通道,决定需要跳转的页面
     * 1:传入支付通道参数,跳转到相应的支付通道扫码页面
     * 2:未传入支付通道参数,跳转到
     * @return
     */
	@Api(name = "/jsapiPay/jsonPay", isIgnoreToken = true)
    public @ResponseBody Object jsonPay(JsapiJsonPayRequest request){
        Map<String , Object> paramMap = new HashMap<String , Object>();

        //获取商户传入参数
        String payKey = request.getPayKey(); // 企业支付KEY
        String productName = request.getProductName(); // 商品名称
        String orderNo = request.getOrderNo(); // 订单编号
        String orderPriceStr = request.getOrderPrice(); // 订单金额 , 单位:元
        String payWayCode = request.getPayWayCode(); // 支付方式编码 支付宝: ALIPAY  微信:WEIXIN
        String orderIp = request.getOrderIp(); // 下单IP
        String orderDateStr = request.getOrderDate(); // 订单日期
        String orderTimeStr = request.getOrderTime(); // 订单日期
        String orderPeriodStr = request.getOrderPeriod(); // 订单有效期
        String returnUrl = request.getReturnUrl(); // 页面通知返回url
        String notifyUrl = request.getNotifyUrl(); // 后台消息通知Url
        String remark = request.getRemark(); // 支付备注

        String field1 = request.getField1(); // 扩展字段1
        String field2 = request.getField2(); // 扩展字段2
        String field3 = request.getField3(); // 扩展字段3
        String field4 = request.getField4(); // 扩展字段4
        String field5 = request.getField5(); // 扩展字段5
        
        String subMerchantNo = request.getSubMerchantNo(); // 扩展字段5

		Date orderDate = DateUtils.parseDate(orderDateStr, "yyyyMMdd");
		Date orderTime = DateUtils.parseDate(orderTimeStr, "yyyyMMddHHmmss");
		Integer orderPeriod = Integer.valueOf(orderPeriodStr);
        
		LOG.info("PAY请求参数:" + JSON.toJSONString(paramMap));
        
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
		if (rpUserPayConfig == null) {
			throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
		}

		BigDecimal orderPrice = BigDecimal.valueOf(Double.valueOf(orderPriceStr));
		
		OrderPayBo orderPayBo = changToOrderBo(productName, orderNo, orderDate, orderTime, orderPrice, 
		    	 orderIp, orderPeriod, returnUrl, notifyUrl, remark, 
		    	 field1, field2, field3, field4, field5, subMerchantNo);
		
        AppPayResultVo appPayResultVo = rpTradePaymentManagerService.initDirectJsapiPay(payKey, payWayCode, orderPayBo);

        LOG.info("PrePay:" + appPayResultVo.getPrePay());
		LOG.info("PAY返回:" + JSON.toJSONString(appPayResultVo));
        return appPayResultVo;
    }

}
