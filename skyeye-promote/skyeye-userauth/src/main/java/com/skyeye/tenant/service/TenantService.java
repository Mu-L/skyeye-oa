/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.Tenant;

import java.util.List;

/**
 * @ClassName: TenantService
 * @Description: 租户服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/28 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TenantService extends SkyeyeBusinessService<Tenant> {

    void editTenantAccountNumber(String tenantId, Integer accountNumber);

    void queryAllTenantList(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据租户id查询所有菜单id列表
     *
     * @param tenantId 租户id
     * @param type     菜单类型 {@link com.skyeye.tenant.classenum.TenantAppMenuType}
     * @return
     */
    List<String> queryAllMenuListByTenantId(String tenantId, Integer type);

    void checkTenantAccountNum(String tenantId);

    void queryAllTenantListByKeyword(InputObject inputObject, OutputObject outputObject);
}
