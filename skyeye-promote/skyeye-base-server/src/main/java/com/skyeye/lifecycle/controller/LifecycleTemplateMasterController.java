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
import com.skyeye.lifecycle.entity.LifecycleTemplateMaster;
import com.skyeye.lifecycle.service.LifecycleTemplateMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LifecycleTemplateMasterController
 * @Description: 生命周期模板主表控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 11:10
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "生命周期状态管理", tags = "生命周期状态管理", modelName = "生命周期管理")
public class LifecycleTemplateMasterController {

    @Autowired
    private LifecycleTemplateMasterService lifecycleTemplateMasterService;

    @ApiOperation(id = "writeLifecycleTemplateMaster", value = "新增/编辑生命周期模板主表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LifecycleTemplateMaster.class)
    @RequestMapping("/post/LifecycleStateController/writeLifecycleTemplateMaster")
    public void writeLifecycleTemplateMaster(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateMasterService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLifecycleTemplateMaster", value = "查询生命周期模板主表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用得appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "服务类的className", required = "required")})
    @RequestMapping("/post/LifecycleStateController/queryLifecycleTemplateMaster")
    public void queryLifecycleTemplateMaster(InputObject inputObject, OutputObject outputObject) {
        lifecycleTemplateMasterService.queryLifecycleTemplateMaster(inputObject, outputObject);
    }

}
