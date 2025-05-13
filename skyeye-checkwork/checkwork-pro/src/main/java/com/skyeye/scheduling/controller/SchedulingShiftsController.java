package com.skyeye.scheduling.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.SchedulingShifts;
import com.skyeye.scheduling.service.SchedulingShiftsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "排班班次管理", tags = "排班班次管理", modelName = "排班班次管理")
public class SchedulingShiftsController {

    @Autowired
    private SchedulingShiftsService schedulingShiftsService;

    @ApiOperation(id = "writeSchedulingShifts", value = "新增班次", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SchedulingShifts.class)
    @RequestMapping("/post/SchedulingShiftsController/writeSchedulingShifts")
    public void writeSchedulingShifts(InputObject inputObject, OutputObject outputObject) {
        schedulingShiftsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSchedulingShiftsByIds", value = "删除班次", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "班次ids", required = "required")})
    @RequestMapping("/post/SchedulingShiftsController/deleteSchedulingShifts")
    public void deleteSchedulingShifts(InputObject inputObject, OutputObject outputObject) {
        schedulingShiftsService.deleteSchedulingShifts(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingShiftsList", value = "批量查询排班班次", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SchedulingShiftsController/querySchedulingShiftsList")
    public void querySchedulingShiftsList(InputObject inputObject, OutputObject outputObject) {
        schedulingShiftsService.querySchedulingShiftsList(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingShiftsById", value = "根据Id查询排班班次", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SchedulingShiftsController/querySchedulingShiftsById")
    public void querySchedulingShiftsById(InputObject inputObject, OutputObject outputObject) {
        schedulingShiftsService.querySchedulingShiftsById(inputObject, outputObject);
    }

}
