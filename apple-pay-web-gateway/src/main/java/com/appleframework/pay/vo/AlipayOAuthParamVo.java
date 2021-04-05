package com.appleframework.pay.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AlipayOAuthParamVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 支付Key
     */
    @Size(max = 32, message = "支付Key[payKey]长度最大32位")
    @NotNull(message = "支付Key[payKey]不能为空")
    private String payKey;

    /**
     * 应用号
     */
    @Size(max = 32, message = "应用ID[appId]长度最大32位")
    @NotNull(message = "应用ID[appId]不能为空")
    private String appId;

    /**
     * 签名
     */
    @NotNull(message = "数据签名[sign]不能为空")
    private String sign;

    public String getPayKey() {
        return payKey;
    }

    public void setPayKey(String payKey) {
        this.payKey = payKey;
    }
    
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
    
}
