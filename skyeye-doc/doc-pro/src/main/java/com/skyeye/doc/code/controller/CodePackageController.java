/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.code.entity.CodePackage;
import com.skyeye.doc.code.service.CodePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @ApiOperation(id = "queryCodePackageList", value = "查询源代码包列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CodePackageController/queryCodePackageList")
    public void queryCodePackageList(InputObject inputObject, OutputObject outputObject) {
        codePackageService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCodePackage", value = "新增/编辑源代码包", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CodePackage.class)
    @RequestMapping("/post/CodePackageController/writeCodePackage")
    public void writeCodePackage(InputObject inputObject, OutputObject outputObject) {
        codePackageService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCodePackageById", value = "根据id删除源代码包", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CodePackageController/deleteCodePackageById")
    public void deleteCodePackageById(InputObject inputObject, OutputObject outputObject) {
        codePackageService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllCodePackageList", value = "根据id删除源代码包", method = "GET", allUse = "2")
    @RequestMapping("/post/CodePackageController/queryAllCodePackageList")
    public void queryAllCodePackageList(InputObject inputObject, OutputObject outputObject) {
        codePackageService.queryAllCodePackageList(inputObject, outputObject);
    }

}
