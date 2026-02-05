/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dispatch.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dispatch.entity.SealDispatchConfig;
import com.skyeye.dispatch.service.SealDispatchConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealDispatchRuleController
 * @Description: 工单派单规则配置控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "派单规则配置", tags = "工单管理", modelName = "工单管理")
public class SealDispatchRuleController {

    @Autowired
    private SealDispatchConfigService sealDispatchConfigService;

    @ApiOperation(id = "getDispatchRules", value = "获取派单规则配置", method = "GET", allUse = "2")
    @RequestMapping("/post/SealDispatchRuleController/getDispatchRules")
    public void getDispatchRules(InputObject inputObject, OutputObject outputObject) {
        sealDispatchConfigService.getDispatchRules(inputObject, outputObject);
    }

    @ApiOperation(id = "saveDispatchRules", value = "保存派单规则配置", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SealDispatchConfig.class)
    @RequestMapping("/post/SealDispatchRuleController/saveDispatchRules")
    public void saveDispatchRules(InputObject inputObject, OutputObject outputObject) {
        sealDispatchConfigService.saveOrUpdateEntity(inputObject, outputObject);
    }

}
