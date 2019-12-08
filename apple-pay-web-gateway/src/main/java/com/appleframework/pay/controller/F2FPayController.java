package com.appleframework.pay.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.appleframework.pay.common.core.utils.DateUtils;
import com.appleframework.pay.controller.common.BaseController;
import com.appleframework.pay.service.CnpPayService;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.trade.model.OrderPayBo;
import com.appleframework.pay.trade.service.RpTradePaymentManagerService;
import com.appleframework.pay.trade.utils.MerchantApiUtil;
import com.appleframework.pay.trade.vo.F2FPayResultVo;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;


/**
 * <b>功能说明:扫码支付控制类
 * </b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
@Controller
@RequestMapping(value = "/f2fPay")
public class F2FPayController extends BaseController {
	
    private static final Logger LOG = LoggerFactory.getLogger(F2FPayController.class) ;

    @Autowired
    private RpTradePaymentManagerService rpTradePaymentManagerService;

    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;

    @Autowired
    private CnpPayService cnpPayService;

    /**
     * 条码支付,商户通过前置设备获取到用户支付授权码后,请求支付网关支付.
     *
     * @return
     */
    @RequestMapping("/doPay")
    public void initPay(HttpServletResponse httpServletResponse , HttpServletRequest httpServletRequest) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        //获取商户传入参数
        String payKey = getString_UrlDecode_UTF8("payKey"); // 企业支付KEY
        paramMap.put("payKey", payKey);
        String authCode = getString_UrlDecode_UTF8("authCode"); // 企业支付KEY
        paramMap.put("authCode", authCode);
        String productName = getString_UrlDecode_UTF8("productName"); // 商品名称
        paramMap.put("productName", productName);
        String orderNo = getString_UrlDecode_UTF8("orderNo"); // 订单编号
        paramMap.put("orderNo", orderNo);
        String orderPriceStr = getString_UrlDecode_UTF8("orderPrice"); // 订单金额 , 单位:元
        paramMap.put("orderPrice", orderPriceStr);
        String payWayCode = getString_UrlDecode_UTF8("payWayCode"); // 支付方式编码 支付宝: ALIPAY  微信:WEIXIN
        paramMap.put("payWayCode", payWayCode);
        String orderIp = getString_UrlDecode_UTF8("orderIp"); // 下单IP
        paramMap.put("orderIp", orderIp);
        String orderDateStr = getString_UrlDecode_UTF8("orderDate"); // 订单日期
        paramMap.put("orderDate", orderDateStr);
        String orderTimeStr = getString_UrlDecode_UTF8("orderTime"); // 订单日期
        paramMap.put("orderTime", orderTimeStr);
        String remark = getString_UrlDecode_UTF8("remark"); // 支付备注
        paramMap.put("remark", remark);
        String sign = getString_UrlDecode_UTF8("sign"); // 签名

        String field1 = getString_UrlDecode_UTF8("field1"); // 扩展字段1
        paramMap.put("field1", field1);
        String field2 = getString_UrlDecode_UTF8("field2"); // 扩展字段2
        paramMap.put("field2", field2);
        String field3 = getString_UrlDecode_UTF8("field3"); // 扩展字段3
        paramMap.put("field3", field3);
        String field4 = getString_UrlDecode_UTF8("field4"); // 扩展字段4
        paramMap.put("field4", field4);
        String field5 = getString_UrlDecode_UTF8("field5"); // 扩展字段5
        paramMap.put("field5", field5);

        Date orderDate = DateUtils.parseDate(orderDateStr, "yyyyMMdd");
        Date orderTime = DateUtils.parseDate(orderTimeStr, "yyyyMMddHHmmss");

        RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
        if (rpUserPayConfig == null) {
            throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
        }

        cnpPayService.checkIp( rpUserPayConfig ,  httpServletRequest);//ip校验

        if (!MerchantApiUtil.isRightSign(paramMap, rpUserPayConfig.getPaySecret(), sign)) {
            throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "订单签名异常");
        }

        BigDecimal orderPrice = BigDecimal.valueOf(Double.valueOf(orderPriceStr));
        
		OrderPayBo orderPayBo = changToOrderBo(productName, orderNo, orderDate, orderTime, orderPrice, 
		    	 orderIp, null, null, null, remark, 
		    	 field1, field2, field3, field4, field5);

        F2FPayResultVo f2FPayResultVo = rpTradePaymentManagerService.f2fPay(payKey, authCode, payWayCode, orderPayBo);

        String payResultJson = JSONObject.toJSONString(f2FPayResultVo);

        LOG.debug("支付结果==>{}" ,payResultJson);
        httpServletResponse.setContentType("text/text;charset=UTF-8");
        write(httpServletResponse, payResultJson);

    }

}
