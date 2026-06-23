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
import com.skyeye.equipment.classenum.EquipmentState;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentRepairOrderController
 * @Description: 设备维修单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
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

    @ApiOperation(id = "queryAllEquipmentRepairOrderList", value = "获取所有设备维修单", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentRepairOrderController/queryAllEquipmentRepairOrderList")
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryAllEquipmentRepairOrderList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentRepairOrder", value = "新增/编辑故障报修", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class)
    @RequestMapping("/post/EquipmentRepairOrderController/writeEquipmentRepairOrder")
    public void writeEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairWaitToWorkMation", value = "派工", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "serviceUserId", name = "serviceUserId", value = "接收人", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairWaitToWorkMation")
    public void editEquipmentRepairWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairWaitToWorkMation(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairResult", value = "编辑维修结果", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class)
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairResult")
    public void editEquipmentRepairResult(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairResult(inputObject, outputObject);
    }

    @ApiOperation(id = "completeEquipmentRepairOrderById", value = "完工操作", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/completeEquipmentRepairOrderById")
    public void completeEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.completeEquipmentRepairOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairEvaluate", value = "编辑评价", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class)
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairEvaluate")
    public void editEquipmentRepairEvaluate(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairEvaluate(inputObject, outputObject);
    }

    @ApiOperation(id = "editEquipmentRepairAcceptance", value = "结果确认", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "isFixed", name = "isFixed", value = "是否修复", required = "required,num"),
        @ApiImplicitParam(id = "equipmentStatus", name = "equipmentStatus", value = "设备状态", enumClass = EquipmentState.class, required = "num")})
    @RequestMapping("/post/EquipmentRepairOrderController/editEquipmentRepairAcceptance")
    public void editEquipmentRepairAcceptance(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.editEquipmentRepairAcceptance(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentRepairSparePartUsage", value = "保存维修单备件使用明细", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class)
    @RequestMapping("/post/EquipmentRepairOrderController/writeEquipmentRepairSparePartUsage")
    public void writeEquipmentRepairSparePartUsage(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.writeEquipmentRepairSparePartUsage(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentRepairMyBeCompleted", value = "查询我负责的待完工状态的维修单", method = "GET", allUse = "2")
    @RequestMapping("/post/EquipmentRepairOrderController/queryEquipmentRepairMyBeCompleted")
    public void queryEquipmentRepairMyBeCompleted(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryEquipmentRepairMyBeCompleted(inputObject, outputObject);
    }

    @ApiOperation(id = "receivingEquipmentRepairOrderById", value = "接单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/receivingEquipmentRepairOrderById")
    public void receivingEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.receivingEquipmentRepairOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentRepairOrderById", value = "根据id查询设备维修单", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/queryEquipmentRepairOrderById")
    public void queryEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentRepairOrderById", value = "删除设备维修单", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/deleteEquipmentRepairOrderById")
    public void deleteEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.deleteById(inputObject, outputObject);
    }

}
