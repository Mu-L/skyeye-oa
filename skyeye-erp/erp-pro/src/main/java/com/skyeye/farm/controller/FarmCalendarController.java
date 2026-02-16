/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.farm.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.FarmCalendar;
import com.skyeye.farm.service.FarmCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FarmCalendarController
 * @Description: 车间产能日历控制类
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车间产能日历", tags = "车间管理", modelName = "车间管理")
public class FarmCalendarController {

    @Autowired
    private FarmCalendarService farmCalendarService;

    @ApiOperation(id = "queryFarmCalendarList", value = "获取车间产能日历列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FarmCalendarController/queryFarmCalendarList")
    public void queryFarmCalendarList(InputObject inputObject, OutputObject outputObject) {
        farmCalendarService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeFarmCalendar", value = "新增/编辑车间产能日历", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = FarmCalendar.class)
    @RequestMapping("/post/FarmCalendarController/writeFarmCalendar")
    public void writeFarmCalendar(InputObject inputObject, OutputObject outputObject) {
        farmCalendarService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFarmCalendarById", value = "根据id获取车间产能日历", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FarmCalendarController/queryFarmCalendarById")
    public void queryFarmCalendarById(InputObject inputObject, OutputObject outputObject) {
        farmCalendarService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteFarmCalendarById", value = "根据ID删除车间产能日历", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FarmCalendarController/deleteFarmCalendarById")
    public void deleteFarmCalendarById(InputObject inputObject, OutputObject outputObject) {
        farmCalendarService.deleteById(inputObject, outputObject);
    }
}
