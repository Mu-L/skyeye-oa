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
import com.skyeye.eve.service.ActGroupUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ActGroupUserController
 * @Description: 用户组关联用户管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/12 14:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用户组关联用户管理", tags = "用户组关联用户管理", modelName = "用户组关联用户管理")
public class ActGroupUserController {

    @Autowired
    private ActGroupUserService actGroupService;

    @ApiOperation(id = "insertActGroupUser", value = "给用户组新增用户", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "groupId", name = "groupId", value = "用户组id", required = "required"),
        @ApiImplicitParam(id = "userIds", name = "userIds", value = "用户id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/ActGroupUserController/insertActGroupUser")
    public void insertActGroupUser(InputObject inputObject, OutputObject outputObject) {
        actGroupService.insertActGroupUser(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteActGroupUserById", value = "移除用户组中的某个用户", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ActGroupUserController/deleteActGroupUserById")
    public void deleteActGroupUserById(InputObject inputObject, OutputObject outputObject) {
        actGroupService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryUserInfoOnActGroup", value = "展示用户组的用户信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActGroupUserController/queryUserInfoOnActGroup")
    public void queryUserInfoOnActGroup(InputObject inputObject, OutputObject outputObject) {
        actGroupService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAllActGroupUserByGroupId", value = "一键移除指定用户组下的所有用户", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "groupId", name = "groupId", value = "用户组id", required = "required")})
    @RequestMapping("/post/ActGroupUserController/deleteAllActGroupUserByGroupId")
    public void deleteAllActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) {
        actGroupService.deleteAllActGroupUserByGroupId(inputObject, outputObject);
    }
}
