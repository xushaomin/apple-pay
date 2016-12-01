package com.appleframework.pay;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.pay.trade.service.RpTradePaymentManagerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/*.xml" })
public class RpTradePaymentManagerServiceTest {
	
	@Resource
	private RpTradePaymentManagerService rpTradePaymentManagerService;

	@Test
	public void testAddOpinion1() {
		try {			
	        Map<String , String> notifyMap = new HashMap<String , String >();
	        notifyMap.put("is_subscribe", "N");
	        notifyMap.put("appid", "wx9bcd824c00ba56d4");
	        notifyMap.put("fee_type", "CNY");
	        notifyMap.put("nonce_str", "086de77d98d14b3f951cf58212d9e62c"); 
	        notifyMap.put("out_trade_no", "66662016110310000004"); 
	        notifyMap.put("device_info", "WEB"); 
	        notifyMap.put("transaction_id", "4005932001201611038610071821"); 
	        notifyMap.put("trade_type", "NATIVE"); 
	        notifyMap.put("sign", "EE29A3A4518A2F43321EDF1E34C494DC"); 
	        notifyMap.put("result_code", "SUCCESS"); 
	      	notifyMap.put("mch_id", "1383074802"); 
	      	notifyMap.put("total_fee", "10"); 
	        notifyMap.put("time_end", "20161103145613"); 
	        notifyMap.put("openid", "oTlmCwWJP6SZ4i9GTGSNHir9XL5I"); 
	        notifyMap.put(" bank_type", "CFT"); 
	        notifyMap.put("return_code", "SUCCESS"); 
	        notifyMap.put("cash_fee", "10");
			rpTradePaymentManagerService.completeScanPay("WEIXIN", notifyMap);
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
