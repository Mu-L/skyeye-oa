/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.doc.code.service.CodePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CodePackageController
 * @Description: 源代码包管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 17:34
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "源代码包管理", tags = "源代码包管理", modelName = "源代码包管理")
public class CodePackageController {

    @Autowired
    private CodePackageService codePackageService;

}
