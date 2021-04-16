package com.egzosn.pay.wx.api;

import static com.egzosn.pay.wx.api.WxConst.APPID;
import static com.egzosn.pay.wx.api.WxConst.HMACSHA256;
import static com.egzosn.pay.wx.api.WxConst.MCH_ID;
import static com.egzosn.pay.wx.api.WxConst.NONCE_STR;
import static com.egzosn.pay.wx.api.WxConst.RESULT_CODE;
import static com.egzosn.pay.wx.api.WxConst.RETURN_CODE;
import static com.egzosn.pay.wx.api.WxConst.RETURN_MSG_CODE;
import static com.egzosn.pay.wx.api.WxConst.SIGN;
import static com.egzosn.pay.wx.api.WxConst.SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.http.ClientHttpRequest;
import com.egzosn.pay.common.http.HttpStringEntity;
import com.egzosn.pay.common.util.Util;
import com.egzosn.pay.common.util.XML;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.egzosn.pay.wx.bean.WxPayError;
import com.egzosn.pay.wx.bean.WxProfitSharing;
import com.egzosn.pay.wx.bean.WxProfitSharingType;

/**
 * 微信支付服务
 *
 * @author egan
 * <pre>
 * email egzosn@gmail.com
 * date 2016-5-18 14:09:01
 * </pre>
 */
public class WxPayService2 extends WxPayService implements WxProfitsharingService {

	public WxPayService2(WxPayConfigStorage payConfigStorage) {
		super(payConfigStorage);
	}

	@Override
	public Map<String, Object> doProfitsharing(WxProfitSharing profitSharing) {
		 //分账
        Map<String, Object> parameters = getPublicParameters();
        parameters.put("out_order_no", profitSharing.getOutOrderNo());
        parameters.put("transaction_id", profitSharing.getTransactionId());
        
        List<Map<String, Object>> receivers = new ArrayList<>();
        Map<String, Object> receiver = new HashMap<>();
        receiver.put("type", profitSharing.getType());
        receiver.put("account", profitSharing.getAccount());
        receiver.put("amount", Util.conversionCentAmount(profitSharing.getAmount()));
        receiver.put("description", profitSharing.getDescription());
        receivers.add(receiver);
        
        parameters.put("receivers", receivers);

        setSign(parameters);

        String requestXML = XML.getMap2Xml(parameters);
        if (LOG.isDebugEnabled()) {
            LOG.debug("requestXML：" + requestXML);
        }

        HttpStringEntity entity = new HttpStringEntity(requestXML, ClientHttpRequest.APPLICATION_XML_UTF_8);

        //调起支付的参数列表
        JSONObject result = requestTemplate.postForObject(getReqUrl(WxProfitSharingType.ONE), entity, JSONObject.class);

        if (!SUCCESS.equals(result.get(RETURN_CODE)) || !SUCCESS.equals(result.get(RESULT_CODE))) {
            throw new PayErrorException(new WxPayError(result.getString(RESULT_CODE), result.getString(RETURN_MSG_CODE), result.toJSONString()));
        }
        return result;		
	}
	
	/**
     * 生成并设置签名
     *
     * @param parameters 请求参数
     * @return 请求参数
     */
    private Map<String, Object> setSign(Map<String, Object> parameters) {

        String signTypeStr = payConfigStorage.getSignType();
        if (HMACSHA256.equals(signTypeStr)) {
            signTypeStr = SignUtils.HMACSHA256.getName();
        }
        parameters.put("sign_type", signTypeStr);
        String sign = createSign(SignUtils.parameterText(parameters, "&", SIGN, "appId"), payConfigStorage.getInputCharset());
        parameters.put(SIGN, sign);
        return parameters;
    }


	/**
     * 获取公共参数
     *
     * @return 公共参数
     */
    private Map<String, Object> getPublicParameters() {
        Map<String, Object> parameters = new TreeMap<String, Object>();
        parameters.put(APPID, payConfigStorage.getAppId());
        parameters.put(MCH_ID, payConfigStorage.getMchId());
        //判断如果是服务商模式信息则加入
        setParameters(parameters, "sub_mch_id", payConfigStorage.getSubMchId());
        setParameters(parameters, "sub_appid", payConfigStorage.getSubAppId());
        parameters.put(NONCE_STR, SignUtils.randomStr());
        return parameters;
    }
   

}
