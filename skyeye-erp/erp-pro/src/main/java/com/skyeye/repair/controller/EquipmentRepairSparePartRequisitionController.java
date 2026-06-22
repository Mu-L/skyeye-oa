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
import com.skyeye.repair.entity.EquipmentSparePartRequisition;
import com.skyeye.repair.service.EquipmentSparePartRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 维修工单备件领用单（关联设备维修单）
 */
@RestController
@Api(value = "维修工单备件领用", tags = "维修工单备件领用", modelName = "设备维修单")
public class EquipmentRepairSparePartRequisitionController {

    @Autowired
    private EquipmentSparePartRequisitionService equipmentSparePartRequisitionService;

    @ApiOperation(id = "queryEquipmentSparePartRequisitionList", value = "获取维修工单备件领用单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentRepairSparePartRequisitionController/queryEquipmentSparePartRequisitionList")
    public void queryEquipmentSparePartRequisitionList(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEquipmentSparePartRequisition", value = "新增/编辑维修工单备件领用单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EquipmentSparePartRequisition.class)
    @RequestMapping("/post/EquipmentRepairSparePartRequisitionController/writeEquipmentSparePartRequisition")
    public void writeEquipmentSparePartRequisition(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEquipmentSparePartRequisitionById", value = "根据ID查询维修工单备件领用单详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairSparePartRequisitionController/queryEquipmentSparePartRequisitionById")
    public void queryEquipmentSparePartRequisitionById(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentSparePartRequisitionById", value = "根据ID删除维修工单备件领用单", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentRepairSparePartRequisitionController/deleteEquipmentSparePartRequisitionById")
    public void deleteEquipmentSparePartRequisitionById(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEquipmentSparePartRequisitionByIds", value = "批量删除维修工单备件领用单", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个用逗号分隔", required = "required")})
    @RequestMapping("/post/EquipmentRepairSparePartRequisitionController/deleteEquipmentSparePartRequisitionByIds")
    public void deleteEquipmentSparePartRequisitionByIds(InputObject inputObject, OutputObject outputObject) {
        equipmentSparePartRequisitionService.deleteByIds(inputObject, outputObject);
    }

}
