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
package com.appleframework.pay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <b>功能说明:
 *      该控制类用来支撑商户查询订单结果
 * </b>
 *
 * @author Peter
 *         <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
@Controller
@RequestMapping("merchantOrderQuery")
public class MerchantOrderQueryController {

    @RequestMapping
    public void singleOrderQuery(HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse){

    }
}
