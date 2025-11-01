package com.skyeye.school.building.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.LocationRange;
import com.skyeye.school.building.service.LocationRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LocationRangeController
 * @Description: 地点范围控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/11/10 15:17
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "地点范围管理", tags = "地点范围管理", modelName = "地点范围管理")
public class LocationRangeController {

    @Autowired
    private LocationRangeService locationRangeService;

    @ApiOperation(id = "writeLocationRange", value = "添加地点范围", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LocationRange.class)
    @RequestMapping("/post/LocationRangeController/writeLocationRange")
    public void writeLocationRange(InputObject inputObject, OutputObject outputObject) {
        locationRangeService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLocationRangeById", value = "根据ID删除地点范围信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LocationRangeController/deleteLocationRangeById")
    public void deleteLocationRangeById(InputObject inputObject, OutputObject outputObject) {
        locationRangeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLocationRangeById", value = "根据id获取获取地点范围信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "地点id", required = "required")
    })
    @RequestMapping("/post/LocationRangeController/queryLocationRangeById")
    public void queryLocationRangeById(InputObject inputObject, OutputObject outputObject) {
        locationRangeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLocationRangeList", value = "获取地点范围信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LocationRangeController/queryLocationRangeList")
    public void queryLocationRangeList(InputObject inputObject, OutputObject outputObject) {
        locationRangeService.queryLocationRangeList(inputObject, outputObject);
    }

}
