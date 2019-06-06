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
package com.appleframework.pay.trade.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.appleframework.config.core.PropertyConfigurer;
import com.appleframework.pay.account.enums.AccountTypeEnum;
import com.appleframework.pay.account.service.RpAccountTransactionService;
import com.appleframework.pay.common.core.enums.PayTypeEnum;
import com.appleframework.pay.common.core.enums.PayWayEnum;
import com.appleframework.pay.common.core.utils.DateUtils;
import com.appleframework.pay.common.core.utils.StringUtil;
import com.appleframework.pay.trade.dao.RpTradePaymentRecordDao;
import com.appleframework.pay.trade.dao.RpTradeTransferOrderDao;
import com.appleframework.pay.trade.entity.RpTradePaymentRecord;
import com.appleframework.pay.trade.entity.RpTradeTransferOrder;
import com.appleframework.pay.trade.entity.weixinpay.WeiXinPrePay;
import com.appleframework.pay.trade.enums.TradeStatusEnum;
import com.appleframework.pay.trade.enums.TrxTypeEnum;
import com.appleframework.pay.trade.enums.alipay.AliPayTradeStateEnum;
import com.appleframework.pay.trade.enums.weixinpay.WeiXinTradeTypeEnum;
import com.appleframework.pay.trade.enums.weixinpay.WeixinTradeStateEnum;
import com.appleframework.pay.trade.exception.TradeBizException;
import com.appleframework.pay.trade.service.RpTradeTransferManagerService;
import com.appleframework.pay.trade.utils.WeiXinPayUtils;
import com.appleframework.pay.trade.utils.alipay.config.AlipayConfigUtil;
import com.appleframework.pay.trade.utils.alipay.util.AlipayNotify;
import com.appleframework.pay.trade.utils.alipay.util.ApplePayNotify;
import com.appleframework.pay.trade.utils.alipay.util.ApplePayUtil;
import com.appleframework.pay.trade.utils.apple.AppleReceiptBean;
import com.appleframework.pay.trade.utils.apple.AppleVerifyBean;
import com.appleframework.pay.trade.vo.TransferResultVo;
import com.appleframework.pay.user.entity.RpPayWay;
import com.appleframework.pay.user.entity.RpUserInfo;
import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.entity.RpUserPayInfo;
import com.appleframework.pay.user.enums.FundInfoTypeEnum;
import com.appleframework.pay.user.exception.UserBizException;
import com.appleframework.pay.user.service.BuildNoService;
import com.appleframework.pay.user.service.RpPayWayService;
import com.appleframework.pay.user.service.RpUserInfoService;
import com.appleframework.pay.user.service.RpUserPayConfigService;
import com.appleframework.pay.user.service.RpUserPayInfoService;
import com.taobao.diamond.utils.JSONUtils;

/**
 * <b>功能说明:交易模块管理实现类实现</b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
@Service("rpTradeTransferManagerService")
public class RpTradeTransferManagerServiceImpl implements RpTradeTransferManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(RpTradeTransferManagerServiceImpl.class);

    @Autowired
    private RpTradeTransferOrderDao rpTradeTransferOrderDao;

    @Autowired
    private RpTradePaymentRecordDao rpTradePaymentRecordDao;

    @Autowired
    private RpUserInfoService rpUserInfoService;

    @Autowired
    private RpUserPayInfoService rpUserPayInfoService;

    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;

    @Autowired
    private RpPayWayService rpPayWayService;

    @Autowired
    private BuildNoService buildNoService;

    @Autowired
    private RpAccountTransactionService rpAccountTransactionService;

    /**
     * 初始化直连扫码支付数据,直连扫码支付初始化方法规则
     * 1:根据(商户编号 + 商户订单号)确定订单是否存在
     * 1.1:如果订单存在,抛异常,提示订单已存在
     * 1.2:如果订单不存在,创建支付订单
     * 2:创建支付记录
     * 3:根据相应渠道方法
     * 4:调转到相应支付渠道扫码界面
     *
     * @param payKey  商户支付KEY
     * @param payeeAccount  收款方账号
     * @param orderName 产品名称
     * @param orderNo     商户订单号
     * @param orderDate   下单日期
     * @param orderTime   下单时间
     * @param orderPrice  订单金额(元)
     * @param payWayCode      支付方式编码
     * @param orderIp     下单IP
     * @param orderPeriod 订单有效期(分钟)
     * @param remark      支付备注
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
	public TransferResultVo initDirectTransfer(String payKey, String payeeAccount, String payeeName, String orderName, String orderNo, Date orderDate,
			Date orderTime, BigDecimal orderPrice, String payWayCode, String orderIp, Integer orderPeriod,
			String remark) {

        RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(payKey);
        if (rpUserPayConfig == null){
            throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR,"用户支付配置有误");
        }

        //根据支付产品及支付方式获取费率
        RpPayWay payWay = null;
        PayTypeEnum payType = null;
        if (PayWayEnum.WEIXIN.name().equals(payWayCode)){
            payWay = rpPayWayService.getByPayWayTypeCode(rpUserPayConfig.getProductCode(), payWayCode, PayTypeEnum.WX_TRANSFER.name());
            payType = PayTypeEnum.WX_TRANSFER;
        }else if (PayWayEnum.ALIPAY.name().equals(payWayCode)){
            payWay = rpPayWayService.getByPayWayTypeCode(rpUserPayConfig.getProductCode(), payWayCode, PayTypeEnum.ALI_TRANSFER.name());
            payType = PayTypeEnum.ALI_TRANSFER;
        }

        if(payWay == null){
            throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR,"用户支付配置有误");
        }

        String merchantNo = rpUserPayConfig.getUserNo();//商户编号

        RpUserInfo rpUserInfo = rpUserInfoService.getDataByMerchentNo(merchantNo);
        if (rpUserInfo == null){
            throw new UserBizException(UserBizException.USER_IS_NULL,"用户不存在");
        }

        RpTradeTransferOrder rpTradeTransferOrder = rpTradeTransferOrderDao.selectByMerchantNoAndMerchantOrderNo(merchantNo, orderNo);
        if (rpTradeTransferOrder == null){
			rpTradeTransferOrder = sealRpTradeTransferOrder(payeeAccount, payeeName,
					merchantNo, rpUserInfo.getUserName(), orderName, orderNo,
					orderDate, orderTime, orderPrice, payWayCode, PayWayEnum.getEnum(payWayCode).getDesc(), payType,
					rpUserPayConfig.getFundIntoType(), orderIp, orderPeriod, remark);
            rpTradeTransferOrderDao.insert(rpTradeTransferOrder);
		} else {
			if (TradeStatusEnum.SUCCESS.name().equals(rpTradeTransferOrder.getStatus())) {
				throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "订单已支付成功,无需重复支付");
			}

			if (rpTradeTransferOrder.getOrderAmount().compareTo(orderPrice) != 0) {
				rpTradeTransferOrder.setOrderAmount(orderPrice);// 如果金额不一致,修改金额为最新的金额
			}
		}
        return getTransferResultVo(rpTradeTransferOrder, payWay);
    }

    /**
     * 支付成功方法
     * @param rpTradePaymentRecord
     */
	private void completeSuccessOrder(RpTradePaymentRecord rpTradePaymentRecord, String bankTrxNo, Date timeEnd, String bankReturnMsg) {

		LOG.info("completeSuccessOrder:rpTradePaymentRecord:" + rpTradePaymentRecord);
		LOG.info("completeSuccessOrder:bankTrxNo:" + bankTrxNo);
		LOG.info("completeSuccessOrder:timeEnd:" + timeEnd);
		LOG.info("completeSuccessOrder:bankReturnMsg:" + bankReturnMsg);
		rpTradePaymentRecord.setPaySuccessTime(timeEnd);
		rpTradePaymentRecord.setBankTrxNo(bankTrxNo);// 设置银行流水号
		rpTradePaymentRecord.setBankReturnMsg(bankReturnMsg);
		rpTradePaymentRecord.setStatus(TradeStatusEnum.SUCCESS.name());
		rpTradePaymentRecordDao.update(rpTradePaymentRecord);
		
		RpTradeTransferOrder rpTradeTransferOrder = rpTradeTransferOrderDao.selectByMerchantNoAndMerchantOrderNo(rpTradePaymentRecord.getMerchantNo(), rpTradePaymentRecord.getMerchantOrderNo());
		rpTradeTransferOrder.setStatus(TradeStatusEnum.SUCCESS.name());
		rpTradeTransferOrder.setTrxNo(rpTradePaymentRecord.getTrxNo());// 设置支付平台支付流水号
		rpTradeTransferOrderDao.update(rpTradeTransferOrder);
		
		LOG.info("completeSuccessOrder:rpTradeTransferOrder:" + rpTradeTransferOrder);
		LOG.info("completeSuccessOrder:TrxNo:" + rpTradePaymentRecord.getTrxNo());

		if (FundInfoTypeEnum.MERCHANT_RECEIVES.name().equals(rpTradePaymentRecord.getFundIntoType())) {
			rpAccountTransactionService.debitToAccount(rpTradePaymentRecord.getMerchantNo(),
					rpTradePaymentRecord.getOrderAmount().subtract(rpTradePaymentRecord.getPlatIncome()),
					rpTradePaymentRecord.getBankOrderNo(), rpTradePaymentRecord.getBankTrxNo(),
					rpTradePaymentRecord.getTrxType(), rpTradePaymentRecord.getRemark());
		}

	}


    

    /**
     * 支付失败方法
     * @param rpTradePaymentRecord
     */
    private void completeFailOrder(RpTradePaymentRecord rpTradePaymentRecord , String bankReturnMsg){
        rpTradePaymentRecord.setBankReturnMsg(bankReturnMsg);
        rpTradePaymentRecord.setStatus(TradeStatusEnum.FAILED.name());
        rpTradePaymentRecordDao.update(rpTradePaymentRecord);

        RpTradeTransferOrder rpTradeTransferOrder = rpTradeTransferOrderDao.selectByMerchantNoAndMerchantOrderNo(rpTradePaymentRecord.getMerchantNo(), rpTradePaymentRecord.getMerchantOrderNo());
        rpTradeTransferOrder.setStatus(TradeStatusEnum.FAILED.name());
        rpTradeTransferOrderDao.update(rpTradeTransferOrder);

    }

    /**
     * 通过支付订单及商户费率生成支付记录
     * @param rpTradeTransferOrder   支付订单
     * @param payWay   商户支付配置
     * @return
     */
    private TransferResultVo getTransferResultVo(RpTradeTransferOrder rpTradeTransferOrder, RpPayWay payWay){

    	TransferResultVo transferResultVo = new TransferResultVo();

        String payWayCode = payWay.getPayWayCode();//支付方式

        PayTypeEnum payType = null;
        if (PayWayEnum.WEIXIN.name().equals(payWay.getPayWayCode())){
            payType = PayTypeEnum.WX_TRANSFER;
        }else if(PayWayEnum.ALIPAY.name().equals(payWay.getPayWayCode())){
            payType = PayTypeEnum.ALI_TRANSFER;
        }

        rpTradeTransferOrder.setPayTypeCode(payType.name());
        rpTradeTransferOrder.setPayTypeName(payType.getDesc());

        rpTradeTransferOrder.setPayWayCode(payWay.getPayWayCode());
        rpTradeTransferOrder.setPayWayName(payWay.getPayWayName());
        rpTradeTransferOrderDao.update(rpTradeTransferOrder);

        RpTradePaymentRecord rpTradePaymentRecord = sealRpTradePaymentRecord(
        		rpTradeTransferOrder.getPayerUserNo(), rpTradeTransferOrder.getPayerName(),
        		rpTradeTransferOrder.getMerchantNo(),  rpTradeTransferOrder.getMerchantName(), 
        		rpTradeTransferOrder.getOrderName(),  rpTradeTransferOrder.getMerchantOrderNo(),  
        		rpTradeTransferOrder.getOrderAmount(), payWay.getPayWayCode(), payWay.getPayWayName(), payType, 
        		rpTradeTransferOrder.getFundOutType(), BigDecimal.valueOf(payWay.getPayRate()),  
        		rpTradeTransferOrder.getOrderIp(), rpTradeTransferOrder.getRemark());
        rpTradePaymentRecordDao.insert(rpTradePaymentRecord);

		LOG.info("支付方式={}", payWayCode);
		LOG.info("支付类型={}", payType.getDesc());
        
        if (PayWayEnum.WEIXIN.name().equals(payWayCode)){//微信支付
			String appid = "";
			String mch_id = "";
			String partnerKey = "";
			if (FundInfoTypeEnum.MERCHANT_RECEIVES.name().equals(rpTradeTransferOrder.getFundOutType())) {// 商户转账
				// 根据资金流向获取配置信息
				RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(rpTradeTransferOrder.getMerchantNo(), payWayCode);
				appid = rpUserPayInfo.getAppId();
				mch_id = rpUserPayInfo.getMerchantId();
				partnerKey = rpUserPayInfo.getPartnerKey();
			} else if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(rpTradeTransferOrder.getFundOutType())) {// 平台转账
				appid = PropertyConfigurer.getString("weixinpay.appId");
				mch_id = PropertyConfigurer.getString("weixinpay.mch_id");
				partnerKey = PropertyConfigurer.getString("weixinpay.partnerKey");
			}

            WeiXinPrePay weiXinPrePay = sealWeixinPerPay(appid , mch_id , 
            		rpTradeTransferOrder.getOrderName() ,rpTradeTransferOrder.getRemark() , 
            		rpTradePaymentRecord.getBankOrderNo() , rpTradeTransferOrder.getOrderAmount() ,  
            		rpTradeTransferOrder.getOrderTime() ,  rpTradeTransferOrder.getOrderPeriod() , 
            		WeiXinTradeTypeEnum.NATIVE ,
                    rpTradePaymentRecord.getBankOrderNo() ,"" ,rpTradeTransferOrder.getOrderIp());
            String prePayXml = WeiXinPayUtils.getPrePayXml(weiXinPrePay, partnerKey);
            //调用微信支付的功能,获取微信支付code_url
            
            String prepayUrl = PropertyConfigurer.getString("weixinpay.prepay_url");
            Map<String, Object> prePayRequest = WeiXinPayUtils.httpXmlRequest(prepayUrl, "POST", prePayXml);
            if (WeixinTradeStateEnum.SUCCESS.name().equals(prePayRequest.get("return_code")) && WeixinTradeStateEnum.SUCCESS.name().equals(prePayRequest.get("result_code"))) {
                String weiXinPrePaySign = WeiXinPayUtils.geWeiXintPrePaySign(appid, mch_id, weiXinPrePay.getDeviceInfo(), WeiXinTradeTypeEnum.NATIVE.name(), prePayRequest, partnerKey);
                String codeUrl = String.valueOf(prePayRequest.get("code_url"));
                LOG.info("预支付生成成功,{}",codeUrl);
                if (prePayRequest.get("sign").equals(weiXinPrePaySign)) {
                    rpTradePaymentRecord.setBankReturnMsg(prePayRequest.toString());
                    rpTradePaymentRecordDao.update(rpTradePaymentRecord);
                    //scanPayResultVo.setCodeUrl(codeUrl);//设置微信跳转地址
                    //scanPayResultVo.setPayWayCode(PayWayEnum.WEIXIN.name());
                    //scanPayResultVo.setOrderName(rpTradeTransferOrder.getOrderName());
                    //scanPayResultVo.setOrderAmount(rpTradeTransferOrder.getOrderAmount());
                    
                    //再次前面返回给APP
                    //Map<String, String> prePay = WeiXinPayUtils.getPrePayMapForAPP(prePayRequest, partnerKey);
                    //scanPayResultVo.setPrePay(prePay);
                }else{
                    throw new TradeBizException(TradeBizException.TRADE_WEIXIN_ERROR,"微信返回结果签名异常");
                }
            }else{
                throw new TradeBizException(TradeBizException.TRADE_WEIXIN_ERROR,"请求微信异常");
            }
		} else if (PayWayEnum.ALIPAY.name().equals(payWayCode)) {// 支付宝转账
        	        	
			// 支付宝支付
			String app_id = "";
			String rsa_private_key = "";
			String alipay_public_key = "";

			if (FundInfoTypeEnum.MERCHANT_RECEIVES.name().equals(rpTradeTransferOrder.getFundOutType())) {// 商户收款
				// 根据资金流向获取配置信息
				RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(rpTradeTransferOrder.getMerchantNo(), payWayCode);
				app_id = rpUserPayInfo.getAppId();
				rsa_private_key = rpUserPayInfo.getRsaPrivateKey();
				alipay_public_key = rpUserPayInfo.getRsaPublicKey();
			} else if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(rpTradeTransferOrder.getFundOutType())) {// 平台收款
				app_id = AlipayConfigUtil.app_id;
				rsa_private_key = AlipayConfigUtil.rsa_private_key;
				alipay_public_key = AlipayConfigUtil.rsa_public_key;
			}

			String charset = AlipayConfigUtil.input_charset;
			String serverUrl = "https://openapi.alipay.com/gateway.do";
        	
        	LOG.info("支付宝app_id={}", app_id);
        	
			AlipayClient alipayClient = new 
					DefaultAlipayClient(serverUrl, app_id, rsa_private_key, "json", charset, alipay_public_key, "RSA2");

			BigDecimal amount = rpTradeTransferOrder.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_UP);

			//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.open.public.template.message.industry.modify 
			AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
			AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
			model.setAmount(String.valueOf(amount));
			//model.setExtParam(extParam);
			model.setOutBizNo(rpTradeTransferOrder.getMerchantOrderNo());
			model.setPayeeAccount(rpTradeTransferOrder.getReceiverUserNo());
			model.setPayeeRealName(rpTradeTransferOrder.getReceiverName());
			model.setPayeeType("ALIPAY_LOGONID");
			//model.setPayerRealName(payerRealName); //收款方真实姓名,如果本参数不为空，则会校验该账户在支付宝登记的实名是否与收款方真实姓名一致。	
			model.setPayerShowName(rpTradeTransferOrder.getOrderName()); //付款方姓名,例如：上海交通卡退款
			model.setRemark(rpTradeTransferOrder.getRemark());
			request.setBizModel(model);

			AlipayFundTransToaccountTransferResponse response;
			try {
				response = alipayClient.execute(request);
				LOG.info("支付宝body={}", response.getBody());
	            
	            if(response.isSuccess()){
	            	String bankTrxNo = response.getOrderId();
	            	Date timeEnd = null;
					if (!StringUtil.isEmpty(response.getPayDate())) {
						timeEnd = DateUtils.getDateFromString(response.getPayDate(), "yyyy-MM-dd HH:mm:ss");
					}
	            	completeSuccessOrder(rpTradePaymentRecord, bankTrxNo, timeEnd, response.getBody());
	            }
	            else {
	            	completeFailOrder(rpTradePaymentRecord, response.getBody());
	            }	            
			} catch (AlipayApiException e) {
				e.printStackTrace();
				
				LOG.info("支付宝exception={}", e.getMessage());
	            rpTradePaymentRecord.setBankReturnMsg(e.getErrMsg());
	            rpTradePaymentRecordDao.update(rpTradePaymentRecord);
	            
	            completeFailOrder(rpTradePaymentRecord, e.getMessage());
			}
			
			transferResultVo.setOrderAmount(amount);//设置跳转地址
			transferResultVo.setOrderName(rpTradeTransferOrder.getOrderName());
			transferResultVo.setPayWayCode(payWayCode);
			transferResultVo.setTxNo(rpTradePaymentRecord.getId());

		} else {
			throw new TradeBizException(TradeBizException.TRADE_PAY_WAY_ERROR, "错误的支付方式");
		}

        return transferResultVo;
    }

    /**
     * 封装预支付实体
     * @param appId 公众号ID
     * @param mchId    商户号
     * @param productName   商品描述
     * @param remark  支付备注
     * @param bankOrderNo   银行订单号
     * @param orderPrice    订单价格
     * @param orderTime 订单下单时间
     * @param orderPeriod   订单有效期
     * @param weiXinTradeTypeEnum   微信支付方式
     * @param productId 商品ID
     * @param openId    用户标识
     * @param orderIp   下单IP
     * @return
     */
    private WeiXinPrePay sealWeixinPerPay(String appId ,String mchId ,String productName ,String remark ,String bankOrderNo ,BigDecimal orderPrice , Date orderTime , Integer orderPeriod ,WeiXinTradeTypeEnum weiXinTradeTypeEnum ,
                                            String productId ,String openId ,String orderIp){
        WeiXinPrePay weiXinPrePay = new WeiXinPrePay();

        weiXinPrePay.setAppid(appId);
        weiXinPrePay.setMchId(mchId);
        weiXinPrePay.setBody(productName);//商品描述
        weiXinPrePay.setAttach(remark);//支付备注
        weiXinPrePay.setOutTradeNo(bankOrderNo);//银行订单号

        Integer totalFee = orderPrice.multiply(BigDecimal.valueOf(100d)).intValue();
        weiXinPrePay.setTotalFee(totalFee);//订单金额
        weiXinPrePay.setTimeStart(DateUtils.formatDate(orderTime, "yyyyMMddHHmmss"));//订单开始时间
        weiXinPrePay.setTimeExpire(DateUtils.formatDate(DateUtils.addMinute(orderTime, orderPeriod), "yyyyMMddHHmmss"));//订单到期时间
        weiXinPrePay.setNotifyUrl(PropertyConfigurer.getString("weixinpay.notify_url"));//通知地址
        weiXinPrePay.setTradeType(weiXinTradeTypeEnum);//交易类型
        weiXinPrePay.setProductId(productId);//商品ID
        weiXinPrePay.setOpenid(openId);//用户标识
        weiXinPrePay.setSpbillCreateIp(orderIp);//下单IP

        return weiXinPrePay;
    }
    /**
     * 完成扫码支付(支付宝即时到账支付)
     * @param payWayCode
     * @param notifyMap
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String completeTransfer(String payWayCode, Map<String, String> notifyMap) {
		LOG.info("接收到{}支付结果{}", payWayCode, notifyMap);

		String returnStr = null;
		String bankOrderNo = notifyMap.get("out_trade_no");
		// 根据银行订单号获取支付信息
		LOG.info("bankOrderNo=" + bankOrderNo);
		
		if (StringUtil.isEmpty(bankOrderNo)) {
			LOG.info("参数out_trade_no异常" + TradeBizException.TRADE_PARAM_ERROR);
			throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR, "参数out_trade_no异常");
		}
		RpTradePaymentRecord rpTradePaymentRecord = rpTradePaymentRecordDao.getByBankOrderNo(bankOrderNo);
		if (rpTradePaymentRecord == null) {
			LOG.info("非法订单,订单不存在" + TradeBizException.TRADE_ORDER_ERROR);
			throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "非法订单,订单不存在");
		}

		LOG.info("rpTradePaymentRecord=" + rpTradePaymentRecord.toString());
		if (TradeStatusEnum.SUCCESS.name().equals(rpTradePaymentRecord.getStatus())) {
			throw new TradeBizException(TradeBizException.TRADE_ORDER_ERROR, "订单为成功状态");
		}
		String merchantNo = rpTradePaymentRecord.getMerchantNo();// 商户编号

		LOG.info("merchantNo=" + merchantNo);
		// 根据支付订单获取配置信息
		String fundOutType = rpTradePaymentRecord.getFundIntoType();// 获取资金流入类型
		String partnerKey = "";
		RpUserPayInfo rpUserPayInfo = null;

		if (FundInfoTypeEnum.MERCHANT_RECEIVES.name().equals(fundOutType)) {// 商户收款
			// 根据资金流向获取配置信息
			rpUserPayInfo = rpUserPayInfoService.getByUserNo(merchantNo, payWayCode);
			partnerKey = rpUserPayInfo.getPartnerKey();

			LOG.info("rpUserPayInfo=" + rpUserPayInfo.toString());
		} else if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(fundOutType)) {// 平台收款
			RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByUserNo(merchantNo);
			if (rpUserPayConfig == null) {
				throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
			}
			// 根据支付产品及支付方式获取费率
			RpPayWay payWay = rpPayWayService.getByPayWayTypeCode(rpUserPayConfig.getProductCode(),
					rpTradePaymentRecord.getPayWayCode(), rpTradePaymentRecord.getPayTypeCode());
			if (payWay == null) {
				throw new UserBizException(UserBizException.USER_PAY_CONFIG_ERRPR, "用户支付配置有误");
			}
		} else {
			LOG.error("错误的收款方式:" + fundOutType);
		}

		if (PayWayEnum.WEIXIN.name().equals(payWayCode)) {
			String sign = notifyMap.remove("sign");
			LOG.info("sign=" + sign);
			if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(fundOutType)) {
				partnerKey = PropertyConfigurer.getString("weixinpay.partnerKey");
			}

			if (WeiXinPayUtils.notifySign(notifyMap, sign, partnerKey)) {// 根据配置信息验证签名
				if (WeixinTradeStateEnum.SUCCESS.name().equals(notifyMap.get("result_code"))) {// 业务结果成功
					String timeEndStr = notifyMap.get("time_end");
					Date timeEnd = null;
					if (!StringUtil.isEmpty(timeEndStr)) {
						timeEnd = DateUtils.getDateFromString(timeEndStr, "yyyyMMddHHmmss");// 订单支付完成时间
					}
					completeSuccessOrder(rpTradePaymentRecord, notifyMap.get("transaction_id"), timeEnd, notifyMap.toString());
					returnStr = "<xml>\n" 
							+ "  <return_code><![CDATA[SUCCESS]]></return_code>\n"
							+ "  <return_msg><![CDATA[OK]]></return_msg>\n" 
							+ "</xml>";
					LOG.info("returnStr=" + returnStr);
				} else {
					completeFailOrder(rpTradePaymentRecord, notifyMap.toString());
				}
			} else {
				throw new TradeBizException(TradeBizException.TRADE_WEIXIN_ERROR, "微信签名失败");
			}
		} else if (PayWayEnum.ALIPAY.name().equals(payWayCode)) {
			String signType = notifyMap.get("sign_type");
			String decryptKey = null;
			if (FundInfoTypeEnum.MERCHANT_RECEIVES.name().equals(fundOutType)) {// 商户收款
				if (signType.equalsIgnoreCase("MD5")) {
					decryptKey = rpUserPayInfo.getAppSectet();
				} else if (signType.equalsIgnoreCase("RSA") || signType.equalsIgnoreCase("RSA2")) {
					decryptKey = rpUserPayInfo.getRsaPublicKey();
				} else {
					LOG.error("错误的加密方式:" + signType);
				}
				LOG.info("MERCHANT_RECEIVES -> signType:" + signType);
			} else if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(fundOutType)) {// 平台收款
				if (signType.equalsIgnoreCase("MD5")) {
					decryptKey = AlipayConfigUtil.key;
				} else if (signType.equalsIgnoreCase("RSA") || signType.equalsIgnoreCase("RSA2")) {
					decryptKey = AlipayConfigUtil.rsa_public_key;
				} else {
					LOG.error("错误的加密方式:" + signType);
				}
				LOG.info("PLAT_RECEIVES -> signType:" + signType);
			} else {
				LOG.error("错误的收款方式:" + fundOutType);
			}
			
			LOG.info("Pre AlipayNotify:" + notifyMap);
			
			String charset = AlipayConfigUtil.input_charset;
			LOG.info("sign charset:" + charset);
			boolean verifyResult = AlipayNotify.verify(partnerKey, decryptKey, notifyMap);
			LOG.info("verifyResult:" + verifyResult);
			if (verifyResult) {// 验证成功
				String tradeStatus = notifyMap.get("trade_status");
				LOG.info("AFT AlipayNotify:tradeStatus=" + tradeStatus);
				if (AliPayTradeStateEnum.TRADE_FINISHED.name().equals(tradeStatus)) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
					// 如果有做过处理，不执行商户的业务程序

					// 注意：
					// 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				} else if (AliPayTradeStateEnum.TRADE_SUCCESS.name().equals(tradeStatus)) {
					String gmtTransferStr = notifyMap.get("gmt_payment");// 付款时间
					Date timeEnd = null;
					if (!StringUtil.isEmpty(gmtTransferStr)) {
						timeEnd = DateUtils.getDateFromString(gmtTransferStr, "yyyy-MM-dd HH:mm:ss");
					}
					LOG.info("Pre completeSuccessOrder:" + notifyMap.get("trade_no"));
					completeSuccessOrder(rpTradePaymentRecord, notifyMap.get("trade_no"), timeEnd, notifyMap.toString());
					returnStr = "success";
				} else {
					completeFailOrder(rpTradePaymentRecord, notifyMap.toString());
					returnStr = "fail";
				}
			} else {// 验证失败
				throw new TradeBizException(TradeBizException.TRADE_ALIPAY_ERROR, "支付宝签名异常");
			}
		} else if (PayWayEnum.APPLE.name().equals(payWayCode)) {
			//苹果客户端传上来的收据,是最原据的收据  
			String receipt = notifyMap.get("receipt");
			Boolean isSandbox = PropertyConfigurer.getBoolean("applepay.sandbox", true);
			String verifyResult = ApplePayNotify.buyAppVerify(receipt, isSandbox);
			
			if (verifyResult == null) {
                //苹果服务器没有返回验证结果  
            	try {
					completeFailOrder(rpTradePaymentRecord, JSONUtils.serializeObject(notifyMap));
				} catch (Exception e) {}
				throw new TradeBizException(TradeBizException.TRADE_APPLE_ERROR, "苹果签名异常");
			} else {
				//跟苹果验证有返回结果------------------ 
				AppleVerifyBean verifybean = ApplePayUtil.parseJsonToBean(verifyResult);
				String states = verifybean.getStatus().toString();
				if (states.equals("0")) {// 验证成功
					
					AppleReceiptBean receiptBean = verifybean.getReceipt();
					String timeEndStr = receiptBean.getReceipt_creation_date();
					Date timeEnd = null;
					if (!StringUtil.isEmpty(timeEndStr)) {
						timeEnd = DateUtils.getDateFromString(timeEndStr, "yyyy-MM-dd HH:mm:ss 'Etc/GMT'");// 订单支付完成时间
					}
					
					String transactionId = ApplePayUtil.getTransactionId(receiptBean);
					completeSuccessOrder(rpTradePaymentRecord, transactionId, timeEnd, verifyResult.toString());
					returnStr = "{\"message\": \"success\",\"code\": 0}";
					LOG.info("returnStr=" + returnStr);
				} else {
					// 账单无效
					completeFailOrder(rpTradePaymentRecord, verifyResult);
					throw new TradeBizException(TradeBizException.TRADE_APPLE_ERROR, "苹果验证异常");
				}
                //跟苹果验证有返回结果------------------  
            }  
			
		} else {
			throw new TradeBizException(TradeBizException.TRADE_PAY_WAY_ERROR, "错误的支付方式");
		}

		LOG.info("返回支付通道{}信息{}", payWayCode, returnStr);
        return returnStr;
    }

    /**
     * 支付订单实体封装
     * @param merchantNo    商户编号
     * @param merchantName  商户名称
     * @param orderName   产品名称
     * @param orderNo   商户订单号
     * @param orderDate 下单日期
     * @param orderTime 下单时间
     * @param orderPrice    订单金额
     * @param payWay    支付方式
     * @param payWayName    支付方式名称
     * @param payType   支付类型
     * @param fundOutType  资金流入类型
     * @param orderIp   下单IP
     * @param orderPeriod   订单有效期
     * @param remark    支付备注
     * @return
     */
    private RpTradeTransferOrder sealRpTradeTransferOrder(String payeeAccount, String payeeName, 
    		String merchantNo, String merchantName,
    		String orderName, String orderNo, Date orderDate, Date orderTime, BigDecimal orderPrice,
           	String payWay,String payWayName , PayTypeEnum payType, String fundOutType,
           	String orderIp, Integer orderPeriod, String remark){

        RpTradeTransferOrder rpTradeTransferOrder = new RpTradeTransferOrder();
        rpTradeTransferOrder.setOrderName(orderName);//商品名称
        if (StringUtil.isEmpty(orderNo)){
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR,"订单号错误");
        }

        rpTradeTransferOrder.setMerchantOrderNo(orderNo);//订单号

        if (orderPrice == null || orderPrice.doubleValue() <= 0){
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR,"订单金额错误");
        }

        rpTradeTransferOrder.setOrderAmount(orderPrice);//订单金额

        if (StringUtil.isEmpty(merchantName)){
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR,"商户名称错误");
        }
        rpTradeTransferOrder.setMerchantName(merchantName);//商户名称

        if (StringUtil.isEmpty(merchantNo)){
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR,"商户编号错误");
        }
        rpTradeTransferOrder.setMerchantNo(merchantNo);//商户编号

        if (orderDate == null){
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR,"下单日期错误");
        }
        rpTradeTransferOrder.setOrderDate(orderDate);//下单日期

        if (orderTime == null){
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR,"下单时间错误");
        }
        rpTradeTransferOrder.setOrderTime(orderTime);//下单时间
        rpTradeTransferOrder.setOrderIp(orderIp);//下单IP
        
        if (orderPeriod == null || orderPeriod <= 0){
            throw new TradeBizException(TradeBizException.TRADE_PARAM_ERROR,"订单有效期错误");
        }
        rpTradeTransferOrder.setOrderPeriod(orderPeriod);//订单有效期

        Date expireTime = DateUtils.addMinute(orderTime,orderPeriod);//订单过期时间
        rpTradeTransferOrder.setExpireTime(expireTime);//订单过期时间
        rpTradeTransferOrder.setPayWayCode(payWay);//支付通道编码
        rpTradeTransferOrder.setPayWayName(payWayName);//支付通道名称
        rpTradeTransferOrder.setStatus(TradeStatusEnum.WAITING_PAYMENT.name());//订单状态 等待支付

        if (payType != null){
            rpTradeTransferOrder.setPayTypeCode(payType.name());//支付类型
            rpTradeTransferOrder.setPayTypeName(payType.getDesc());//支付方式
        }
        rpTradeTransferOrder.setFundOutType(fundOutType);//资金流入方向

        rpTradeTransferOrder.setReceiverUserNo(payeeAccount);
        rpTradeTransferOrder.setReceiverName(payeeName);
        rpTradeTransferOrder.setReceiverAccountType(AccountTypeEnum.USER.name());
        rpTradeTransferOrder.setRemark(remark);//支付备注

        return rpTradeTransferOrder;
    }


    /**
     * 封装支付流水记录实体
     * @param merchantNo    商户编号
     * @param merchantName  商户名称
     * @param orderName   产品名称
     * @param orderNo   商户订单号
     * @param orderPrice    订单金额
     * @param payWay    支付方式编码
     * @param payWayName    支付方式名称
     * @param payType   支付类型
     * @param fundOutType  资金流入方向
     * @param feeRate   支付费率
     * @param orderIp   订单IP
     * @param remark    备注
     * @return
     */
    private RpTradePaymentRecord sealRpTradePaymentRecord(
    		String payeeAccount, String payeeName,
    		String merchantNo, String merchantName,
    		String orderName, String orderNo, BigDecimal orderPrice,
    		String payWay, String payWayName, PayTypeEnum payType, 
    		String fundOutType, BigDecimal feeRate,
    		String orderIp, String remark){
        RpTradePaymentRecord rpTradePaymentRecord = new RpTradePaymentRecord();
        rpTradePaymentRecord.setProductName(orderName);//产品名称
        rpTradePaymentRecord.setMerchantOrderNo(orderNo);//产品编号

        String trxNo = buildNoService.buildTrxNo();
        rpTradePaymentRecord.setTrxNo(trxNo);//支付流水号

        String bankOrderNo = buildNoService.buildBankOrderNo();
        rpTradePaymentRecord.setBankOrderNo(bankOrderNo);//银行订单号
        rpTradePaymentRecord.setMerchantName(merchantName);
        rpTradePaymentRecord.setMerchantNo(merchantNo);//商户编号
        rpTradePaymentRecord.setOrderIp(orderIp);//下单IP
        rpTradePaymentRecord.setPayWayCode(payWay);//支付通道编码
        rpTradePaymentRecord.setPayWayName(payWayName);//支付通道名称
        rpTradePaymentRecord.setTrxType(TrxTypeEnum.EXPENSE.name());//交易类型
        rpTradePaymentRecord.setOrderAmount(orderPrice);//订单金额
        rpTradePaymentRecord.setStatus(TradeStatusEnum.WAITING_PAYMENT.name());//订单状态 等待支付

        rpTradePaymentRecord.setPayTypeCode(payType.name());//支付类型
        rpTradePaymentRecord.setPayTypeName(payType.getDesc());//支付方式
        rpTradePaymentRecord.setFundIntoType(fundOutType);//资金流入方向
        
        rpTradePaymentRecord.setPayerAccountType(AccountTypeEnum.ENTERPRISE.name());
        rpTradePaymentRecord.setReceiverAccountType(AccountTypeEnum.USER.name());
        
        rpTradePaymentRecord.setReceiverName(payeeName);
        rpTradePaymentRecord.setReceiverUserNo(payeeAccount);

        if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(fundOutType)){//平台收款 需要修改费率 成本 利润 收入 以及修改商户账户信息
            BigDecimal orderAmount = rpTradePaymentRecord.getOrderAmount();//订单金额
            BigDecimal platIncome = orderAmount.multiply(feeRate).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP);  //平台收入 = 订单金额 * 支付费率(设置的费率除以100为真实费率)
            BigDecimal platCost = orderAmount.multiply(BigDecimal.valueOf(Double.valueOf(PropertyConfigurer.getString("weixinpay.pay_rate")))).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_HALF_UP);//平台成本 = 订单金额 * 微信费率(设置的费率除以100为真实费率)
            BigDecimal platProfit = platIncome.subtract(platCost);//平台利润 = 平台收入 - 平台成本

            rpTradePaymentRecord.setFeeRate(feeRate);//费率
            rpTradePaymentRecord.setPlatCost(platCost);//平台成本
            rpTradePaymentRecord.setPlatIncome(platIncome);//平台收入
            rpTradePaymentRecord.setPlatProfit(platProfit);//平台利润
            
            BigDecimal receiverPayAmount = orderAmount.divide(platIncome);
            rpTradePaymentRecord.setReceiverPayAmount(receiverPayAmount);
            rpTradePaymentRecord.setReceiverFee(feeRate);
        }

        rpTradePaymentRecord.setRemark(remark);//支付备注
        return rpTradePaymentRecord;
    }

}
