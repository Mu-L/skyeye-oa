/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.pro.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

public interface ISysEveUserStaffService extends IService {

    List<String> queryTenantUserStaffIdByTenantId(String tenantId);

    List<String> queryTenantUserUserIdByTenantId(String tenantId);

    List<Map<String, Object>> queryTenantUserByTenantId(String tenantId, List<String> stateList);

}
