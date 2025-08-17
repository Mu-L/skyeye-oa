/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.language.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.language.entity.Language;
import com.skyeye.language.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LanguageController
 * @Description: 员工语言能力信息管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/17 7:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工语言能力", tags = "员工语言能力", modelName = "员工语言能力")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    @ApiOperation(id = "queryLanguageList", value = "查询语言能力列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LanguageController/queryLanguageList")
    public void queryLanguageList(InputObject inputObject, OutputObject outputObject) {
        languageService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeLanguage", value = "新增/编辑员工语言能力", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Language.class)
    @RequestMapping("/post/LanguageController/writeLanguage")
    public void writeLanguage(InputObject inputObject, OutputObject outputObject) {
        languageService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLanguageById", value = "根据id删除员工语言能力", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LanguageController/deleteLanguageById")
    public void deleteLanguageById(InputObject inputObject, OutputObject outputObject) {
        languageService.deleteById(inputObject, outputObject);
    }

}
