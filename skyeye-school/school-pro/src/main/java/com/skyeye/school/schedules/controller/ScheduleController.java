package com.skyeye.school.schedules.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.schedules.entity.Schedule;
import com.skyeye.school.schedules.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ScheduleController
 * @Description: 排课表控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@RestController
@Api(value = "排课表管理", tags = "排课表管理", modelName = "排课表管理")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(id = "writeSchedules", value = "新增/编辑排课表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Schedule.class)
    @RequestMapping("/post/ScheduleController/writeSchedules")
    public void writeSchedules(InputObject inputObject, OutputObject outputObject) {
        scheduleService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulesById", value = "根据id获课表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id",name = "id", value = "排课表id", required = "required")
    })
    @RequestMapping("/post/ScheduleController/writeSchedules")
    public void querySchedulesById(InputObject inputObject, OutputObject outputObject) {
        scheduleService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSchedulesById", value = "根据id删除课表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id",name = "id", value = "排课表id", required = "required")
    })
    @RequestMapping("/post/ScheduleController/deleteSchedulesById")
    public void deleteSchedulesById(InputObject inputObject, OutputObject outputObject) {
        scheduleService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulesList", value = "获取课表列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "teacherId",name = "teacherId", value = "老师id"),
        @ApiImplicitParam(id = "classroomId",name = "classroomId", value = "教室id"),
    })
    @RequestMapping("/post/ScheduleController/querySchedulesList")
    public void querySchedulesList(InputObject inputObject, OutputObject outputObject) {
        scheduleService.querySchedulesList(inputObject, outputObject);
    }
}
