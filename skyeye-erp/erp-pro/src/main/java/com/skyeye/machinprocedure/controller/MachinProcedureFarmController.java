/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MachinProcedureFarmController
 * @Description: 车间任务控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 20:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车间任务", tags = "车间任务", modelName = "车间任务")
public class MachinProcedureFarmController {

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @ApiOperation(id = "queryMachinProcedureFarmList", value = "获取车间任务信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MachinProcedureFarmController/queryMachinProcedureFarmList")
    public void queryMachinProcedureFarmList(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "receiveMachinProcedureFarm", value = "车间任务接收", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "planStartTime", name = "planStartTime", required = "required", value = "计划开始时间，格式yyyy-MM-dd HH:mm:ss"),
        @ApiImplicitParam(id = "planEndTime", name = "planEndTime", required = "required", value = "计划结束时间，格式yyyy-MM-dd HH:mm:ss")})
    @RequestMapping("/post/MachinProcedureFarmController/receiveMachinProcedureFarm")
    public void receiveMachinProcedureFarm(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.receiveMachinProcedureFarm(inputObject, outputObject);
    }

    @ApiOperation(id = "receptionReceiveMachinProcedureFarm", value = "车间任务反接收", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinProcedureFarmController/receptionReceiveMachinProcedureFarm")
    public void receptionReceiveMachinProcedureFarm(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.receptionReceiveMachinProcedureFarm(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMachinProcedureFarmToInOrOutList", value = "车间任务转加工入库单时，根据车间任务id查询对应的成品信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinProcedureFarmController/queryMachinProcedureFarmToInOrOutList")
    public void queryMachinProcedureFarmToInOrOutList(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.queryMachinProcedureFarmToInOrOutList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProcedureConsumablesByFarmId", value = "车间任务转工序验收时，根据车间任务id查询该工序的耗材信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "车间任务id", required = "required"),
        @ApiImplicitParam(id = "targetNum", name = "targetNum", value = "目标数量", required = "required")})
    @RequestMapping("/post/MachinProcedureFarmController/queryProcedureConsumablesByFarmId")
    public void queryProcedureConsumablesByFarmId(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.queryProcedureConsumablesByFarmId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPendingAcceptNumByFarmId", value = "根据车间任务id获取待工序验收的数量", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "车间任务id", required = "required")})
    @RequestMapping("/post/MachinProcedureFarmController/queryPendingAcceptNumByFarmId")
    public void queryPendingAcceptNumByFarmId(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.queryPendingAcceptNumByFarmId(inputObject, outputObject);
    }

    @ApiOperation(id = "editMachinProcedureFarmInfo", value = "修改车间任务信息（仅待执行、部分完成可操作）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "车间任务id", required = "required"),
        @ApiImplicitParam(id = "planStartTime", name = "planStartTime", value = "计划开始时间，格式yyyy-MM-dd HH:mm:ss"),
        @ApiImplicitParam(id = "planEndTime", name = "planEndTime", value = "计划结束时间，格式yyyy-MM-dd HH:mm:ss")})
    @RequestMapping("/post/MachinProcedureFarmController/editMachinProcedureFarmInfo")
    public void editMachinProcedureFarmInfo(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.editMachinProcedureFarmInfo(inputObject, outputObject);
    }

    @ApiOperation(id = "queryGanttListByMonth", value = "按月份查询车间任务列表（排产甘特图）", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "yearMonth", name = "yearMonth", value = "年月，格式 yyyy-MM", required = "required"),
        @ApiImplicitParam(id = "type", name = "type", value = "farm=指定车间 department=本部门车间 all=全部", required = "required", defaultValue = "department"),
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "当 type=farm 时为车间 id")})
    @RequestMapping("/post/MachinProcedureFarmController/queryGanttListByMonth")
    public void queryGanttListByMonth(InputObject inputObject, OutputObject outputObject) {
        machinProcedureFarmService.queryGanttListByMonth(inputObject, outputObject);
    }

}
