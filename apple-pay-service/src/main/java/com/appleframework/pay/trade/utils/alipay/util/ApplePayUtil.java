package com.appleframework.pay.trade.utils.alipay.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.appleframework.pay.trade.utils.apple.AppleInAppBean;
import com.appleframework.pay.trade.utils.apple.AppleReceiptBean;
import com.appleframework.pay.trade.utils.apple.AppleVerifyBean;

/**
 * @功能说明:
 * @创建者: Peter
 * @创建时间: 16/6/14 下午3:43
 * @公司名称:深圳市果苹网络科技有限公司(http://www.appleframework.com)
 * @版本:V1.0
 */
public class ApplePayUtil {

	public static Map<String, String> parseNotifyMsg(Map<String, String[]> requestParams) {
		Map<String, String> params = new HashMap<String, String>();
		for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		return params;
	}

	public static AppleVerifyBean parseJsonToBean(String jsonString) {
		return JSONObject.parseObject(jsonString, AppleVerifyBean.class);
	}
	
	public static String getTransactionId(AppleReceiptBean receipt) {
		List<AppleInAppBean> list = receipt.getIn_app();
		if(list.size() > 0) {
			return list.get(0).getTransaction_id();
		}
		return null;
	}

}
