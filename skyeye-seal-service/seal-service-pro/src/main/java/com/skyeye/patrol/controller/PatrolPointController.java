/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolPoint;
import com.skyeye.patrol.service.PatrolPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolPointController
 * @Description: 巡检点位控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检点位", tags = "巡检点位", modelName = "巡检点位")
public class PatrolPointController {

    @Autowired
    private PatrolPointService patrolPointService;

    @ApiOperation(id = "queryPatrolPointList", value = "获取巡检点位列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolPointController/queryPatrolPointList")
    public void queryPatrolPointList(InputObject inputObject, OutputObject outputObject) {
        patrolPointService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePatrolPoint", value = "新增/编辑巡检点位", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatrolPoint.class)
    @RequestMapping("/post/PatrolPointController/writePatrolPoint")
    public void writePatrolPoint(InputObject inputObject, OutputObject outputObject) {
        patrolPointService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPatrolPointById", value = "根据ID获取巡检点位信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolPointController/queryPatrolPointById")
    public void queryPatrolPointById(InputObject inputObject, OutputObject outputObject) {
        patrolPointService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePatrolPointById", value = "根据ID删除巡检点位", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolPointController/deletePatrolPointById")
    public void deletePatrolPointById(InputObject inputObject, OutputObject outputObject) {
        patrolPointService.deleteById(inputObject, outputObject);
    }

}

