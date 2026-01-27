/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolPlan;
import com.skyeye.patrol.service.PatrolPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolPlanController
 * @Description: 巡检计划控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检计划", tags = "巡检计划", modelName = "巡检计划")
public class PatrolPlanController {

    @Autowired
    private PatrolPlanService patrolPlanService;

    @ApiOperation(id = "queryPatrolPlanList", value = "获取巡检计划列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolPlanController/queryPatrolPlanList")
    public void queryPatrolPlanList(InputObject inputObject, OutputObject outputObject) {
        patrolPlanService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePatrolPlan", value = "新增/编辑巡检计划", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatrolPlan.class)
    @RequestMapping("/post/PatrolPlanController/writePatrolPlan")
    public void writePatrolPlan(InputObject inputObject, OutputObject outputObject) {
        patrolPlanService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPatrolPlanById", value = "根据ID查询巡检计划详情", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolPlanController/queryPatrolPlanById")
    public void queryPatrolPlanById(InputObject inputObject, OutputObject outputObject) {
        patrolPlanService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePatrolPlanById", value = "根据ID删除巡检计划", method = "DELETE", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolPlanController/deletePatrolPlanById")
    public void deletePatrolPlanById(InputObject inputObject, OutputObject outputObject) {
        patrolPlanService.deleteById(inputObject, outputObject);
    }
}

