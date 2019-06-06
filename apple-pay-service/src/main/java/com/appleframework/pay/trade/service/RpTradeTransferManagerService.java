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
import java.util.Date;
import java.util.Map;

import com.appleframework.pay.trade.vo.TransferResultVo;

/**
 * <b>功能说明:交易模块管理接口</b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public interface RpTradeTransferManagerService {

    /**
     * 初始化直连扫码支付数据,直连扫码支付初始化方法规则
     * 1:根据(商户编号 + 商户订单号)确定订单是否存在
     * 1.1:如果订单不存在,创建支付订单
     * 2:创建支付记录
     * 3:根据相应渠道方法
     * 4:调转到相应支付渠道扫码界面
     * @param payKey    商户支付Key
     * @param productName   产品名称
     * @param orderNo   商户订单号
     * @param orderDate 下单日期
     * @param orderTime 下单时间
     * @param orderPrice    订单金额(元)
     * @param payWayCode    支付方式
     * @param orderIp   下单IP
     * @param orderPeriod   订单有效期(分钟)
     * @param returnUrl 支付结果页面通知地址
     * @param notifyUrl 支付结果后台通知地址
     * @param remark    支付备注
     * @param field1    扩展字段1
     * @param field2    扩展字段2
     * @param field3    扩展字段3
     * @param field4    扩展字段4
     * @param field5    扩展字段5
     */
	public TransferResultVo initDirectTransfer(String payKey, String payeeAccount, String payeeName, String orderName, String orderNo, Date orderDate,
			Date orderTime, BigDecimal orderPrice, String payWayCode, String orderIp, Integer orderPeriod,
			String remark);

    /**
     * 完成扫码支付(支付宝即时到账支付)
     * @param payWayCode
     * @param notifyMap
     * @return
     */
	public String completeTransfer(String payWayCode, Map<String, String> notifyMap);


}
