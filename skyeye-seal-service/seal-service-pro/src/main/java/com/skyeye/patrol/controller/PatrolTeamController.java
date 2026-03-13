/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolTeam;
import com.skyeye.patrol.service.PatrolTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolTeamController
 * @Description: 巡检班组控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检班组", tags = "巡检班组", modelName = "巡检班组")
public class PatrolTeamController {

    @Autowired
    private PatrolTeamService patrolTeamService;

    @ApiOperation(id = "queryPatrolTeamList", value = "获取巡检班组列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolTeamController/queryPatrolTeamList")
    public void queryPatrolTeamList(InputObject inputObject, OutputObject outputObject) {
        patrolTeamService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePatrolTeam", value = "新增/编辑巡检班组", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatrolTeam.class)
    @RequestMapping("/post/PatrolTeamController/writePatrolTeam")
    public void writePatrolTeam(InputObject inputObject, OutputObject outputObject) {
        patrolTeamService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPatrolTeamById", value = "根据ID获取巡检班组信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolTeamController/queryPatrolTeamById")
    public void queryPatrolTeamById(InputObject inputObject, OutputObject outputObject) {
        patrolTeamService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePatrolTeamById", value = "根据ID删除巡检班组", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolTeamController/deletePatrolTeamById")
    public void deletePatrolTeamById(InputObject inputObject, OutputObject outputObject) {
        patrolTeamService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllPatrolTeamList", value = "获取所有巡检班组列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "启用状态", enumClass = EnableEnum.class)})
    @RequestMapping("/post/PatrolTeamController/queryAllPatrolTeamList")
    public void queryAllPatrolTeamList(InputObject inputObject, OutputObject outputObject) {
        patrolTeamService.queryAllPatrolTeamList(inputObject, outputObject);
    }

}

