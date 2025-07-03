package com.skyeye.scheduling.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.service.SchedulingShiftsTimeWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "排班班次时间下工位管理", tags = "排班班次时间下工位管理", modelName = "排班班次时间下工位管理")
public class SchedulingShiftsTimeWorkController {

    @Autowired
    private SchedulingShiftsTimeWorkService schedulingShiftsTimeWorkService;

    @ApiOperation(id = "deleteSchedulingByWorkId", value = "根据工位Id删除班次时间下的工位", method = "DELETE", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "workId", name = "workId", value = "工位Id", required = "required"))
    @RequestMapping("/post/SchedulingShiftsTimeWorkController/deleteSchedulingByWorkId")
    public void deleteSchedulingByWorkId(InputObject inputObject, OutputObject outputObject) {
        schedulingShiftsTimeWorkService.deleteSchedulingByWorkId(inputObject, outputObject);
    }
}
