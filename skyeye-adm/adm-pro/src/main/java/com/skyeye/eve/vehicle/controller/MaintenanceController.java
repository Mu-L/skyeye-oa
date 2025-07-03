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
import com.skyeye.eve.vehicle.entity.Maintenance;
import com.skyeye.eve.vehicle.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MaintenanceController
 * @Description: 车辆维修管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 10:15
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车辆维修管理", tags = "车辆维修管理", modelName = "车辆模块")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    /**
     * 遍历车辆维修保养
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "maintenance001", value = "遍历车辆维修保养", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MaintenanceController/queryMaintenanceList")
    public void queryMaintenanceList(InputObject inputObject, OutputObject outputObject) {
        maintenanceService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/修改维修信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeVehicleMaintenance", value = "新增/修改维修信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Maintenance.class)
    @RequestMapping("/post/MaintenanceController/writeVehicleMaintenance")
    public void writeVehicleMaintenance(InputObject inputObject, OutputObject outputObject) {
        maintenanceService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id删除维修信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "maintenance003", value = "根据id删除维修信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaintenanceController/deleteMaintenanceById")
    public void deleteMaintenanceById(InputObject inputObject, OutputObject outputObject) {
        maintenanceService.deleteById(inputObject, outputObject);
    }

}
