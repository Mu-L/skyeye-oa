/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
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

    @ApiOperation(id = "queryAllEquipmentRepairOrderList", value = "获取所有设备维修单（无分页无筛选）", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentRepairOrderController/queryAllEquipmentRepairOrderList")
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryAllEquipmentRepairOrderList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentRepairOrder", value = "新增/编辑设备维修单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentRepairOrder.class)
    @RequestMapping("/post/EquipmentRepairOrderController/writeEquipmentRepairOrder")
    public void writeEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.saveOrUpdateEntity(inputObject, outputObject);
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

    @ApiOperation(id = "deleteEquipmentRepairOrderByIds", value = "批量删除设备维修单（传 ids）", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个用逗号分隔", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/deleteEquipmentRepairOrderByIds")
    public void deleteEquipmentRepairOrderByIds(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalEquipmentRepairOrder", value = "设备维修单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/EquipmentRepairOrderController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeEquipmentRepairOrder", value = "撤销设备维修单审批申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/EquipmentRepairOrderController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.revoke(inputObject, outputObject);
    }

}

