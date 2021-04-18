package com.appleframework.pay.trade.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appleframework.config.core.PropertyConfigurer;
import com.appleframework.pay.common.core.enums.PayWayEnum;
import com.appleframework.pay.trade.dao.RpTradePaymentOrderDao;
import com.appleframework.pay.trade.dao.RpTradePaymentRecordDao;
import com.appleframework.pay.trade.entity.RpTradePaymentOrder;
import com.appleframework.pay.trade.entity.RpTradePaymentRecord;
import com.appleframework.pay.trade.entity.RpTradeProfitsSharingReceiver;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.trade.service.RpTradeProfitsSharingReceiverService;
import com.appleframework.pay.trade.service.RpTradeProfitsharingService;
import com.appleframework.pay.trade.utils.MD5Util;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.entity.RpUserPayInfo;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;
import com.appleframework.pay.user.service.RpUserPayInfoService;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.wx.api.WxConst;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayService2;
import com.egzosn.pay.wx.bean.WxAddReceiver;
import com.egzosn.pay.wx.bean.WxProfitSharing;

@Service
public class RpTradeProfitsharingServiceImpl implements RpTradeProfitsharingService {
	
    private static final Logger LOG = LoggerFactory.getLogger(RpTradeProfitsharingServiceImpl.class);

    @Autowired
    private RpTradePaymentOrderDao rpTradePaymentOrderDao;

    @Autowired
    private RpTradePaymentRecordDao rpTradePaymentRecordDao;

    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;

    @Autowired
    private RpUserPayInfoService rpUserPayInfoService;
    
    @Autowired
    private RpTradeProfitsSharingReceiverService rpTradeProfitsSharingReceiverService;
    
	@Override
	public void doSharing(String payKey, String merchantOrderNo, BigDecimal amount) {
		
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
        if (rpUserPayConfig == null){
            throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR,"用户支付配置有误");
        }
		
        String merchantNo = rpUserPayConfig.getUserNo();//商户编号
        RpTradePaymentOrder rpTradePaymentOrder = rpTradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(merchantNo, merchantOrderNo);

        if (rpTradePaymentOrder == null){
        	throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR,"订单不存在,不能分账");
        }
        
        RpTradePaymentRecord rpTradePaymentRecord = rpTradePaymentRecordDao.getSuccessRecordByMerchantNoAndMerchantOrderNo(merchantNo, merchantOrderNo);
        if (rpTradePaymentRecord == null){
        	throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR,"交易记录不存在或未支付成功,不能分账");
        }
        
        BigDecimal totalFee = rpTradePaymentOrder.getOrderAmount();

        if(totalFee.subtract(amount).doubleValue() < 0) {
        	throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "分账的金额不能大于交易金额");
        }
        
        String payWayCode = rpTradePaymentRecord.getPayWayCode();//支付方式
        if (PayWayEnum.WEIXIN.name().equals(payWayCode)){//微信支付
        	
			// 根据资金流向获取配置信息
			RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(rpTradePaymentOrder.getMerchantNo(), payWayCode);
			String appid = rpUserPayInfo.getAppId();
			String mch_id = rpUserPayInfo.getMerchantId();
			String partnerKey = rpUserPayInfo.getPartnerKey();
			
            String subMerchantId = rpTradePaymentRecord.getSubMerchantNo();
        	
        	WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
    		wxPayConfigStorage.setMchId(mch_id);
    		wxPayConfigStorage.setAppId(appid);
    		//wxPayConfigStorage.setKeyPublic("转账公钥，转账时必填");
    		//wxPayConfigStorage.setSecretKey(partnerKey);
    		wxPayConfigStorage.setKeyPrivate(partnerKey);
    		wxPayConfigStorage.setNotifyUrl(PropertyConfigurer.getString("weixinpay.notify_url"));
    		wxPayConfigStorage.setReturnUrl(PropertyConfigurer.getString("weixinpay.notify_url"));
    		wxPayConfigStorage.setSignType(WxConst.HMACSHA256);
    		wxPayConfigStorage.setInputCharset("utf-8");
    		
    	    HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
    	    httpConfigStorage.setKeystore(rpUserPayInfo.getRsaPrivateKey());
    	    httpConfigStorage.setStorePassword(mch_id);
    	    
            httpConfigStorage.setCertStoreType(CertStoreType.PATH);

    		
    		RpUserPayInfo subUserPayInfo = rpUserPayInfoService.getByUserNo(subMerchantId, payWayCode);
    		if(null != subUserPayInfo) {
    			if(null != subUserPayInfo.getMerchantId()) {
    				wxPayConfigStorage.setSubMchId(subUserPayInfo.getMerchantId());
    			}
    		}
    		
	        //支付服务
    		WxPayService2 service =  new WxPayService2(wxPayConfigStorage, httpConfigStorage);
	        
			// 支付订单基础信息
			//String subject, String body, BigDecimal price, String outTradeNo
	        WxProfitSharing profitSharing = new WxProfitSharing();
	        profitSharing.setOutOrderNo(rpTradePaymentRecord.getBankOrderNo());
	        profitSharing.setTransactionId(rpTradePaymentRecord.getBankTrxNo());
	        profitSharing.setAccount(mch_id);
	        profitSharing.setAmount(amount);
	        profitSharing.setType("MERCHANT_ID");
	        profitSharing.setDescription("嘚啷分账");
	        
	        try {
		        service.doProfitsharing(profitSharing);
			} catch (PayErrorException e) {
				throw new TradeBizException(TradeBizException.TRADE_PRSH_ERROR, e.getPayError().getErrorMsg()); 
			}

        }
        
	}
	
	private String success = "SUCCESS";

	@Override
	public void addReceiver(String payKey, String subMerchantNo, PayWayEnum payWay) {
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
        if (rpUserPayConfig == null){
            throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR,"用户支付配置有误");
        }
        if (PayWayEnum.WEIXIN.equals(payWay)){//微信支付
        	
			RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(rpUserPayConfig.getUserNo(), payWay.name());
    		RpUserPayInfo subUserPayInfo = rpUserPayInfoService.getByUserNo(subMerchantNo, payWay.name());

			String appid = rpUserPayInfo.getAppId();
			String mch_id = rpUserPayInfo.getMerchantId();
			String sub_mch_id = null;
			String partnerKey = rpUserPayInfo.getPartnerKey();
			
			String md5 = this.getMD5(payWay.name(), mch_id, subMerchantNo, mch_id);
			if(this.isExist(md5) ) {
				throw new TradeBizException(TradeBizException.TRADE_PRSH_ERROR, "分账帐号已经添加"); 
			}
			
    		if(null != subUserPayInfo) {
    			if(null != subUserPayInfo.getMerchantId()) {
    				sub_mch_id = subUserPayInfo.getMerchantId();
    			}
    		}
			
			try {
		        addReceiver(payKey, subMerchantNo, PayWayEnum.WEIXIN);
			} catch (TradeBizException e) {
				LOG.info(e.getMessage());
			}
			        	
        	WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
    		wxPayConfigStorage.setMchId(mch_id);
    		wxPayConfigStorage.setAppId(appid);
    		//wxPayConfigStorage.setKeyPublic("转账公钥，转账时必填");
    		//wxPayConfigStorage.setSecretKey(partnerKey);
    		wxPayConfigStorage.setKeyPrivate(partnerKey);
    		wxPayConfigStorage.setNotifyUrl(PropertyConfigurer.getString("weixinpay.notify_url"));
    		wxPayConfigStorage.setReturnUrl(PropertyConfigurer.getString("weixinpay.notify_url"));
    		wxPayConfigStorage.setSignType(WxConst.HMACSHA256);
    		wxPayConfigStorage.setInputCharset("utf-8");
    		
    	    HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
    	    httpConfigStorage.setKeystore(rpUserPayInfo.getRsaPrivateKey());
    	    httpConfigStorage.setStorePassword(mch_id);
    	    
            httpConfigStorage.setCertStoreType(CertStoreType.PATH);
            
            if(null != sub_mch_id) {
    			wxPayConfigStorage.setSubMchId(sub_mch_id);    			
    		}
    		
	        //支付服务
    		WxPayService2 service =  new WxPayService2(wxPayConfigStorage, httpConfigStorage);
	        
			// 支付订单基础信息
			//String subject, String body, BigDecimal price, String outTradeNo
	        WxAddReceiver addReceiver = new WxAddReceiver();
	        addReceiver.setAccount(mch_id);
	        addReceiver.setName(rpUserPayConfig.getRemark());
	        addReceiver.setType("MERCHANT_ID");
	        addReceiver.setRelationType("SERVICE_PROVIDER");
	        Map<String, Object> result = service.addReceiver(addReceiver);
	        
	        if(success.equals(result.get("return_code")) && success.equals(result.get("return_code"))) {
		        RpTradeProfitsSharingReceiver receiver = new RpTradeProfitsSharingReceiver();
		        receiver.setUserName(rpUserPayInfo.getUserName());
		        receiver.setUserNo(mch_id);
		        receiver.setPayWayCode(payWay.name());
		        receiver.setPayWayName(payWay.getDesc());
		        receiver.setSubUserNo(subMerchantNo);
		        receiver.setSubUserName(subUserPayInfo.getUserName());
		        
		        receiver.setPsAccount(mch_id);
		        receiver.setPsRelationType("SERVICE_PROVIDER");
		        receiver.setPsType("MERCHANT_ID");
		        receiver.setPsName(rpUserPayConfig.getRemark());
		        receiver.setContentMd5(md5);
		        
		        rpTradeProfitsSharingReceiverService.addReceiver(receiver);

	        }

        }
	}
	
	private boolean isExist(String md5) {
		if(null == rpTradeProfitsSharingReceiverService.getByMD5(md5)) {
			return false;
		}
		return true;
	}
	
	private String getMD5(String payWayCode, String merchantNo, String subMerchantNo, String account) {
		String key = payWayCode + merchantNo + subMerchantNo + subMerchantNo;
		return MD5Util.encode(key);
	}

}
