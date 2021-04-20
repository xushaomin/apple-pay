package com.appleframework.pay.trade.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appleframework.config.core.PropertyConfigurer;
import com.appleframework.pay.common.core.enums.PayWayEnum;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.trade.service.RpTradeRedPackService;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.entity.RpUserPayInfo;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;
import com.appleframework.pay.user.service.RpUserPayInfoService;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayService2;
import com.egzosn.pay.wx.bean.RedpackOrder;
import com.egzosn.pay.wx.bean.WxSendredpackType;

@Service
public class RpTradeRedPackServiceImpl implements RpTradeRedPackService {
	
    private static final Logger LOG = LoggerFactory.getLogger(RpTradeRedPackServiceImpl.class);

    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;

    @Autowired
    private RpUserPayInfoService rpUserPayInfoService;    
        
    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private static AtomicInteger count = new AtomicInteger(1000);
    
    private String wishing = "恭喜！你获得了【{0}】的月榜奖金";

	@Override
	public void doSend(String payKey, PayWayEnum payWay, String sendTo, BigDecimal amount) {
		
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
        if (rpUserPayConfig == null){
            throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR,"用户支付配置有误");
        }		
        
        if (PayWayEnum.WEIXIN.name().equals(payWay.name())){//微信支付
        	
			// 根据资金流向获取配置信息
			RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(rpUserPayConfig.getUserNo(), payWay.name());
			String appid = rpUserPayInfo.getAppId();
			String mch_id = rpUserPayInfo.getMerchantId();
			String partnerKey = rpUserPayInfo.getPartnerKey();
			        	
        	WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
    		wxPayConfigStorage.setMchId(mch_id);
    		wxPayConfigStorage.setAppId(appid);
    		//wxPayConfigStorage.setKeyPublic("转账公钥，转账时必填");
    		//wxPayConfigStorage.setSecretKey(partnerKey);
    		wxPayConfigStorage.setKeyPrivate(partnerKey);
    		wxPayConfigStorage.setNotifyUrl(PropertyConfigurer.getString("weixinpay.notify_url"));
    		wxPayConfigStorage.setReturnUrl(PropertyConfigurer.getString("weixinpay.notify_url"));
    		wxPayConfigStorage.setSignType("MD5");
    		wxPayConfigStorage.setInputCharset("utf-8");
    		
    		HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
    	    httpConfigStorage.setKeystore(rpUserPayInfo.getRsaPrivateKey());
    	    httpConfigStorage.setStorePassword(mch_id);
    	    
            httpConfigStorage.setCertStoreType(CertStoreType.PATH);
	        //支付服务
    		WxPayService2 service =  new WxPayService2(wxPayConfigStorage, httpConfigStorage);
    		
    		int id = count.incrementAndGet();
    		if(id > 9999) {
    			id = 1000;
    			count.set(id);
    		}
    		String mchBillno = mch_id + dateFormat.format(new Date()) + id;
    		RedpackOrder redpackOrder = new RedpackOrder();
			//String subject, String body, BigDecimal price, String outTradeNo
    		redpackOrder.setActName("月榜");
    		redpackOrder.setWishing(MessageFormat.format(wishing, rpUserPayConfig.getRemark()));
    		redpackOrder.setReOpenid(sendTo);
    		redpackOrder.setSendName(rpUserPayInfo.getUserName());
    		redpackOrder.setRemark("月榜");
    		redpackOrder.setTotalNum(1);
    		redpackOrder.setTotalAmount(amount);
    		redpackOrder.setMchBillno(mchBillno);
    		redpackOrder.setTransferType(WxSendredpackType.SENDMINIPROGRAMHB);
	        
	        try {
	        	Map<String, Object> map = service.sendredpack(redpackOrder);
	        	Object resultCode = map.get("result_code");
	        	if(resultCode.equals("FAIL")) {
	        		throw new TradeBizException(TradeBizException.TRADE_PRSH_ERROR, map.get("err_code_des") + ""); 
	        	}
			} catch (PayErrorException e) {
				LOG.error(e.getMessage());
				throw new TradeBizException(TradeBizException.TRADE_PRSH_ERROR, e.getPayError().getErrorMsg()); 
			}

        }
        
	}
	
	
	

}
