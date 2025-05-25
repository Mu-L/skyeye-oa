/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.pro.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffRest {

    /**
     * 根据租户ID获取租户下的员工ID信息
     *
     * @param tenantId  租户ID
     * @param stateList 员工状态列表，多个逗号分隔
     * @return 租户下的员工信息【集合】
     */
    @PostMapping("/queryTenantUserStaffIdByTenantId")
    String queryTenantUserStaffIdByTenantId(@RequestParam("tenantId") String tenantId,
                                            @RequestParam("stateList") String stateList);

}
