/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.controller;

import com.skyeye.activiti.service.ActivitiTaskService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ActivitiTaskController
 * @Description: 工作流用户任务相关
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工作流用户任务操作", tags = "工作流用户任务操作", modelName = "工作流模块")
public class ActivitiTaskController {

    @Autowired
    private ActivitiTaskService activitiTaskService;

    @ApiOperation(id = "activitimode008", value = "获取我的待办任务", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActivitiTaskController/queryUserAgencyTasksListByUserId")
    public void queryUserAgencyTasksListByUserId(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.queryUserAgencyTasksListByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode013", value = "获取我的流程", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActivitiTaskController/queryStartProcessNotSubByUserId")
    public void queryStartProcessNotSubByUserId(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.queryStartProcessNotSubByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode014", value = "获取我的历史审批任务", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActivitiTaskController/queryMyHistoryTaskByUserId")
    public void queryMyHistoryTaskByUserId(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.queryMyHistoryTaskByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode017", value = "获取历史审批列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/queryApprovalTasksHistoryByProcessInstanceId")
    public void queryApprovalTasksHistoryByProcessInstanceId(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.queryApprovalTasksHistoryByProcessInstanceId(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode018", value = "获取所有已完成的流程信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActivitiTaskController/queryAllComplateProcessList")
    public void queryAllComplateProcessList(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.queryAllComplateProcessList(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode019", value = "获取所有待办的流程信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActivitiTaskController/queryAllConductProcessList")
    public void queryAllConductProcessList(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.queryAllConductProcessList(inputObject, outputObject);
    }

    @ApiOperation(id = "activitimode016", value = "根据taskId获取表单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程id", required = "required"),
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/querySubFormMationByTaskId")
    public void querySubFormMationByTaskId(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.querySubFormMationByTaskId(inputObject, outputObject);
    }

    @RequestMapping("/post/ActivitiTaskController/editActivitiModelToRun")
    public void editActivitiModelToRun(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.editActivitiModelToRun(inputObject, outputObject);
    }

    @ApiOperation(id = "activitiTask001", value = "委派", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "principalUserId", name = "principalUserId", value = "被委托人id", required = "required"),
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/delegateTask")
    public void delegateTask(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.delegateTask(inputObject, outputObject);
    }

    @ApiOperation(id = "activitiTask002", value = "委派", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "transferredPersonId", name = "transferredPersonId", value = "被转办人id", required = "required"),
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/transferTask")
    public void transferTask(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.transferTask(inputObject, outputObject);
    }

    @ApiOperation(id = "activitiTask003", value = "前加签", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "chooseUserMation", name = "chooseUserMation", value = "加签人的用户json串", required = "required,json"),
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/beforeAddSignTask")
    public void beforeAddSignTask(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.beforeAddSignTask(inputObject, outputObject);
    }

    @ApiOperation(id = "activitiTask004", value = "后加签", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "chooseUserMation", name = "chooseUserMation", value = "加签人的用户json串", required = "required,json"),
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/afterAddSignTask")
    public void afterAddSignTask(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.afterAddSignTask(inputObject, outputObject);
    }

    @ApiOperation(id = "activitiTask006", value = "获取会签节点的数据信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/jointlySignTaskDetail")
    public void jointlySignTaskDetail(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.jointlySignTaskDetail(inputObject, outputObject);
    }

    @ApiOperation(id = "activitiTask005", value = "会签加减签", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "chooseUserMation", name = "chooseUserMation", value = "加签人的用户json串", required = "required,json"),
        @ApiImplicitParam(id = "taskId", name = "taskId", value = "任务id", required = "required")})
    @RequestMapping("/post/ActivitiTaskController/jointlySignAddSignTask")
    public void jointlySignAddSignTask(InputObject inputObject, OutputObject outputObject) {
        activitiTaskService.jointlySignAddSignTask(inputObject, outputObject);
    }

}
