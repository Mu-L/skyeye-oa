/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.vehicle.entity.Vehicle;
import com.skyeye.eve.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: VehicleController
 * @Description: 车辆管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车辆管理", tags = "车辆管理", modelName = "车辆模块")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    /**
     * 查询所有的车辆
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle001", value = "查询所有的车辆", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VehicleController/queryVehicleList")
    public void queryVehicleList(InputObject inputObject, OutputObject outputObject) {
        vehicleService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/修改车辆信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeVehicle", value = "新增/修改车辆信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Vehicle.class)
    @RequestMapping("/post/VehicleController/writeVehicle")
    public void writeVehicle(InputObject inputObject, OutputObject outputObject) {
        vehicleService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id删除车辆信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle003", value = "根据id删除车辆信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VehicleController/deleteVehicleById")
    public void deleteVehicleById(InputObject inputObject, OutputObject outputObject) {
        vehicleService.deleteById(inputObject, outputObject);
    }

    /**
     * 车辆恢复正常
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle004", value = "车辆恢复正常", method = "POST", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VehicleController/normalVehicleById")
    public void normalVehicleById(InputObject inputObject, OutputObject outputObject) {
        vehicleService.normalVehicleById(inputObject, outputObject);
    }

    /**
     * 车辆维修
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle005", value = "车辆维修", method = "POST", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VehicleController/repairVehicleById")
    public void repairVehicleById(InputObject inputObject, OutputObject outputObject) {
        vehicleService.repairVehicleById(inputObject, outputObject);
    }

    /**
     * 车辆报废
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle006", value = "车辆报废", method = "POST", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VehicleController/scrapVehicleById")
    public void scrapVehicleById(InputObject inputObject, OutputObject outputObject) {
        vehicleService.scrapVehicleById(inputObject, outputObject);
    }

    /**
     * 查询所有正常的车辆信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle010", value = "查询所有正常的车辆信息", method = "GET", allUse = "2")
    @RequestMapping("/post/VehicleController/queryAllNormalVehicleList")
    public void queryAllNormalVehicleList(InputObject inputObject, OutputObject outputObject) {
        vehicleService.queryAllNormalVehicleList(inputObject, outputObject);
    }

}
