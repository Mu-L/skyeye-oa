/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.lifecycle.entity.LifecycleStateChangeHistory;
import com.skyeye.lifecycle.service.LifecycleStateChangeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LifecycleStateChangeHistoryController
 * @Description: 生命周期状态变更历史管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/15 10:23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "生命周期状态变更历史管理", tags = "生命周期状态变更历史管理", modelName = "生命周期管理")
public class LifecycleStateChangeHistoryController {

    @Autowired
    private LifecycleStateChangeHistoryService lifecycleStateChangeHistoryService;

    @ApiOperation(id = "insertLifecycleStateChangeHistory", value = "新增状态变更历史信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LifecycleStateChangeHistory.class)
    @RequestMapping("/post/LifecycleStateChangeHistoryController/insertLifecycleStateChangeHistory")
    public void insertLifecycleStateChangeHistory(InputObject inputObject, OutputObject outputObject) {
        lifecycleStateChangeHistoryService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNewLifecycleStateChangeHistory", value = "获取最新得一条状态变更历史信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id(员工id)", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "所属第三方业务数据的key(员工key)", required = "required"),
        @ApiImplicitParam(id = "objectAppId", name = "objectAppId", value = "所属第三方业务数据的应用的appId", required = "required"),
        @ApiImplicitParam(id = "templateId", name = "templateId", value = "业务对象数据所对应得模板id", required = "required")})
    @RequestMapping("/post/LifecycleStateChangeHistoryController/queryNewLifecycleStateChangeHistory")
    public void queryNewLifecycleStateChangeHistory(InputObject inputObject, OutputObject outputObject) {
        lifecycleStateChangeHistoryService.queryNewLifecycleStateChangeHistory(inputObject, outputObject);
    }

}
