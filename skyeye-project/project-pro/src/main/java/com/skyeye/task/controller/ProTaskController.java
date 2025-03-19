/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.task.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.task.entity.Task;
import com.skyeye.task.service.ProTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProTaskController
 * @Description: 项目任务管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 20:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "任务管理", tags = "任务管理", modelName = "任务管理")
public class ProTaskController {

    @Autowired
    private ProTaskService proTaskService;

    @ApiOperation(id = "queryProTaskList", value = "获取任务列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProTaskController/queryProTaskList")
    public void queryProTaskList(InputObject inputObject, OutputObject outputObject) {
        proTaskService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeTask", value = "新增/编辑任务管理", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Task.class)
    @RequestMapping("/post/ProTaskController/writeTask")
    public void writeTask(InputObject inputObject, OutputObject outputObject) {
        proTaskService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProTaskById", value = "删除任务信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProTaskController/deleteProTaskById")
    public void deleteProTaskById(InputObject inputObject, OutputObject outputObject) {
        proTaskService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeTask", value = "撤销任务审批申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProTaskController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        proTaskService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalTask", value = "任务提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "approvalId", name = "approvalId", value = "审批人", required = "required")})
    @RequestMapping("/post/ProTaskController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        proTaskService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidTask", value = "作废任务", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProTaskController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        proTaskService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "executionTask", value = "任务开始执行", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProTaskController/executionTask")
    public void executionTask(InputObject inputObject, OutputObject outputObject) {
        proTaskService.executionTask(inputObject, outputObject);
    }

    @ApiOperation(id = "complateTask", value = "任务执行完成", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "actualWorkload", name = "actualWorkload", value = "实际工作量", required = "required"),
        @ApiImplicitParam(id = "executionResult", name = "executionResult", value = "执行结果", required = "required"),
        @ApiImplicitParam(id = "executionEnclosureInfo", name = "executionEnclosureInfo", value = "执行结果附件", required = "json")})
    @RequestMapping("/post/ProTaskController/complateTask")
    public void complateTask(InputObject inputObject, OutputObject outputObject) {
        proTaskService.complateTask(inputObject, outputObject);
    }

    @ApiOperation(id = "closeTask", value = "任务关闭", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProTaskController/closeTask")
    public void closeTask(InputObject inputObject, OutputObject outputObject) {
        proTaskService.closeTask(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProTaskListForGantt", value = "获取任务列表(甘特图/看板)", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/ProTaskController/queryProTaskListForGantt")
    public void queryProTaskListForGantt(InputObject inputObject, OutputObject outputObject) {
        proTaskService.queryProTaskListForGantt(inputObject, outputObject);
    }

}
