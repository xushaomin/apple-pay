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
package com.appleframework.pay.trade.exception;

import com.appleframework.pay.common.core.exception.BizException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>功能说明:交易模块异常类</b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class TradeBizException extends BizException {

	private static final long serialVersionUID = 3536909333010163563L;

	/** 错误的支付方式 **/
	public static final int TRADE_PAY_WAY_ERROR = 102;

	/** 微信异常 **/
	public static final int TRADE_WEIXIN_ERROR = 103;
	
	/** 订单异常 **/
	public static final int TRADE_ORDER_ERROR = 104;

	/** 交易记录状态不为成功 **/
	public static final int TRADE_ORDER_STATUS_NOT_SUCCESS = 105;

	/** 支付宝异常 **/
	public static final int TRADE_ALIPAY_ERROR = 106;

	/** 参数异常 **/
	public static final int TRADE_PARAM_ERROR = 107;
	
	/** 参数异常 **/
	public static final int TRADE_APPLE_ERROR = 108;
	
	/** 分账异常 **/
	public static final int TRADE_PRSH_ERROR = 109;


	private static final Log LOG = LogFactory.getLog(TradeBizException.class);

	public TradeBizException() {
	}

	public TradeBizException(int code, String msgFormat, Object... args) {
		super(code, msgFormat, args);
	}

	public TradeBizException(int code, String msg) {
		super(code, msg);
	}

	public TradeBizException print() {
		LOG.info("==>BizException, code:" + this.code + ", msg:" + this.msg);
		return this;
	}
}
