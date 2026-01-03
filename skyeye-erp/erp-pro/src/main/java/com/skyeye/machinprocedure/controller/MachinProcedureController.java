/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MachinProcedureController
 * @Description: 加工单子单据工序信息控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 15:02
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "加工单子单据工序信息", tags = "加工单子单据工序信息", modelName = "加工单管理")
public class MachinProcedureController {

    @Autowired
    private MachinProcedureService machinProcedureService;

    @ApiOperation(id = "queryMachinProcedureById", value = "查询工序的任务信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinProcedureController/queryMachinProcedureById")
    public void queryMachinProcedureById(InputObject inputObject, OutputObject outputObject) {
        machinProcedureService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "setMachinProcedureById", value = "设置计划时间", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "planStartTime", name = "planStartTime", value = "计划开始时间", required = "required"),
        @ApiImplicitParam(id = "planEndTime", name = "planEndTime", value = "计划结束时间", required = "required"),
        @ApiImplicitParam(id = "actualStartTime", name = "actualStartTime", value = "实际开始时间"),
        @ApiImplicitParam(id = "actualEndTime", name = "actualEndTime", value = "实际结束时间"),
        @ApiImplicitParam(id = "machinProcedureFarmList", name = "machinProcedureFarmList", value = "加工单子单据工序关联车间信息，参考#MachinProcedureFarm实体类", required = "json")})
    @RequestMapping("/post/MachinProcedureController/setMachinProcedureById")
    public void setMachinProcedureById(InputObject inputObject, OutputObject outputObject) {
        machinProcedureService.setMachinProcedureById(inputObject, outputObject);
    }

}
