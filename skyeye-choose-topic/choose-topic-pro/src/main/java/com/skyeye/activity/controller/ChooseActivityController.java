/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.controller;

import com.skyeye.activity.entity.ChooseActivity;
import com.skyeye.activity.service.ChooseActivityService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ActivityController
 * @Description: 活动管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "活动管理", tags = "活动管理", modelName = "活动管理")
public class ChooseActivityController {

    @Autowired
    private ChooseActivityService chooseActivityService;

    /**
     * 分页获取课题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryActivityList", value = "分页获取活动信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ActivityController/queryActivityList")
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.queryPageList(inputObject, outputObject);
    }


    /**
     * 新增/编辑活动信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "saveOrUpdateActivity", value = "新增/编辑活动信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ChooseActivity.class)
    @RequestMapping("/post/ActivityController/saveOrUpdateActivity")
    public void saveOrUpdateActivity(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id删除活动信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteActivityById", value = "根据id删除活动信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required")})
    @RequestMapping("/post/ActivityController/deleteActivityById")
    public void deleteActivityById(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.deleteById(inputObject, outputObject);
    }


    /**
     * 根据id查询活动信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectActivityById", value = "根据id查询活动信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required")})
    @RequestMapping("/post/ActivityController/selectActivityById")
    public void selectActivityById(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.selectById(inputObject, outputObject);
    }

    /**
     * 分页获取我参与的活动信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyJoinActivityList", value = "获取我参与的活动信息列表", method = "POST", allUse = "2")
    @RequestMapping("/post/ActivityController/queryMyJoinActivityList")
    public void queryMyJoinActivityList(InputObject inputObject, OutputObject outputObject) {
        chooseActivityService.queryMyJoinActivityList(inputObject, outputObject);
    }
}
