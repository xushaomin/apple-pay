package com.appleframework.pay.notify.dao;


import com.appleframework.pay.common.core.dao.BaseDao;
import com.appleframework.pay.notify.entity.RpNotifyRecordLog;


/**
 * @功能说明:
 * @创建者: Peter
 * @创建时间: 16/6/2  上午11:20
 * @公司名称:深圳市果苹网络科技有限公司(http://www.appleframework.com)
 * @版本:V1.0
 */
public interface RpNotifyRecordLogDao  extends BaseDao<RpNotifyRecordLog> {


    int deleteByPrimaryKey(String id);

    int insertSelective(RpNotifyRecordLog record);

    RpNotifyRecordLog selectByPrimaryKey(String id);


    int updateByPrimaryKey(RpNotifyRecordLog record);
}