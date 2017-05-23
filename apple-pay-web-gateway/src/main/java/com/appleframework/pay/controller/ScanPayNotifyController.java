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

/**
 * <b>功能说明:后台通知结果控制类
 * </b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.appleframework.pay.common.core.enums.PayWayEnum;
import com.appleframework.pay.common.core.utils.StringUtil;
import com.appleframework.pay.trade.service.RpTradePaymentManagerService;
import com.appleframework.pay.trade.utils.WeiXinPayUtils;
import com.appleframework.pay.trade.utils.alipay.util.AliPayUtil;
import com.appleframework.pay.trade.utils.alipay.util.ApplePayUtil;
import com.appleframework.pay.trade.vo.OrderPayResultVo;

@Controller
@RequestMapping(value = "/scanPayNotify")
public class ScanPayNotifyController {

    @Autowired
    private RpTradePaymentManagerService rpTradePaymentManagerService;

	@RequestMapping("/notify/{payWayCode}")
	public void notify(@PathVariable("payWayCode") String payWayCode, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		Map<String, String> notifyMap = new HashMap<String, String>();
		if (PayWayEnum.WEIXIN.name().equals(payWayCode)) {
			InputStream inputStream = httpServletRequest.getInputStream();// 从request中取得输入流
			notifyMap = WeiXinPayUtils.parseXml(inputStream);
		} else if (PayWayEnum.ALIPAY.name().equals(payWayCode)) {
			Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
			notifyMap = AliPayUtil.parseNotifyMsg(requestParams);
		} else if (PayWayEnum.APPLE.name().equals(payWayCode)) {
			Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
			notifyMap = ApplePayUtil.parseNotifyMsg(requestParams);
		}
		String completePayNotify = rpTradePaymentManagerService.completeScanPay(payWayCode, notifyMap);
		if (!StringUtil.isEmpty(completePayNotify)) {
			if (PayWayEnum.WEIXIN.name().equals(payWayCode)) {
				httpServletResponse.setContentType("text/xml");
			}
			httpServletResponse.getWriter().print(completePayNotify);
		}
	}

    @RequestMapping("/result/{payWayCode}")
    public String result(@PathVariable("payWayCode") String payWayCode, HttpServletRequest httpServletRequest , Model model) throws Exception {

        Map<String,String> resultMap = new HashMap<String,String>();
        Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            valueStr = new String(valueStr);
            resultMap.put(name, valueStr);
        }

        OrderPayResultVo scanPayByResult = rpTradePaymentManagerService.completeScanPayByResult(payWayCode, resultMap);
        model.addAttribute("scanPayByResult",scanPayByResult);

        return "PayResult";
    }

}
