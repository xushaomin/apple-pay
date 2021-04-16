package com.appleframework.pay.trade.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appleframework.config.core.PropertyConfigurer;
import com.appleframework.pay.common.core.enums.PayWayEnum;
import com.appleframework.pay.trade.dao.RpTradePaymentOrderDao;
import com.appleframework.pay.trade.dao.RpTradePaymentRecordDao;
import com.appleframework.pay.trade.entity.RpTradePaymentOrder;
import com.appleframework.pay.trade.entity.RpTradePaymentRecord;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.trade.service.RpTradeProfitsharingService;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.entity.RpUserPayInfo;
import com.appleframework.pay.user.enums.FundInfoTypeEnum;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.RpUserPayConfigService;
import com.appleframework.pay.user.service.RpUserPayInfoService;
import com.egzosn.pay.wx.api.WxConst;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayService2;
import com.egzosn.pay.wx.bean.WxProfitSharing;

@Service
public class RpTradeProfitsharingServiceImpl implements RpTradeProfitsharingService {
	
    @Autowired
    private RpTradePaymentOrderDao rpTradePaymentOrderDao;

    @Autowired
    private RpTradePaymentRecordDao rpTradePaymentRecordDao;

    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;

    @Autowired
    private RpUserPayInfoService rpUserPayInfoService;
    
	@Override
	public Map<String, Object> doSharing(String payKey, String merchantOrderNo, BigDecimal amount) {
		
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
        	
			String appid = "";
			String mch_id = "";
			String partnerKey = "";
			if (FundInfoTypeEnum.MERCHANT_RECEIVES.name().equals(rpTradePaymentOrder.getFundIntoType())) {// 商户收款
				// 根据资金流向获取配置信息
				RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(rpTradePaymentOrder.getMerchantNo(),
						payWayCode);
				appid = rpUserPayInfo.getAppId();
				mch_id = rpUserPayInfo.getMerchantId();
				partnerKey = rpUserPayInfo.getPartnerKey();
			} else if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(rpTradePaymentOrder.getFundIntoType())) {// 平台收款
				appid = PropertyConfigurer.getString("weixinpay.appId");
				mch_id = PropertyConfigurer.getString("weixinpay.mch_id");
				partnerKey = PropertyConfigurer.getString("weixinpay.partnerKey");
			}
			
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
    		
    		RpUserPayInfo subUserPayInfo = rpUserPayInfoService.getByUserNo(subMerchantId, payWayCode);
    		if(null != subUserPayInfo) {
    			if(null != subUserPayInfo.getMerchantId()) {
    				wxPayConfigStorage.setSubMchId(subUserPayInfo.getMerchantId());
    			}
    		}
    		
	        //支付服务
    		WxPayService2 service =  new WxPayService2(wxPayConfigStorage);
	        
			// 支付订单基础信息
			//String subject, String body, BigDecimal price, String outTradeNo
	        WxProfitSharing profitSharing = new WxProfitSharing();
	        profitSharing.setOutOrderNo(rpTradePaymentRecord.getBankOrderNo());
	        profitSharing.setTransactionId(rpTradePaymentRecord.getBankTrxNo());
	        profitSharing.setAccount(mch_id);
	        profitSharing.setAmount(amount);
	        profitSharing.setType("MERCHANT_ID");
	        profitSharing.setDescription("嘚啷分账");
	        return service.doProfitsharing(profitSharing);

        }
        
        
		return null;
	}

}
