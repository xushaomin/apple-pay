package com.appleframework.pay.trade.dao;

import com.appleframework.pay.common.core.dao.BaseDao;
import com.appleframework.pay.trade.entity.RpUserPayOauth;

public interface RpUserPayOauthDao extends BaseDao<RpUserPayOauth> {

	public RpUserPayOauth findOne(String appType, String appId, String merchantId, String payUserid);
	
}
