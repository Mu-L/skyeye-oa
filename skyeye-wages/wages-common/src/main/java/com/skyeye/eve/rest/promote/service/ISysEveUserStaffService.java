/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.rest.promote.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;

public interface ISysEveUserStaffService extends IService {

    List<String> queryTenantUserStaffIdByTenantId(String tenantId);

}
