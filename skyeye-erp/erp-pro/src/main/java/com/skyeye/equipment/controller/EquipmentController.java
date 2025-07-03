/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EquipmentController
 * @Description: 设备管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/17 21:16
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "设备管理", tags = "设备管理", modelName = "设备管理")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    /**
     * 获取设备列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryEquipmentList", value = "分页获取设备列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentController/queryEquipmentList")
    public void queryEquipmentList(InputObject inputObject, OutputObject outputObject) {
        equipmentService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑设备信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeEquipment", value = "新增/编辑设备信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Equipment.class)
    @RequestMapping("/post/EquipmentController/writeEquipment")
    public void writeEquipment(InputObject inputObject, OutputObject outputObject) {
        equipmentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据ID删除设备信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteEquipmentById", value = "根据ID删除设备信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EquipmentController/deleteEquipmentById")
    public void deleteEquipmentById(InputObject inputObject, OutputObject outputObject) {
        equipmentService.deleteById(inputObject, outputObject);
    }

    /**
     * 获取所有设备列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllEquipmentList", value = "获取所有设备列表", method = "GET", allUse = "2")
    @RequestMapping("/post/EquipmentController/queryAllEquipmentList")
    public void queryAllEquipmentList(InputObject inputObject, OutputObject outputObject) {
        equipmentService.queryAllEquipmentList(inputObject, outputObject);
    }

    /**
     * 查询上个月采购设备的成本
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryLastMonthEquipmentCost", value = "查询上个月采购设备的成本", method = "POST", allUse = "2")
    @RequestMapping("/post/EquipmentController/queryLastMonthEquipmentCost")
    public void queryLastMonthEquipmentCost(InputObject inputObject, OutputObject outputObject) {
        equipmentService.queryLastMonthEquipmentCost(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLastMonthEquipmentList", value = "根据objectId(项目id)分页获取上个月设备列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EquipmentController/queryLastMonthEquipmentList")
    public void queryLastMonthEquipmentList(InputObject inputObject, OutputObject outputObject) {
        equipmentService.queryLastMonthEquipmentList(inputObject, outputObject);
    }

}
