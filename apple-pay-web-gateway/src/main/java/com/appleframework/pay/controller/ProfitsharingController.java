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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.appleframework.pay.common.core.enums.PayWayEnum;
import com.appleframework.pay.controller.common.BaseController;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.trade.service.RpTradeProfitsharingService;
import com.appleframework.pay.trade.utils.MerchantApiUtil;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;

/**
 * <b>功能说明:分账控制类
 * </b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
@Controller
@RequestMapping(value = "/profit")
public class ProfitsharingController extends BaseController {
	
    private static final Logger LOG = LoggerFactory.getLogger(ProfitsharingController.class);

    @Autowired
    private RpTradeProfitsharingService rpTradeProfitsharingService;
    
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
    @RequestMapping(value = "/sharing")
    public @ResponseBody Object jsonPay(Model model){
        Map<String , Object> paramMap = new HashMap<String , Object>();

        //获取商户传入参数
        String payKey = getString_UrlDecode_UTF8("payKey"); // 企业支付KEY
        paramMap.put("payKey",payKey);
        String orderNo = getString_UrlDecode_UTF8("orderNo"); // 订单编号
        paramMap.put("orderNo", orderNo);
        
        String amountStr = getString_UrlDecode_UTF8("amount"); // 分账金额
        paramMap.put("amount", amountStr);
        
        String sign = getString_UrlDecode_UTF8("sign"); // 签名
                
		LOG.info("PAY请求参数:" + JSON.toJSONString(paramMap));
        
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
		if (rpUserPayConfig == null) {
			throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
		}

		if (!MerchantApiUtil.isRightSign(paramMap, rpUserPayConfig.getPaySecret(), sign)) {
            throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR,"订单签名异常");
        }

		BigDecimal amount = BigDecimal.valueOf(Double.valueOf(amountStr));
		
        rpTradeProfitsharingService.doSharing(payKey, orderNo, amount);
        //LOG.info("PrePay:" + appPayResultVo.getPrePay());
		//LOG.info("PAY返回:" + JSON.toJSONString(appPayResultVo));
        return "SUCCESS";
    }
    
    @RequestMapping(value = "/addreceiver")
    public @ResponseBody Object addreceiver(Model model){
        Map<String , Object> paramMap = new HashMap<String , Object>();

        //获取商户传入参数
        String payKey = getString_UrlDecode_UTF8("payKey"); // 企业支付KEY
        paramMap.put("payKey",payKey);
        String subMerchantNo = getString_UrlDecode_UTF8("subMerchantNo"); // 分账帐号
        paramMap.put("subMerchantNo", subMerchantNo);
                
        String sign = getString_UrlDecode_UTF8("sign"); // 签名
                
		LOG.info("PAY请求参数:" + JSON.toJSONString(paramMap));
        
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
		if (rpUserPayConfig == null) {
			throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
		}

		if (!MerchantApiUtil.isRightSign(paramMap, rpUserPayConfig.getPaySecret(), sign)) {
            throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR,"订单签名异常");
        }
		
		try {
	        rpTradeProfitsharingService.addReceiver(payKey, subMerchantNo, PayWayEnum.WEIXIN);
		} catch (TradeBizException e) {
			LOG.info(e.getMessage());
		}
        //LOG.info("PrePay:" + appPayResultVo.getPrePay());
		//LOG.info("PAY返回:" + JSON.toJSONString(appPayResultVo));
        return "SUCCESS";
    }

}
