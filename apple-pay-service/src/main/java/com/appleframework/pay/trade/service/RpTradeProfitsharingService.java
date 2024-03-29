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
package com.appleframework.pay.trade.service;

import java.math.BigDecimal;

import com.appleframework.pay.common.core.enums.PayWayEnum;

/**
 * <b>功能说明:分账接口</b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public interface RpTradeProfitsharingService {
    
    /**
     * @param orderNo   商户订单号
     * @param amount 分账金额
     */
    public void doSharing(String payKey, String merchantOrderNo, BigDecimal amount);
    
    /**
     * @param subMerchantNo   子商户帐号
     * @param type 分账类型
     */
    public void addReceiver(String payKey, String subMerchantNo, PayWayEnum payWay);
    
}
