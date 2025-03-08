/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.joincircle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.joincircle.entity.JoinCircle;
import com.skyeye.joincircle.service.JoinCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: JoinCircleController
 * @Description: 加入圈子管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31.
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "加入圈子管理", tags = "加入圈子管理", modelName = "加入圈子管理")
public class JoinCircleController {

    @Autowired
    private JoinCircleService joinCircleService;

    /**
     * 新增加入圈子信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertCircleCollect", value = "新增加入圈子信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = JoinCircle.class)
    @RequestMapping("/post/CircleCollectController/insertCircleCollect")
    public void insertCircleCollect(InputObject inputObject, OutputObject outputObject) {
        joinCircleService.createEntity(inputObject, outputObject);
    }

    /**
     * 根据ID删除加入圈子信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteCircleCollectById", value = "根据ID删除加入圈子信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CircleCollectController/deleteCircleCollectById")
    public void deleteCircleCollectById(InputObject inputObject, OutputObject outputObject) {
        joinCircleService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据圈子id查询加入记录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryJoinUserByCircleId", value = "根据圈子id(objectId)查询加入记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CircleCollectController/queryJoinUserByCircleId")
    public void queryJoinUserByCircleId(InputObject inputObject, OutputObject outputObject) {
        joinCircleService.queryJoinUserByCircleId(inputObject, outputObject);
    }
}
