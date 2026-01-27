/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolTask;
import com.skyeye.patrol.service.PatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolTaskController
 * @Description: 巡检任务控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检任务", tags = "巡检任务", modelName = "巡检任务")
public class PatrolTaskController {

    @Autowired
    private PatrolTaskService patrolTaskService;

    @ApiOperation(id = "queryPatrolTaskList", value = "获取巡检任务列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolTaskController/queryPatrolTaskList")
    public void queryPatrolTaskList(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePatrolTask", value = "新增/编辑巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatrolTask.class)
    @RequestMapping("/post/PatrolTaskController/writePatrolTask")
    public void writePatrolTask(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPatrolTaskById", value = "根据ID查询巡检任务详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolTaskController/queryPatrolTaskById")
    public void queryPatrolTaskById(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePatrolTaskById", value = "根据ID删除巡检任务", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolTaskController/deletePatrolTaskById")
    public void deletePatrolTaskById(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "startPatrolTask", value = "开始执行巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/PatrolTaskController/startPatrolTask")
    public void startPatrolTask(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.startTask(inputObject, outputObject);
    }

    @ApiOperation(id = "completePatrolTask", value = "完成巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/PatrolTaskController/completePatrolTask")
    public void completePatrolTask(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.completeTask(inputObject, outputObject);
    }

    @ApiOperation(id = "cancelPatrolTask", value = "取消巡检任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required")})
    @RequestMapping("/post/PatrolTaskController/cancelPatrolTask")
    public void cancelPatrolTask(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.cancelTask(inputObject, outputObject);
    }

    @ApiOperation(id = "reassignTimeoutTask", value = "重新分配超时任务", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "任务ID", required = "required"),
        @ApiImplicitParam(id = "executorId", name = "executorId", value = "执行人ID（员工ID），可选"),
        @ApiImplicitParam(id = "plannedStartTime", name = "plannedStartTime", value = "计划开始执行时间，可选")})
    @RequestMapping("/post/PatrolTaskController/reassignTimeoutTask")
    public void reassignTimeoutTask(InputObject inputObject, OutputObject outputObject) {
        patrolTaskService.reassignTimeoutTask(inputObject, outputObject);
    }
}

