/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.lifecycle.entity.LifecycleState;
import com.skyeye.lifecycle.service.LifecycleStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LifecycleStateController
 * @Description: 生命周期状态管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "生命周期状态管理", tags = "生命周期状态管理", modelName = "生命周期管理")
public class LifecycleStateController {

    @Autowired
    private LifecycleStateService lifecycleStateService;

    @ApiOperation(id = "queryLifecycleStateList", value = "获取状态列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "className", name = "className", value = "service的className", required = "required"),
        @ApiImplicitParam(id = "appId", name = "appId", value = "服务的appId", required = "required"),
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "启用状态", enumClass = EnableEnum.class)})
    @RequestMapping("/post/LifecycleStateController/queryLifecycleStateList")
    public void queryLifecycleStateList(InputObject inputObject, OutputObject outputObject) {
        lifecycleStateService.queryLifecycleStateList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeLifecycleState", value = "新增/编辑状态信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LifecycleState.class)
    @RequestMapping("/post/LifecycleStateController/writeLifecycleState")
    public void writeLifecycleStateMation(InputObject inputObject, OutputObject outputObject) {
        lifecycleStateService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLifecycleStateById", value = "根据id获取状态信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LifecycleStateController/queryLifecycleStateById")
    public void queryLifecycleStateById(InputObject inputObject, OutputObject outputObject) {
        lifecycleStateService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLifecycleStateByIds", value = "根据id批量获取状态信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id，多个id用英文逗号分隔", required = "required")})
    @RequestMapping("/post/LifecycleStateController/queryLifecycleStateByIds")
    public void queryLifecycleStateByIds(InputObject inputObject, OutputObject outputObject) {
        lifecycleStateService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLifecycleStateById", value = "根据id删除状态信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LifecycleStateController/deleteLifecycleStateById")
    public void deleteLifecycleStateById(InputObject inputObject, OutputObject outputObject) {
        lifecycleStateService.deleteById(inputObject, outputObject);
    }

}
