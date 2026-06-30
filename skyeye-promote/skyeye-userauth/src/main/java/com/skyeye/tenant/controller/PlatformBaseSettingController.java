/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.PlatformBaseSetting;
import com.skyeye.tenant.service.PlatformBaseSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PlatformBaseSettingController
 * @Description: 平台基础信息设置控制层（管理接口需平台租户权限）
 */
@RestController
@Api(value = "平台基础信息设置", tags = "平台基础信息设置", modelName = "租户管理")
public class PlatformBaseSettingController {

    @Autowired
    private PlatformBaseSettingService platformBaseSettingService;

    @ApiOperation(id = "queryPlatformBaseSetting", value = "获取平台基础信息设置", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformBaseSetting")
    public void queryPlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformBaseSetting(inputObject, outputObject);
    }

    @ApiOperation(id = "updatePlatformBaseSetting", value = "编辑平台基础信息设置", method = "PUT", allUse = "1")
    @ApiImplicitParams(classBean = PlatformBaseSetting.class)
    @RequestMapping("/post/PlatformBaseSettingController/updatePlatformBaseSetting")
    public void updatePlatformBaseSetting(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.updatePlatformBaseSetting(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPlatformAccountUnitPrice", value = "获取平台成员席位单价", method = "GET", allUse = "2")
    @RequestMapping("/post/PlatformBaseSettingController/queryPlatformAccountUnitPrice")
    public void queryPlatformAccountUnitPrice(InputObject inputObject, OutputObject outputObject) {
        platformBaseSettingService.queryPlatformAccountUnitPrice(inputObject, outputObject);
    }

}
