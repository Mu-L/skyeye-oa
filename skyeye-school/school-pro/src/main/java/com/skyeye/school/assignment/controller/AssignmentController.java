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
import com.skyeye.school.assignment.entity.Assignment;
import com.skyeye.school.assignment.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AssignmentController
 * @Description: 作业管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:47
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "作业管理", tags = "作业管理", modelName = "作业管理")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @ApiOperation(id = "writeAssignment", value = "新增/编辑作业信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Assignment.class)
    @RequestMapping("/post/AssignmentController/writeAssignment")
    public void writeAssignment(InputObject inputObject, OutputObject outputObject) {
        assignmentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssignmentById", value = "根据id查询作业信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssignmentController/queryAssignmentById")
    public void queryAssignmentById(InputObject inputObject, OutputObject outputObject) {
        assignmentService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAssignmentById", value = "根据ID删除作业信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssignmentController/deleteAssignmentById")
    public void deleteAssignmentById(InputObject inputObject, OutputObject outputObject) {
        assignmentService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssignmentListBySubjectClassesId", value = "根据科目表与班级表的关系id获取作业列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectClassesId", name = "subjectClassesId", value = "科目表与班级表的关系id", required = "required")})
    @RequestMapping("/post/AssignmentController/queryAssignmentListBySubjectClassesId")
    public void queryAssignmentListBySubjectClassesId(InputObject inputObject, OutputObject outputObject) {
        assignmentService.queryAssignmentListBySubjectClassesId(inputObject, outputObject);
    }

}
