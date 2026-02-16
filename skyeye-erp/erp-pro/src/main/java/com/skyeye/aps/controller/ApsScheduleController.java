/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.aps.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.aps.entity.ApsScheduleParam;
import com.skyeye.aps.entity.ApsScheduleSaveParam;
import com.skyeye.aps.service.ApsScheduleService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APS排程控制器
 * 支持：多加工单、多工序、工序依赖、车间产能日历、标准工时
 *
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 */
@RestController
@Api(value = "APS排程", tags = "APS排程", modelName = "APS排程")
public class ApsScheduleController {

    @Autowired
    private ApsScheduleService apsScheduleService;

    @ApiOperation(id = "executeSchedule", value = "执行APS排程计算", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ApsScheduleParam.class)
    @RequestMapping("/post/ApsScheduleController/executeSchedule")
    public void executeSchedule(InputObject inputObject, OutputObject outputObject) {
        apsScheduleService.schedule(inputObject, outputObject);
    }

    @ApiOperation(id = "saveSchedule", value = "保存排产信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ApsScheduleSaveParam.class)
    @RequestMapping("/post/ApsScheduleController/saveSchedule")
    public void saveSchedule(InputObject inputObject, OutputObject outputObject) {
        apsScheduleService.saveSchedule(inputObject, outputObject);
    }
}
