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
import com.skyeye.eve.vehicle.entity.VehicleDrivers;
import com.skyeye.eve.vehicle.service.VehicleDriversService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: VehicleDriversController
 * @Description: 车辆驾驶员信息控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/22 10:43
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车辆驾驶员信息", tags = "车辆驾驶员信息", modelName = "车辆模块")
public class VehicleDriversController {

    @Autowired
    private VehicleDriversService vehicleDriversService;

    @ApiOperation(id = "queryVehicleDriversList", value = "获取驾驶员信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VehicleDriversController/queryVehicleDriversList")
    public void queryVehicleDriversList(InputObject inputObject, OutputObject outputObject) {
        vehicleDriversService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeVehicleDrivers", value = "新增/编辑驾驶员资料信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = VehicleDrivers.class)
    @RequestMapping("/post/VehicleDriversController/writeVehicleDrivers")
    public void writeVehicleDrivers(InputObject inputObject, OutputObject outputObject) {
        vehicleDriversService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteVehicleDriversById", value = "删除驾驶员资料信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VehicleDriversController/deleteVehicleDriversById")
    public void deleteVehicleDriversById(InputObject inputObject, OutputObject outputObject) {
        vehicleDriversService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllVehicleDriversList", value = "获取所有驾驶员信息", method = "GET", allUse = "2")
    @RequestMapping("/post/VehicleDriversController/queryAllVehicleDriversList")
    public void queryAllVehicleDriversList(InputObject inputObject, OutputObject outputObject) {
        vehicleDriversService.queryAllVehicleDriversList(inputObject, outputObject);
    }

}
