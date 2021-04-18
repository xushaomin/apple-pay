package com.appleframework.pay.manager;

import org.springframework.stereotype.Component;

import com.gitee.easyopen.ApiParam;
import com.gitee.easyopen.Signer;

@Component
public class MD5Signer implements Signer {

	@Override
	public boolean isRightSign(ApiParam apiParam, String secret, String signMethod) {
		// TODO Auto-generated method stub
		return false;
	}

}
