/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.service.TenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TenantUserController
 * @Description: 租户下的用户管理
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:50
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "租户下的用户管理", tags = "租户下的用户管理", modelName = "租户管理")
public class TenantUserController {

    @Autowired
    private TenantUserService tenantUserService;

    @ApiOperation(id = "queryTenantUserList", value = "分页查询租户下的用户信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantUserController/queryTenantUserList")
    public void queryTenantUserList(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "removeTenantUserByStaffId", value = "根据员工ID移除租户用户", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工ID", required = "required")})
    @RequestMapping("/post/TenantUserController/removeTenantUserByStaffId")
    public void removeTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.removeTenantUserByStaffId(inputObject, outputObject);
    }

    @ApiOperation(id = "exitTenantUser", value = "用户自主退出租户", method = "POST", allUse = "2")
    @RequestMapping("/post/TenantUserController/exitTenantUser")
    public void exitTenantUser(InputObject inputObject, OutputObject outputObject) {
        tenantUserService.exitTenantUser(inputObject, outputObject);
    }
}
