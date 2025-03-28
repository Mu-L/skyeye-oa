/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.circleview.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.circleview.entity.CircleView;
import com.skyeye.circleview.service.CircleViewService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CircleController
 * @Description: 圈子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31.
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "圈子浏览记录管理", tags = "圈子浏览记录管理", modelName = "圈子浏览记录管理")
public class CircleViewController {

    @Autowired
    private CircleViewService circleViewService;

    /**
     * 获取圈子浏览信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyCircleList", value = "获取圈子浏览信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CircleViewController/queryMyCircleList")
    public void queryMyCircleList(InputObject inputObject, OutputObject outputObject) {
        circleViewService.queryMyCircleList(inputObject, outputObject);
    }

    /**
     * 新增圈子浏览信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertCircleView", value = "新增圈子浏览信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CircleView.class)
    @RequestMapping("/post/CircleViewController/insertCircleView")
    public void insertCircleView(InputObject inputObject, OutputObject outputObject) {
        circleViewService.createEntity(inputObject, outputObject);
    }

    /**
     * 根据ID删除圈子信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteCircleViewById", value = "根据ID删除圈子浏览信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CircleViewController/deleteCircleViewById")
    public void deleteCircleViewById(InputObject inputObject, OutputObject outputObject) {
        circleViewService.deleteById(inputObject, outputObject);
    }
}
