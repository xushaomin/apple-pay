package com.appleframework.pay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.appleframework.pay.user.exception.UserBizException;
import com.gitee.easyopen.ApiConfig;
import com.gitee.easyopen.AppSecretManager;
import com.gitee.easyopen.support.ApiController;

/**
 * 文档地址：http://localhost:8080/api/doc
 */
@Controller
@RequestMapping("api")
public class IndexController extends ApiController {
	
	@Resource
	private AppSecretManager appSecretManager;
	
	//@Resource
	//private Signer signer;
	
    @Override
    protected void initApiConfig(ApiConfig apiConfig) {
        apiConfig.setShowDoc(true); // 显示文档页面
        // 配置国际化消息
        Map<String, String> appSecretStore = new HashMap<String, String>();
        appSecretStore.put("test", "123456");
        apiConfig.addAppSecret(appSecretStore);
        apiConfig.setApiName("name");
        apiConfig.setAppKeyName("payKey");
        apiConfig.setSignName("sign");
        //apiConfig.setVersionName("v");
        apiConfig.setTraceId("traceId");
        apiConfig.setIgnoreValidate(false);
        apiConfig.setStandardMode(false);
        apiConfig.setShowDoc(true);
        apiConfig.setServiceExceptionClass(UserBizException.class);
        
        apiConfig.setAppSecretManager(appSecretManager);
        apiConfig.setTimeoutSeconds(0);
        //apiConfig.setSigner(signer);
        
        //apiConfig.setInterceptors(new ApiInterceptor[]{new UrlApiInterceptor()});
        
    }
}
