/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.groups.entity.GroupsStudent;
import com.skyeye.school.groups.service.GroupsStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "学生与分组的关系管理", tags = "学生与分组的关系管理", modelName = "分组管理")
public class GroupsStudentController {

    @Autowired
    private GroupsStudentService groupsStudentService;

    @ApiOperation(id = "joinGroups", value = "加入分组", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = GroupsStudent.class)
    @RequestMapping("/post/GroupsStudentController/joinGroups")
    public void joinGroups(InputObject inputObject, OutputObject outputObject) {
        groupsStudentService.joinGroups(inputObject, outputObject);
    }

    @ApiOperation(id = "exitGroups", value = "退出分组", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "groupsId", name = "groupsId", value = "分组id", required = "required")})
    @RequestMapping("/post/GroupsStudentController/exitGroups")
    public void exitGroups(InputObject inputObject, OutputObject outputObject) {
        groupsStudentService.exitGroups(inputObject, outputObject);
    }

}
