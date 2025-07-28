package com.skyeye.scheduling.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.service.SchedulingService;
import com.skyeye.scheduling.service.SchedulingTimeWorkPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "排班工位下员工管理", tags = "排班工位下员工管理", modelName = "排班工位下员工管理")
public class SchedulingTimeWorkPeopleController {

    @Autowired
    private SchedulingTimeWorkPeopleService schedulingTimeWorkPeopleService;

    @ApiOperation(id = "trackEmployeeAttendanceLeaveTime", value = "统计员工上班和请假时间", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "开始时间（年月日）", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "结束时间（年月日）", required = "required"),
        @ApiImplicitParam(id = "employeeId", name = "employeeId", value = "员工Id", required = "required")})
    @RequestMapping("/post/SchedulingTimeWorkPeopleController/trackEmployeeAttendanceLeaveTime")
    public void trackEmployeeAttendanceLeaveTime(InputObject inputObject, OutputObject outputObject) {
        schedulingTimeWorkPeopleService.trackEmployeeAttendanceLeaveTime(inputObject, outputObject);
    }
}
