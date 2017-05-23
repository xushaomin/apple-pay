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
package com.appleframework.pay.controller.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.appleframework.pay.account.entity.RpAccount;
import com.appleframework.pay.account.entity.RpAccountHistory;
import com.appleframework.pay.account.service.RpAccountHistoryService;
import com.appleframework.pay.account.service.RpAccountService;
import com.appleframework.pay.common.core.page.PageBean;
import com.appleframework.pay.common.core.page.PageParam;
import com.appleframework.pay.user.entity.RpUserInfo;
import com.appleframework.pay.user.service.RpUserInfoService;

/**
 * 账户信息
 * http://www.appleframework.com
 * @author Cruise.Xu
 */
@Controller
@RequestMapping("/account")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AccountController {
	
	@Autowired
	private RpAccountService rpAccountService;
	
	@Autowired
	private RpUserInfoService rpUserInfoService;
	
	@Autowired
	private RpAccountHistoryService rpAccountHistoryService;

	/**
	 * 函数功能说明 ： 查询账户信息
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "/list", method ={RequestMethod.POST,RequestMethod.GET})
	public String list(RpAccount rpAccount,PageParam pageParam, Model model) {
		PageBean pageBean = rpAccountService.listPage(pageParam, rpAccount);
		List<Object> recordList = pageBean.getRecordList();
		for(Object obj : recordList){
			RpAccount account = (RpAccount)obj;
			RpUserInfo userInfo = rpUserInfoService.getDataByMerchentNo(account.getUserNo());
			account.setUserName(userInfo.getUserName());
		}
		model.addAttribute("pageBean", pageBean);
        model.addAttribute("pageParam", pageParam);
        model.addAttribute("rpAccount",rpAccount);
		return "account/list";
	}

	/**
	 * 函数功能说明 ： 查询账户历史信息
	 * 
	 * @参数： @return
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = "/historyList", method ={RequestMethod.POST,RequestMethod.GET})
	public String historyList(RpAccountHistory rpAccountHistory,PageParam pageParam, Model model) {
		PageBean pageBean = rpAccountHistoryService.listPage(pageParam, rpAccountHistory);
		List<Object> recordList = pageBean.getRecordList();
		for(Object obj : recordList){
			RpAccountHistory history = (RpAccountHistory)obj;
			RpUserInfo userInfo = rpUserInfoService.getDataByMerchentNo(history.getUserNo());
			history.setUserName(userInfo.getUserName());
		}
		model.addAttribute("pageBean", pageBean);
        model.addAttribute("pageParam", pageParam);
        model.addAttribute("rpAccountHistory",rpAccountHistory);
		return "account/historyList";
	}
}
