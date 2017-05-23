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
package com.appleframework.pay.app.notify;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.appleframework.pay.app.notify.core.NotifyQueueService;
import com.appleframework.pay.app.notify.core.NotifyTask;
import com.appleframework.pay.common.core.page.PageBean;
import com.appleframework.pay.common.core.page.PageParam;
import com.appleframework.pay.notify.entity.RpNotifyRecord;
import com.appleframework.pay.notify.service.RpNotifyService;

/**
 * <b>功能说明:消息APP启动类 </b>
 * 
 * @author Peter <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class AppInit {
	
	private static final Log LOG = LogFactory.getLog(AppInit.class);


	@Resource
	private ThreadPoolTaskExecutor threadPool;

	@Resource
	private RpNotifyService rpNotifyService;

	@Resource
	public NotifyQueueService notifyQueueService;

	public void init() {
		try {
			startInitFromDB();
			startThread();
			LOG.info("== context start");
		} catch (Exception e) {
			LOG.error("== application start error:", e);
		}
	}

	private void startThread() {
		LOG.info("startThread");

		threadPool.execute(new Runnable() {
			public void run() {
				try {
					while (true) {
						Thread.sleep(50);// 50毫秒执行一次
						// 如果当前活动线程等于最大线程，那么不执行
						if (threadPool.getActiveCount() < threadPool.getMaxPoolSize()) {
							final NotifyTask task = notifyQueueService.getTasks().poll();
							if (task != null) {
								threadPool.execute(new Runnable() {
									public void run() {
										LOG.info(threadPool.getActiveCount() + "---------");
										notifyQueueService.getTasks().remove(task);
										task.run();
									}
								});
							}
						}
					}
				} catch (Exception e) {
					LOG.error("系统异常", e);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 从数据库中取一次数据用来当系统启动时初始化
	 */
	private void startInitFromDB() {
		LOG.info("get data from database");

		int pageNum = 1;
		int numPerPage = 500;
		PageParam pageParam = new PageParam(pageNum, numPerPage);

		// 查询状态和通知次数符合以下条件的数据进行通知
		String[] status = new String[] { "101", "102", "200", "201" };
		Integer[] notifyTime = new Integer[] { 0, 1, 2, 3, 4 };
		// 组装查询条件
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("statusList", status);
		paramMap.put("notifyTimeList", notifyTime);

		PageBean<RpNotifyRecord> pager = rpNotifyService.queryNotifyRecordListPage(pageParam, paramMap);
		int totalSize = (pager.getNumPerPage() - 1) / numPerPage + 1;// 总页数
		while (pageNum <= totalSize) {
			List<RpNotifyRecord> list = pager.getRecordList();
			for (int i = 0; i < list.size(); i++) {
				RpNotifyRecord notifyRecord = list.get(i);
				notifyRecord.setLastNotifyTime(new Date());
				notifyQueueService.addElementToList(notifyRecord);
			}
			pageNum++;
			LOG.info(String.format("调用通知服务.rpNotifyService.queryNotifyRecordListPage(%s, %s, %s)", pageNum, numPerPage, paramMap));
			pageParam = new PageParam(pageNum, numPerPage);
			pager = rpNotifyService.queryNotifyRecordListPage(pageParam, paramMap);
		}
	}

}
