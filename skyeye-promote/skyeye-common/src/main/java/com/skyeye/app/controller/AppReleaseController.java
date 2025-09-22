/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.app.service.AppReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppReleaseController
 * @Description: APP发布信息
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 14:56
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "APP发布信息", tags = "APP发布信息", modelName = "APP版本发布模块")
public class AppReleaseController {

    @Autowired
    private AppReleaseService appReleaseService;

}
