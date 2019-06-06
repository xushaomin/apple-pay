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
package com.appleframework.pay.trade.dao.impl;

import com.appleframework.pay.common.core.dao.impl.BaseDaoImpl;
import com.appleframework.pay.trade.dao.RpTradeTransferOrderDao;
import com.appleframework.pay.trade.entity.RpTradeTransferOrder;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>功能说明:商户支付订单,dao层实现类</b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
@Repository("rpTradeTransferOrderDao")
public class RpTradeTransferOrderDaoImpl extends BaseDaoImpl<RpTradeTransferOrder> implements RpTradeTransferOrderDao{


    /**
     * 根据商户编号及商户订单号获取支付订单信息
     * @param merchantNo    商户编号
     * @param merchantOrderNo   商户订单号
     * @return
     */
    @Override
    public RpTradeTransferOrder selectByMerchantNoAndMerchantOrderNo(String merchantNo, String merchantOrderNo) {
        Map<String , Object> paramMap = new HashMap<String , Object>();
        paramMap.put("merchantNo",merchantNo);
        paramMap.put("merchantOrderNo",merchantOrderNo);
        return super.getBy(paramMap);
    }

}
