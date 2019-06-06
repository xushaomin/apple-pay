package com.appleframework.pay;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.appleframework.pay.trade.service.RpTradeTransferManagerService;
import com.appleframework.pay.user.service.BuildNoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/*.xml" })
public class RpTradeTransferManagerServiceTest {
	
	@Resource
	private RpTradeTransferManagerService rpTradeTransferManagerService;

	@Autowired
    private BuildNoService buildNoService;
	
	@Test
	public void testAddOpinion1() {
		try {	        
	        String payKey = "9bc4c9aa98c94437bedc8e1668ead586";
	        String orderName = "转账"; 
	        String orderNo = buildNoService.buildTrxNo().toString(); 
	        Date orderDate = new Date();
			Date orderTime = new Date();
			BigDecimal orderPrice = new BigDecimal(0.1); 
			String payWayCode = "ALIPAY";
			String orderIp = "192.168.1.11"; 
			Integer orderPeriod = 100;
			String remark = "转账测试";
			String payeeAccount = "xushaomin1122@163.com"; 
			String payeeName = null;
	        
	        rpTradeTransferManagerService.initDirectTransfer(
	        		payKey, payeeAccount, payeeName, 
	        		orderName, orderNo, orderDate, orderTime, orderPrice, 
	        		payWayCode, orderIp, orderPeriod, remark);
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
