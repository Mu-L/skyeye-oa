package com.skyeye.scheduling.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.SchedulingLeave;
import com.skyeye.scheduling.service.SchedulingLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "排班请假管理", tags = "排班请假管理", modelName = "排班请假管理")
public class SchedulingLeaveController {

    @Autowired
    private SchedulingLeaveService schedulingLeaveService;

    @ApiOperation(id = "writeSchedulingLeave", value = "新增临时工请假", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SchedulingLeave.class)
    @RequestMapping("/post/SchedulingLeaveController/writeSchedulingLeave")
    public void writeSchedulingLeave(InputObject inputObject, OutputObject outputObject) {
        schedulingLeaveService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSchedulingLeave", value = "删除临时工请假信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "ids", name = "ids", value = "排班人员ids", required = "required"))
    @RequestMapping("/post/SchedulingLeaveController/deleteSchedulingLeave")
    public void deleteSchedulingLeave(InputObject inputObject, OutputObject outputObject) {
        schedulingLeaveService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingLeaveList", value = "批量查询临时工请假", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SchedulingLeaveController/querySchedulingLeaveList")
    public void querySchedulingLeaveList(InputObject inputObject, OutputObject outputObject) {
        schedulingLeaveService.querySchedulingLeaveList(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchedulingLeaveById", value = "根据Id查询临时工请假", method = "POST", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "id", name = "id", value = "主键Id", required = "required"))
    @RequestMapping("/post/SchedulingLeaveController/querySchedulingLeaveById")
    public void querySchedulingLeaveById(InputObject inputObject, OutputObject outputObject) {
        schedulingLeaveService.selectById(inputObject, outputObject);
    }

//    @ApiOperation(id = "updateSchedulingLeave", value = "根据Id修改临时工请假状态", method = "POST", allUse = "2")
//    @ApiImplicitParams({
//        @ApiImplicitParam(id = "id", name = "id", value = "主键Id", required = "required"),
//        @ApiImplicitParam(id = "status", name = "status", value = "请假状态", required = "required")})
//    @RequestMapping("/post/SchedulingLeaveController/updateSchedulingLeave")
//    public void updateSchedulingLeave(InputObject inputObject, OutputObject outputObject) {
//        schedulingLeaveService.updateSchedulingLeave(inputObject, outputObject);
//    }
}
