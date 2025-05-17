/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activity.controller;

import com.skyeye.activity.entity.BatchChooseActivityUserBox;
import com.skyeye.activity.service.ChooseActivityUserService;
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
 * @ClassName: ActivityUserController
 * @Description: 活动可参与的用户信息管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "活动可参与的用户信息管理", tags = "活动可参与的用户信息管理", modelName = "活动可参与的用户信息管理")
public class ChooseActivityUserController {

    @Autowired
    private ChooseActivityUserService chooseActivityUserService;

    @ApiOperation(id = "insertActivityUserList", value = "新增活动可参与的用户信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = BatchChooseActivityUserBox.class)
    @RequestMapping("/post/ActivityUserController/insertActivityUserList")
    public void insertActivityUserList(InputObject inputObject, OutputObject outputObject) {
        chooseActivityUserService.insertActivityUser(inputObject, outputObject);
    }

    @ApiOperation(id = "queryActivityUserList", value = "分页获取活动可参与的用户信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class,
        value = {@ApiImplicitParam(id = "objectId", name = "objectId", value = "活动id", required = "required"),
            @ApiImplicitParam(id = "type", name = "type", value = "用户类型", required = "required")})
    @RequestMapping("/post/ActivityUserController/queryActivityUserList")
    public void queryActivityUserList(InputObject inputObject, OutputObject outputObject) {
        chooseActivityUserService.queryActivityUserList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteActivityUserById", value = "删除活动可参与的用户信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ActivityUserController/deleteActivityUserById")
    public void deleteActivityUserById(InputObject inputObject, OutputObject outputObject) {
        chooseActivityUserService.deleteById(inputObject, outputObject);
    }
}
