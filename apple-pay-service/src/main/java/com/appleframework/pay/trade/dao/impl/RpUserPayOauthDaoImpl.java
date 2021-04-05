package com.appleframework.pay.trade.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.appleframework.pay.common.core.dao.impl.BaseDaoImpl;
import com.appleframework.pay.trade.dao.RpUserPayOauthDao;
import com.appleframework.pay.trade.entity.RpUserPayOauth;

@Repository("rpUserPayOauthDao")
public class RpUserPayOauthDaoImpl extends BaseDaoImpl<RpUserPayOauth> implements RpUserPayOauthDao {

	@Override
	public RpUserPayOauth findOne(String appType, String appId, String merchantId, String payUserid) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appType", appType.trim());
        paramMap.put("appId", appId.trim());
        paramMap.put("merchantId", merchantId.trim());
        paramMap.put("payUserid", payUserid.trim());
        return super.getByColumn(paramMap);
    }

}
