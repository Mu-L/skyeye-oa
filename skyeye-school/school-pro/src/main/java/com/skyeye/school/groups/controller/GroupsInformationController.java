/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.groups.entity.GroupsInformation;
import com.skyeye.school.groups.service.GroupsInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: GroupsInformationController
 * @Description: 学生分组信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/10 10:30
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "学生分组信息管理", tags = "学生分组信息管理", modelName = "分组管理")
public class GroupsInformationController {

    @Autowired
    private GroupsInformationService groupsInformationService;

    @ApiOperation(id = "queryGroupsInformationList", value = "获取学生分组信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/GroupsInformationController/queryGroupsInformationList")
    public void queryGroupsInformationList(InputObject inputObject, OutputObject outputObject) {
        groupsInformationService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeGroupsInformation", value = "添加或修改分组信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = GroupsInformation.class)
    @RequestMapping("/post/GroupsInformationController/writeGroupsInformation")
    public void writeGroupsInformation(InputObject inputObject, OutputObject outputObject) {
        groupsInformationService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteGroupsInformationById", value = "根据ID删除学生分组表", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/GroupsInformationController/deleteGroupsInformationById")
    public void deleteGroupsInformationById(InputObject inputObject, OutputObject outputObject) {
        groupsInformationService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryGroupsInformationById", value = "根据ID查询学生分组表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/GroupsInformationController/queryGroupsInformationById")
    public void queryGroupsInformationById(InputObject inputObject, OutputObject outputObject) {
        groupsInformationService.selectById(inputObject, outputObject);
    }

}
