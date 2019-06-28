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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.appleframework.pay.common.core.utils.DateUtils;
import com.appleframework.pay.controller.common.BaseController;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.trade.service.RpTradeTransferManagerService;
import com.appleframework.pay.trade.utils.MerchantApiUtil;
import com.appleframework.pay.trade.vo.TransferResultVo;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;

/**
 * <b>功能说明:转账控制类
 * </b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
@Controller
@RequestMapping(value = "/trade")
public class TradeTransferController extends BaseController {
	
    private static final Logger LOG = LoggerFactory.getLogger(TradeTransferController.class);

    @Autowired
    private RpTradeTransferManagerService rpTradeTransferManagerService;
    
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
    @RequestMapping(value = "/transfer")
    public @ResponseBody Object jsonPay(Model model){
        Map<String , Object> paramMap = new HashMap<String , Object>();
        
        
		/*
		 * \"out_biz_no\":\"3142321423432\"," + "\"payee_type\":\"ALIPAY_LOGONID\"," +
		 * "\"payee_account\":\"abc@sina.com\"," + "\"amount\":\"12.23\"," +
		 * "\"payer_show_name\":\"上海交通卡退款\"," + "\"payee_real_name\":\"张三\"," +
		 * "\"remark\":\"转账备注\"" +
		 */

        //获取商户传入参数
		String payKey = getString_UrlDecode_UTF8("payKey"); // 企业支付KEY
		paramMap.put("payKey", payKey);
		String orderNo = getString_UrlDecode_UTF8("orderNo"); // 转账订单编号
		paramMap.put("orderNo", orderNo);
		String orderAccount = getString_UrlDecode_UTF8("orderAccount"); // 转账账号
		paramMap.put("orderAccount", orderAccount);
		String orderName = getString_UrlDecode_UTF8("orderName"); // 转账条目备注
		paramMap.put("orderName", orderName);
		String realName = getString_UrlDecode_UTF8("realName"); // 转账账户名称
		paramMap.put("realName", realName);
		String payWayCode = getString_UrlDecode_UTF8("payWayCode"); // 支付方式编码 支付宝: ALIPAY 微信:WEIXIN
		paramMap.put("payWayCode", payWayCode);
		String orderIp = getString_UrlDecode_UTF8("orderIp"); // 转账下单IP
		paramMap.put("orderIp", orderIp);
		String orderPriceStr = getString_UrlDecode_UTF8("orderPrice"); // 转账订单金额 , 单位:元
		paramMap.put("orderPrice", orderPriceStr);
        String orderDateStr = getString_UrlDecode_UTF8("orderDate"); // 订单日期
        paramMap.put("orderDate",orderDateStr);
        String orderTimeStr = getString_UrlDecode_UTF8("orderTime"); // 订单时间
        paramMap.put("orderTime",orderTimeStr);
        String orderPeriodStr = getString_UrlDecode_UTF8("orderPeriod"); // 订单有效期
        paramMap.put("orderPeriod",orderPeriodStr);

		String remark = getString_UrlDecode_UTF8("remark"); // 支付备注
		paramMap.put("remark", remark);
		String sign = getString_UrlDecode_UTF8("sign"); // 签名

		Date orderDate = DateUtils.parseDate(orderDateStr, "yyyyMMdd");
		Date orderTime = DateUtils.parseDate(orderTimeStr, "yyyyMMddHHmmss");
		Integer orderPeriod = Integer.valueOf(orderPeriodStr);
        
		LOG.info("PAY请求参数:" + JSON.toJSONString(paramMap));
        
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
		if (rpUserPayConfig == null) {
			throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
		}

		if (!MerchantApiUtil.isRightSign(paramMap, rpUserPayConfig.getPaySecret(), sign)) {
            throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR,"订单签名异常");
        }

		String payeeAccount = orderAccount; 
		String payeeName = realName;
		BigDecimal orderPrice = BigDecimal.valueOf(Double.valueOf(orderPriceStr));
		TransferResultVo transferResultVo = rpTradeTransferManagerService.initDirectTransfer(
				payKey, payeeAccount, payeeName, orderName, orderNo, orderDate, orderTime, orderPrice, 
				payWayCode, orderIp, orderPeriod, remark);

//        LOG.info("PrePay:" + appPayResultVo.getPrePay());
//		LOG.info("PAY返回:" + JSON.toJSONString(appPayResultVo));
        return transferResultVo;
    }

}
