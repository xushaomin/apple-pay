package com.appleframework.pay.trade.dao;

import com.appleframework.pay.common.core.dao.BaseDao;
import com.appleframework.pay.trade.entity.RpTradeProfitsSharingReceiver;

public interface RpTradeProfitsSharingReceiverDao extends BaseDao<RpTradeProfitsSharingReceiver> {

	public RpTradeProfitsSharingReceiver selectByMD5(String md5);

}
