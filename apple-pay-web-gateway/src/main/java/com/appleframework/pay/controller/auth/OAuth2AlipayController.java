package com.appleframework.pay.controller.auth;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;

import com.appleframework.pay.common.core.utils.StringUtil;
import com.appleframework.pay.controller.common.BaseController;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.entity.RpUserPayInfo;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;
import com.appleframework.pay.user.service.RpUserPayInfoService;
import com.appleframework.pay.utils.NetworkUtil;
import com.appleframework.pay.vo.AlipayOAuthParamVo;

@Controller
@RequestMapping(value = "/oauth2/alipay")
public class OAuth2AlipayController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AlipayController.class);

    @Autowired
    private RpUserPayInfoService rpUserPayInfoService;
    
    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;
    
    //@Autowired
    //private Validator validator;
    
    private String alipayReturnUrl = "http://gateway.pay.appleframework.com/web-gateway/scanPayNotify/notify/ALIPAY";
    
    private String alipayOauthUrl = "https://openauth.alipay.com/oauth2/appToAppAuth.htm?app_id={0}&redirect_uri={1}&state={2}";

    @RequestMapping(value = "/appToAppAuth")
    public String appToAppAuth(@Valid AlipayOAuthParamVo paramVo, BindingResult bindingResult, HttpServletRequest request, ModelMap modelMap) {
        //参数校验
        if (bindingResult.hasErrors()) {
            String errMsg = getErrorMsg(bindingResult);
            logger.info("小程序用户鉴权--请求参数异常：[{}]", errMsg);
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR, errMsg);
        }

        RpUserPayConfig userPayConfig = rpUserPayConfigService.getByPayKey(paramVo.getPayKey());
        if (userPayConfig == null) {
            throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
        }

        String merchantNo = userPayConfig.getUserNo();// 商户编号
        String appId = paramVo.getAppId();
        
        String payWayCode = "ALIPAY";
        RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(userPayConfig.getUserNo(), payWayCode);

        String productName = "鉴权产品";
        String orderIp = null;
        try {
            orderIp = NetworkUtil.getIpAddress(request);
        } catch (IOException e) {
            logger.info("获取Ip失败!");
        }
        if (StringUtil.isEmpty(orderIp)) {
            orderIp = "127.0.0.1";
        }
        
        String oauthUrl = MessageFormat.format(alipayOauthUrl, appId, alipayReturnUrl, merchantNo);  
        System.out.println(oauthUrl);
        return "oauth_alipay";
    }

    /**
     * 获取错误返回信息
     *
     * @param bindingResult
     * @return
     */
    public String getErrorMsg(BindingResult bindingResult) {
        StringBuffer sb = new StringBuffer();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            sb.append(objectError.getDefaultMessage()).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
    
    @RequestMapping(value = "/appAuthCode")
    public String appAuthCode(HttpServletRequest request, ModelMap modelMap) {
    	System.out.println(modelMap);
    	return null;
    }

}
