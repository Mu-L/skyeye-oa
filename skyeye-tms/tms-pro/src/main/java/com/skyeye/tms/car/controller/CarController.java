/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tms.car.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tms.car.entity.Car;
import com.skyeye.tms.car.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CarController
 * @Description: 车辆管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/9 12:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车辆管理", tags = "车辆管理", modelName = "车辆管理")
public class CarController {

    @Autowired
    private CarService carService;

    @ApiOperation(id = "queryCarList", value = "获取车辆列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CarController/queryCarList")
    public void queryCarList(InputObject inputObject, OutputObject outputObject) {
        carService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCar", value = "新增/编辑车辆", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Car.class)
    @RequestMapping("/post/CarController/writeCar")
    public void writeCar(InputObject inputObject, OutputObject outputObject) {
        carService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCarById", value = "根据ID删除车辆", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CarController/deleteCarById")
    public void deleteCarById(InputObject inputObject, OutputObject outputObject) {
        carService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledCarList", value = "获取已启用的车辆", method = "GET", allUse = "2")
    @RequestMapping("/post/TmsCarTypeController/queryEnabledCarList")
    public void queryEnabledCarList(InputObject inputObject, OutputObject outputObject) {
        carService.queryEnabledCarList(inputObject, outputObject);
    }

}
