/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolItem;
import com.skyeye.patrol.service.PatrolItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolItemController
 * @Description: 巡检项目控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检项目", tags = "巡检项目", modelName = "巡检项目")
public class PatrolItemController {

    @Autowired
    private PatrolItemService patrolItemService;

    @ApiOperation(id = "queryPatrolItemList", value = "获取巡检项目列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolItemController/queryPatrolItemList")
    public void queryPatrolItemList(InputObject inputObject, OutputObject outputObject) {
        patrolItemService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePatrolItem", value = "新增/编辑巡检项目", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatrolItem.class)
    @RequestMapping("/post/PatrolItemController/writePatrolItem")
    public void writePatrolItem(InputObject inputObject, OutputObject outputObject) {
        patrolItemService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPatrolItemById", value = "根据ID获取巡检项目信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolItemController/queryPatrolItemById")
    public void queryPatrolItemById(InputObject inputObject, OutputObject outputObject) {
        patrolItemService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePatrolItemById", value = "根据ID删除巡检项目", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolItemController/deletePatrolItemById")
    public void deletePatrolItemById(InputObject inputObject, OutputObject outputObject) {
        patrolItemService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllPatrolItemList", value = "获取所有巡检项目列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "启用状态", enumClass = EnableEnum.class)})
    @RequestMapping("/post/PatrolItemController/queryAllPatrolItemList")
    public void queryAllPatrolItemList(InputObject inputObject, OutputObject outputObject) {
        patrolItemService.queryAllPatrolItemList(inputObject, outputObject);
    }

}

