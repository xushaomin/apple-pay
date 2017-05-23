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
package com.appleframework.pay.reconciliation.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appleframework.pay.common.core.page.PageBean;
import com.appleframework.pay.common.core.page.PageParam;
import com.appleframework.pay.reconciliation.dao.RpAccountCheckMistakeScratchPoolDao;
import com.appleframework.pay.reconciliation.entity.RpAccountCheckMistakeScratchPool;
import com.appleframework.pay.reconciliation.service.RpAccountCheckMistakeScratchPoolService;

/**
 * 对账暂存池接口实现 .
 *
 * http://www.appleframework.com
 * 
 * @author  Cruise.Xu
 */
@Service("rpAccountCheckMistakeScratchPoolService")
public class RpAccountCheckMistakeScratchPoolServiceImpl implements RpAccountCheckMistakeScratchPoolService {

	@Autowired
	private RpAccountCheckMistakeScratchPoolDao rpAccountCheckMistakeScratchPoolDao;

	@Override
	public void saveData(RpAccountCheckMistakeScratchPool RpAccountCheckMistakeScratchPool) {
		rpAccountCheckMistakeScratchPoolDao.insert(RpAccountCheckMistakeScratchPool);
	}

	/**
	 * 批量保存记录
	 * 
	 * @param ScratchPoolList
	 */
	public void savaListDate(List<RpAccountCheckMistakeScratchPool> scratchPoolList) {
		for (RpAccountCheckMistakeScratchPool record : scratchPoolList) {
			rpAccountCheckMistakeScratchPoolDao.insert(record);
		}
	}

	@Override
	public void updateData(RpAccountCheckMistakeScratchPool RpAccountCheckMistakeScratchPool) {
		rpAccountCheckMistakeScratchPoolDao.update(RpAccountCheckMistakeScratchPool);
	}

	@Override
	public RpAccountCheckMistakeScratchPool getDataById(String id) {
		return rpAccountCheckMistakeScratchPoolDao.getById(id);
	}

	/**
	 * 获取分页数据
	 * 
	 * @param pageParam
	 * @return
	 */
	public PageBean listPage(PageParam pageParam, RpAccountCheckMistakeScratchPool rpAccountCheckMistakeScratchPool) {

		Map<String, Object> paramMap = new HashMap<String, Object>();

		return rpAccountCheckMistakeScratchPoolDao.listPage(pageParam, paramMap);
	}

	/**
	 * 从缓冲池中删除数据
	 * 
	 * @param scratchPoolList
	 */
	public void deleteFromPool(List<RpAccountCheckMistakeScratchPool> scratchPoolList) {
		for (RpAccountCheckMistakeScratchPool record : scratchPoolList) {
			rpAccountCheckMistakeScratchPoolDao.delete(record.getId());
		}

	}

	/**
	 * 查询出缓存池中所有的数据
	 * 
	 * @return
	 */
	public List<RpAccountCheckMistakeScratchPool> listScratchPoolRecord(Map<String, Object> paramMap) {
		if (paramMap == null) {
			paramMap = new HashMap<String, Object>();
		}
		return rpAccountCheckMistakeScratchPoolDao.listByColumn(paramMap);
	}
}