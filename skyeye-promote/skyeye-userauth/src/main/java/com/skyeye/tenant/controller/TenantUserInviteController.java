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
import com.skyeye.tenant.entity.TenantUserInvite;
import com.skyeye.tenant.service.TenantUserInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TenantUserInviteController
 * @Description: 租户与用户邀请关系管理
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/27 8:30
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "租户与用户邀请关系管理", tags = "租户与用户邀请关系管理", modelName = "租户管理")
public class TenantUserInviteController {

    @Autowired
    private TenantUserInviteService tenantUserInviteService;

    @ApiOperation(id = "queryTenantUserInviteList", value = "分页查询租户与用户邀请关系列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantUserInviteController/queryTenantUserInviteList")
    public void queryTenantUserInviteList(InputObject inputObject, OutputObject outputObject) {
        tenantUserInviteService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "inviteUsersToJoin", value = "邀请用户加入租户", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TenantUserInvite.class)
    @RequestMapping("/post/TenantUserInviteController/inviteUsersToJoin")
    public void inviteUsersToJoin(InputObject inputObject, OutputObject outputObject) {
        tenantUserInviteService.inviteUsersToJoin(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelInviteUsersToJoin", value = "作废该邀请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/TenantUserInviteController/cancelInviteUsersToJoin")
    public void cancelInviteUsersToJoin(InputObject inputObject, OutputObject outputObject) {
        tenantUserInviteService.cancelInviteUsersToJoin(inputObject, outputObject);
    }

    @ApiOperation(id = "resendInviteUsersToJoin", value = "重新发送邀请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/TenantUserInviteController/resendInviteUsersToJoin")
    public void resendInviteUsersToJoin(InputObject inputObject, OutputObject outputObject) {
        tenantUserInviteService.resendInviteUsersToJoin(inputObject, outputObject);
    }

    @ApiOperation(id = "joinTenantByInvite", value = "通过邀请加入租户", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "userName", name = "userName", value = "姓名", required = "required"),
        @ApiImplicitParam(id = "userSex", name = "userSex", value = "性别", required = "required"),
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户id", required = "required"),
        @ApiImplicitParam(id = "userIdCard", name = "userIdCard", value = "身份证号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),})
    @RequestMapping("/post/TenantUserInviteController/joinTenantByInvite")
    public void joinTenantByInvite(InputObject inputObject, OutputObject outputObject) {
        tenantUserInviteService.joinTenantByInvite(inputObject, outputObject);
    }

}
