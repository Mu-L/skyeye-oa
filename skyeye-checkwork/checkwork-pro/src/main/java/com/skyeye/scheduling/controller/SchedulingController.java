package com.skyeye.scheduling.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.Scheduling;
import com.skyeye.scheduling.entity.SchedulingAuto;
import com.skyeye.scheduling.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "排班管理", tags = "排班管理", modelName = "排班管理")
public class SchedulingController {

    @Autowired
    private SchedulingService schedulingService;

    @ApiOperation(id = "writeManualScheduling", value = "新增/编辑排班", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Scheduling.class)
    @RequestMapping("/post/SchedulingController/writeManualScheduling")
    public void writeManualScheduling(InputObject inputObject, OutputObject outputObject) {
        schedulingService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingById", value = "根据Id查询排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "id", name = "id", value = "排班id", required = "required"))
    @RequestMapping("/post/SchedulingController/querySchedulingById")
    public void querySchedulingById(InputObject inputObject, OutputObject outputObject) {
        schedulingService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "autoComputeScheduling", value = "智能计算排班", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SchedulingAuto.class)
    @RequestMapping("/post/SchedulingController/autoComputeScheduling")
    public void autoComputeScheduling(InputObject inputObject, OutputObject outputObject) {
        schedulingService.autoComputeScheduling(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingByStaffId", value = "查询当前账户的排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "开始时间(格式 yyyy-MM-dd)", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "结束时间(格式 yyyy-MM-dd)", required = "required")})
    @RequestMapping("/post/SchedulingController/querySchedulingByStaffId")
    public void querySchedulingByStaffId(InputObject inputObject, OutputObject outputObject) {
        schedulingService.querySchedulingByStaffId(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSchedulingByIds", value = "删除排班人员", method = "DELETE", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "ids", name = "ids", value = "排班人员ids", required = "required"))
    @RequestMapping("/post/SchedulingController/deleteSchedulingByIds")
    public void deleteSchedulingByIds(InputObject inputObject, OutputObject outputObject) {
        schedulingService.deleteSchedulingByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingList", value = "查询所有排班记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SchedulingController/querySchedulingList")
    public void querySchedulingList(InputObject inputObject, OutputObject outputObject) {
        schedulingService.querySchedulingList(inputObject, outputObject);
    }

}
