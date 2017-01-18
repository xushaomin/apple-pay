package com.appleframework.pay.resolver;/*
 * Copyright 2015-2102 RonCoo(http://www.appleframework.com) Group.
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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.appleframework.pay.common.core.exception.BizException;

/**
 * <b>功能说明:
 * </b>
 *
 * @author Peter
 *         <a href="http://www.appleframework.com">appleframework(http://www.appleframework.com)</a>
 */
public class PayExceptionResolver implements HandlerExceptionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(PayExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        if (BizException.class.isAssignableFrom(ex.getClass())) {//如果是业务异常类
            BizException bizException = (BizException) ex;
            try {
                //response.setContentType("text/text;charset=UTF-8");
                //JsonUtils.responseJson(response, bizException.getMsg());
                 
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("code", bizException.getCode());//将错误信息传递给view
                map.put("message", bizException.getMsg());
                return new ModelAndView(new MappingJacksonJsonView(),map);
            } catch (Exception e) {
                LOG.error("系统异常:", e);

            }
        }
        return null;
    }
}
