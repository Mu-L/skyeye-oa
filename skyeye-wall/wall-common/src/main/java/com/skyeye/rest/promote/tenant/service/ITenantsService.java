/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.promote.tenant.service;

import com.skyeye.base.rest.service.IService;

import java.util.Map;

public interface ITenantsService extends IService {
  Map<String,Object> queryTenantById(String id);
}
