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
package com.appleframework.pay.app.notify.core;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.appleframework.pay.app.notify.entity.NotifyParam;
import com.appleframework.pay.common.core.exception.BizException;
import com.appleframework.pay.common.core.utils.SpringUtility;
import com.appleframework.pay.notify.entity.RpNotifyRecord;
import com.appleframework.pay.notify.enums.NotifyStatusEnum;
import com.appleframework.pay.trade.utils.httpclient.SimpleHttpParam;
import com.appleframework.pay.trade.utils.httpclient.SimpleHttpResult;
import com.appleframework.pay.trade.utils.httpclient.SimpleHttpUtils;

/**
 * <b>功能说明:
 * </b>
 * @author  Cruise.Xu
 * <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class NotifyTask implements Runnable, Delayed {

    private static final Log LOG = LogFactory.getLog(NotifyTask.class);

    private long executeTime;

    private RpNotifyRecord notifyRecord;

    private NotifyQueueService notifyQueue;

    private NotifyParam notifyParam;

    private NotifyPersistService notifyPersistService;

    public NotifyTask() {
    }

    public NotifyTask(RpNotifyRecord notifyRecord, NotifyQueueService notifyQueue, NotifyParam notifyParam) {
        super();
        this.notifyRecord = notifyRecord;
        this.notifyQueue = notifyQueue;
        this.notifyParam = notifyParam;
        this.executeTime = getExecuteTime(notifyRecord);
    }

	private long getExecuteTime(RpNotifyRecord record) {
		long lastTime = record.getLastNotifyTime().getTime();
		Integer nextNotifyTime = notifyParam.getNotifyParams().get(record.getNotifyTimes());
		return (nextNotifyTime == null ? 0 : nextNotifyTime * 1000) + lastTime;
	}

	public int compareTo(Delayed o) {
		NotifyTask task = (NotifyTask) o;
		return executeTime > task.executeTime ? 1 : (executeTime < task.executeTime ? -1 : 0);
	}

    public long getDelay(TimeUnit unit) {
        return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.SECONDS);
    }

    public void run() {
        // 得到当前通知对象的通知次数
        Integer notifyTimes = notifyRecord.getNotifyTimes();
        // 去通知
        try {
        	if(LOG.isInfoEnabled())
				LOG.info("Notify Url " + notifyRecord.getUrl() + " ;notify id:" + notifyRecord.getId()
						+ ";notify times:" + notifyRecord.getNotifyTimes());

            /** 采用 httpClient */
            SimpleHttpParam param = new SimpleHttpParam(notifyRecord.getUrl());
            SimpleHttpResult result = SimpleHttpUtils.httpRequest(param);

			/*
			 * OkHttpClient client = new OkHttpClient(); 
			 * Request request = new Request.Builder().url(notifyRecord.getUrl()).build(); 
			 * Response response = client.newCall(request).execute();
			 */

            notifyRecord.setNotifyTimes(notifyTimes + 1);
            String successValue = notifyParam.getSuccessValue();

            String responseMsg = "";
            Integer responseStatus = result.getStatusCode();
            
            if(null == notifyPersistService)
            	notifyPersistService = SpringUtility.getBean(NotifyPersistService.class);

            // 得到返回状态，如果是200，也就是通知成功
            if (result != null
                    && (responseStatus == 200 || responseStatus == 201 || responseStatus == 202 || responseStatus == 203
                    || responseStatus == 204 || responseStatus == 205 || responseStatus == 206)) {
                responseMsg = result.getContent().trim();
                responseMsg = responseMsg.length() >= 600 ? responseMsg.substring(0, 600) : responseMsg;
                if(LOG.isInfoEnabled())
					LOG.info("订单号： " + notifyRecord.getMerchantOrderNo() + " HTTP_STATUS：" + responseStatus + "请求返回信息："
							+ responseMsg);
                // 通知成功
                if (responseMsg.trim().equals(successValue)) {
                	notifyPersistService.updateNotifyRord(notifyRecord.getId(), notifyRecord.getNotifyTimes(), NotifyStatusEnum.SUCCESS.name());
                } else {
                    notifyQueue.addElementToList(notifyRecord);
                    notifyPersistService.updateNotifyRord(notifyRecord.getId(), notifyRecord.getNotifyTimes(), NotifyStatusEnum.HTTP_REQUEST_SUCCESS.name());

                }
                if(LOG.isInfoEnabled())
                	LOG.info("Update NotifyRecord:" + JSONObject.toJSONString(notifyRecord)+";responseMsg:"+responseMsg);
            } else {
                notifyQueue.addElementToList(notifyRecord);
                // 再次放到通知列表中，由添加程序判断是否已经通知完毕或者通知失败
                notifyPersistService.updateNotifyRord(notifyRecord.getId(), notifyRecord.getNotifyTimes(), NotifyStatusEnum.HTTP_REQUEST_FALIED.name());
            }

            // 写通知日志表
            notifyPersistService.saveNotifyRecordLogs(notifyRecord.getId(), notifyRecord.getMerchantNo(), notifyRecord.getMerchantOrderNo(), notifyRecord.getUrl(), responseMsg, responseStatus);
            if(LOG.isInfoEnabled())
				LOG.info("Insert NotifyRecordLog, merchantNo:" + notifyRecord.getMerchantNo() + ",merchantOrderNo:"
						+ notifyRecord.getMerchantOrderNo());
        } catch (BizException e) {
            LOG.error("NotifyTask", e);
        } catch (Exception e) {
            LOG.error("NotifyTask", e);
            notifyQueue.addElementToList(notifyRecord);

            notifyPersistService.updateNotifyRord(notifyRecord.getId(), notifyRecord.getNotifyTimes(), NotifyStatusEnum.HTTP_REQUEST_FALIED.name());
            notifyPersistService.saveNotifyRecordLogs(notifyRecord.getId(), notifyRecord.getMerchantNo(), notifyRecord.getMerchantOrderNo(), notifyRecord.getUrl(), "", 0);
        }

    }

}
