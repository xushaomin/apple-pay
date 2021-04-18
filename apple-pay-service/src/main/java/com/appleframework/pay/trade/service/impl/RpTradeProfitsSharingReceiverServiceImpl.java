package com.appleframework.pay.trade.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.appleframework.pay.common.core.enums.PublicStatusEnum;
import com.appleframework.pay.trade.dao.RpTradeProfitsSharingReceiverDao;
import com.appleframework.pay.trade.entity.RpTradeProfitsSharingReceiver;
import com.appleframework.pay.trade.service.RpTradeProfitsSharingReceiverService;

@Service("rpTradeProfitsSharingReceiverService")
public class RpTradeProfitsSharingReceiverServiceImpl implements RpTradeProfitsSharingReceiverService {
	
	@Resource
	private RpTradeProfitsSharingReceiverDao rpTradeProfitsSharingReceiverDao;
	
	@Override
	public RpTradeProfitsSharingReceiver getByMD5(String md5) {
		return rpTradeProfitsSharingReceiverDao.selectByMD5(md5);
	}

	@Override
	public void addReceiver(RpTradeProfitsSharingReceiver record) {
		record.setStatus(PublicStatusEnum.ACTIVE.name());
		rpTradeProfitsSharingReceiverDao.insert(record);
	}

}
