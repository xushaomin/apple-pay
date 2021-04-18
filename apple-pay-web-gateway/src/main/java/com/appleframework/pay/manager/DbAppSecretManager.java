package com.appleframework.pay.manager;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.appleframework.pay.user.entity.RpUserPayConfig;
import com.appleframework.pay.user.service.RpUserPayConfigService;
import com.gitee.easyopen.AppSecretManager;

@Component
public class DbAppSecretManager implements AppSecretManager {

    @Autowired
    private RpUserPayConfigService rpUserPayConfigService;

	@Override
	public void addAppSecret(Map<String, String> appSecretStore) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSecret(String appKey) {
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(appKey);
		if(null == rpUserPayConfig) {
			return null;
		} else {
			return rpUserPayConfig.getPaySecret(); 
		}
	}

	@Override
	public boolean isValidAppKey(String appKey) {
		RpUserPayConfig rpUserPayConfig = rpUserPayConfigService.getByPayKey(appKey);
		if(null != rpUserPayConfig) {
			return true; 
		} else {
			return false;
		}
	}

}
