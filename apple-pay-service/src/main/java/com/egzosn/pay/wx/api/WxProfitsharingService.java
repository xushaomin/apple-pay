package com.egzosn.pay.wx.api;

import java.util.Map;

import com.egzosn.pay.wx.bean.WxAddReceiver;
import com.egzosn.pay.wx.bean.WxProfitSharing;

/**
 * 分账接口
 *
 * @author cruise
 */
public interface WxProfitsharingService {

    Map<String, Object> doProfitsharing(WxProfitSharing profitSharing);
    
    Map<String, Object> addReceiver(WxAddReceiver addReceiver);


}
