package com.appleframework.pay.trade.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.appleframework.pay.common.core.dao.impl.BaseDaoImpl;
import com.appleframework.pay.trade.dao.RpTradeProfitsSharingReceiverDao;
import com.appleframework.pay.trade.entity.RpTradeProfitsSharingReceiver;

@Repository("rpTradeProfitsSharingReceiverDao")
public class RpTradeProfitsSharingReceiverDaoImpl extends BaseDaoImpl<RpTradeProfitsSharingReceiver> implements RpTradeProfitsSharingReceiverDao {

	@Override
	public RpTradeProfitsSharingReceiver selectByMD5(String md5) {
		Map<String , Object> paramMap = new HashMap<String , Object>();
        paramMap.put("contentMd5",md5);
        return super.getBy(paramMap);
	}

}
