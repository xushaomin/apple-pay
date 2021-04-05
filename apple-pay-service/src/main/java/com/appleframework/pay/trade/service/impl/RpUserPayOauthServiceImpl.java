package com.appleframework.pay.trade.service.impl;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appleframework.pay.trade.dao.RpUserPayOauthDao;
import com.appleframework.pay.trade.entity.RpUserPayOauth;
import com.appleframework.pay.trade.enums.TradeStatusEnum;
import com.appleframework.pay.trade.model.UserPayOauthBo;
import com.appleframework.pay.trade.service.RpUserPayOauthService;

@Service
public class RpUserPayOauthServiceImpl implements RpUserPayOauthService {

    @Autowired
    private RpUserPayOauthDao rpUserPayOauthDao;

	@Override
	public void saveOrUpdate(UserPayOauthBo uerPayOauthBo) {
		String appType = uerPayOauthBo.getAppType();
		String appId = uerPayOauthBo.getAppId();
		String merchantId = uerPayOauthBo.getMerchantId();
		String payUserid = uerPayOauthBo.getPayUserid();
		
		RpUserPayOauth userPayOauth = rpUserPayOauthDao.findOne(appType, appId, merchantId, payUserid);
		if(null == userPayOauth) {
			userPayOauth = new RpUserPayOauth();
			BeanUtils.copyProperties(uerPayOauthBo, userPayOauth);
			userPayOauth.setCreateTime(new Date());
			userPayOauth.setCreater("system");
			userPayOauth.setEditTime(new Date());
			userPayOauth.setEditor("system");
			userPayOauth.setStatus(TradeStatusEnum.SUCCESS.name());
			userPayOauth.setRemark("first");
			rpUserPayOauthDao.insert(userPayOauth);
		}
		else {
			BeanUtils.copyProperties(uerPayOauthBo, userPayOauth);
			userPayOauth.setCreateTime(new Date());
			userPayOauth.setCreater("system");
			userPayOauth.setEditTime(new Date());
			userPayOauth.setEditor("system");
			userPayOauth.setStatus(TradeStatusEnum.SUCCESS.name());

			BeanUtils.copyProperties(uerPayOauthBo, userPayOauth);
			rpUserPayOauthDao.update(userPayOauth);
		}
	}
	
	public RpUserPayOauth getOne(String appType, String appId, String merchantId, String payUserid) {
		return rpUserPayOauthDao.findOne(appType, appId, merchantId, payUserid);
	}

}
