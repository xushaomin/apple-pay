package com.appleframework.pay.trade.service;

import com.appleframework.pay.trade.entity.RpUserPayOauth;
import com.appleframework.pay.trade.model.UserPayOauthBo;

public interface RpUserPayOauthService {

    void saveOrUpdate(UserPayOauthBo uerPayOauthBo);
    
	RpUserPayOauth getOne(String appType, String appId, String merchantId, String payUserid);

}
