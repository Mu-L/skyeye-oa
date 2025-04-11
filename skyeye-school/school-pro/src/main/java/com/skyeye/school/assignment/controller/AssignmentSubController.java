/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.assignment.entity.AssignmentSub;
import com.skyeye.school.assignment.service.AssignmentSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AssignmentSubController
 * @Description: 作业提交控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 11:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "作业提交", tags = "作业提交", modelName = "作业管理")
public class AssignmentSubController {

    @Autowired
    private AssignmentSubService assignmentSubService;

    @ApiOperation(id = "writeAssignmentSub", value = "提交作业信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AssignmentSub.class)
    @RequestMapping("/post/AssignmentSubController/writeAssignmentSub")
    public void writeAssignmentSub(InputObject inputObject, OutputObject outputObject) {
        assignmentSubService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssignmentSubById", value = "根据id查询作业提交信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssignmentSubController/queryAssignmentSubById")
    public void queryAssignmentSubById(InputObject inputObject, OutputObject outputObject) {
        assignmentSubService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAssignmentSubById", value = "根据ID删除作业提交信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssignmentSubController/deleteAssignmentSubById")
    public void deleteAssignmentSubById(InputObject inputObject, OutputObject outputObject) {
        assignmentSubService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssignmentStuSubListByAssignmentId", value = "根据作业id获取学生提交信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "assignmentId", name = "assignmentId", value = "作业id", required = "required")})
    @RequestMapping("/post/AssignmentSubController/queryAssignmentStuSubListByAssignmentId")
    public void queryAssignmentStuSubListByAssignmentId(InputObject inputObject, OutputObject outputObject) {
        assignmentSubService.queryAssignmentStuSubListByAssignmentId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssignmentSubListByAssignmentId", value = "根据作业id获取已经提交的学生信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "assignmentId", name = "assignmentId", value = "作业id")})
    @RequestMapping("/post/AssignmentSubController/queryAssignmentSubListByAssignmentId")
    public void queryAssignmentSubListByAssignmentId(InputObject inputObject, OutputObject outputObject) {
        assignmentSubService.queryAssignmentSubListByAssignmentId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssignmentNotSubListByAssignmentId", value = "根据作业id获取未提交的学生信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "assignmentId", name = "assignmentId", value = "作业id")})
    @RequestMapping("/post/AssignmentSubController/queryAssignmentNotSubListByAssignmentId")
    public void queryAssignmentNotSubListByAssignmentId(InputObject inputObject, OutputObject outputObject) {
        assignmentSubService.queryAssignmentNotSubListByAssignmentId(inputObject, outputObject);
    }

    @ApiOperation(id = "readOverAssignmentSubById", value = "批阅作业", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "score", name = "score", value = "得分", required = "required"),
        @ApiImplicitParam(id = "comment", name = "comment", value = "评语")})
    @RequestMapping("/post/AssignmentSubController/readOverAssignmentSubById")
    public void readOverAssignmentSubById(InputObject inputObject, OutputObject outputObject) {
        assignmentSubService.readOverAssignmentSubById(inputObject, outputObject);
    }

}
