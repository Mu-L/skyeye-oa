/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CompanyTalkGroupInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CompanyTalkGroupInviteController
 * @Description: 群组邀请管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 19:42
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "群组邀请管理", tags = "群组邀请管理", modelName = "聊天模块")
public class CompanyTalkGroupInviteController {

    @Autowired
    private CompanyTalkGroupInviteService companyTalkGroupInviteService;

    @ApiOperation(id = "companytalkgroup002", value = "获取邀请信息/入群信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CompanyTalkGroupInviteController/queryGroupInvitationMation")
    public void queryGroupInvitationMation(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupInviteService.queryGroupInvitationMation(inputObject, outputObject);
    }

    @ApiOperation(id = "companytalkgroup003", value = "同意入群", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CompanyTalkGroupInviteController/editAgreeInGroupInvitationMation")
    public void editAgreeInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupInviteService.editAgreeInGroupInvitationMation(inputObject, outputObject);
    }

    @ApiOperation(id = "companytalkgroup004", value = "拒绝入群", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CompanyTalkGroupInviteController/editRefuseInGroupInvitationMation")
    public void editRefuseInGroupInvitationMation(InputObject inputObject, OutputObject outputObject) {
        companyTalkGroupInviteService.editRefuseInGroupInvitationMation(inputObject, outputObject);
    }

}
