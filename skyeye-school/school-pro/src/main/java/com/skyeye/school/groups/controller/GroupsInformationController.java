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


}
