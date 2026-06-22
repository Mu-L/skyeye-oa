/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.repair.entity.EquipmentRepairAcceptance;
import com.skyeye.repair.entity.EquipmentRepairAuditDispatch;
import com.skyeye.repair.entity.EquipmentRepairEvaluate;
import com.skyeye.repair.entity.EquipmentRepairFaultReport;
import com.skyeye.repair.entity.EquipmentRepairResult;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备维修单控制层（分阶段编辑，参考工单管理）
 */
@RestController
@Api(value = "设备维修单", tags = "设备维修单", modelName = "设备维修单")
public class EquipmentRepairOrderController {

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @ApiOperation(id = "queryEquipmentRepairOrderList", value = "获取设备维修单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentRepairOrderController/queryEquipmentRepairOrderList")
    public void queryEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEquipmentRepairOrderList", value = "获取所有设备维修单（无分页无筛选）", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentRepairOrderController/queryAllEquipmentRepairOrderList")
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryAllEquipmentRepairOrderList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertEquipmentRepairOrder", value = "新增设备维修单（故障报修）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairFaultReport.class)
    @RequestMapping("/post/EquipmentRepairOrderController/insertEquipmentRepairOrder")
    public void insertEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.insertEquipmentRepairOrder(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairFaultReport", value = "编辑故障报修", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairFaultReport.class)
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairFaultReport")
    public void editEquipmentRepairFaultReport(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairFaultReport(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairAuditDispatch", value = "编辑审核派工", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairAuditDispatch.class)
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairAuditDispatch")
    public void editEquipmentRepairAuditDispatch(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairAuditDispatch(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairWaitToWorkMation", value = "维修单派工", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "serviceUserId", name = "serviceUserId", value = "接收人", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairWaitToWorkMation")
    public void editEquipmentRepairWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairWaitToWorkMation(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairResult", value = "编辑维修结果并提交完工", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairResult.class)
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairResult")
    public void editEquipmentRepairResult(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairResult(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairEvaluate", value = "编辑待评价并提交", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairEvaluate.class)
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairEvaluate")
    public void editEquipmentRepairEvaluate(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairEvaluate(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairAcceptance", value = "编辑结果验收并审核完工", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairAcceptance.class)
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairAcceptance")
    public void editEquipmentRepairAcceptance(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairAcceptance(inputObject, outputObject);
    }

    @ApiOperation(id = "receivingEquipmentRepairOrderById", value = "维修单接单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/receivingEquipmentRepairOrderById")
    public void receivingEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.receivingEquipmentRepairOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentRepairOrderById", value = "根据ID查询设备维修单详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/queryEquipmentRepairOrderById")
    public void queryEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentRepairOrderById", value = "根据ID删除设备维修单", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/deleteEquipmentRepairOrderById")
    public void deleteEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.deleteById(inputObject, outputObject);
    }

}
