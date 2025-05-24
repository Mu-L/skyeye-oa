/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.rest.promote.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffRest {

    /**
     * 根据租户ID获取租户下的员工ID信息
     *
     * @param tenantId 租户ID
     * @return 租户下的员工信息【集合】
     */
    @PostMapping("/queryTenantUserStaffIdByTenantId")
    String queryTenantUserStaffIdByTenantId(@RequestParam("tenantId") String tenantId);

    /**
     * 修改员工薪资设定信息
     *
     * @param params 参数信息：
     *               staffId：员工id--必填
     *               actMoney：实际薪资--必填
     */
    @PostMapping("/editSysUserStaffActMoneyById")
    String editSysUserStaffActMoneyById(Map<String, Object> params);
}
