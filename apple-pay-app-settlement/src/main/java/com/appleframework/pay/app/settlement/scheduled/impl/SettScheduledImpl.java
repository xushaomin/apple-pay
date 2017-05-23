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
package com.appleframework.pay.app.settlement.scheduled.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.appleframework.pay.account.entity.RpAccount;
import com.appleframework.pay.account.service.RpAccountQueryService;
import com.appleframework.pay.app.settlement.biz.SettBiz;
import com.appleframework.pay.app.settlement.scheduled.SettScheduled;


/**
 * 结算定时任务实现.
 * http://www.appleframework.com
 * @author Cruise.Xu
 */
@Component("settScheduled")
public class SettScheduledImpl implements SettScheduled {

	@Autowired
	private SettBiz settBiz;
	
	@Autowired
	private RpAccountQueryService rpAccountQueryService;

	/**
	 * 发起每日待结算数据汇总.
	 */
	public void launchDailySettCollect() {
		// 查询所有需要跑批的商户账户
		List<RpAccount> list = rpAccountQueryService.listAll();
		Date endDate = new Date();
		settBiz.launchDailySettCollect(list, endDate); // 统计数据
	}

	/**
	 * 发起定期自动结算.
	 */
	public void launchAutoSett() {
		// 查询所有需要跑批的商户账户
		List<RpAccount> list = rpAccountQueryService.listAll();
		settBiz.launchAutoSett(list);
	}

}
