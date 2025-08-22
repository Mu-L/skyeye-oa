/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.code.entity.CodeSource;
import com.skyeye.doc.code.service.CodeSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CodeSourceController
 * @Description: 源代码控制器
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 8:25
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "源代码管理", tags = "源代码管理", modelName = "源代码管理")
public class CodeSourceController {

    @Autowired
    private CodeSourceService codeSourceService;

    @ApiOperation(id = "createCodeSource", value = "新增源代码", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CodeSource.class)
    @RequestMapping("/post/CodeSourceController/createCodeSource")
    public void createCodeSource(InputObject inputObject, OutputObject outputObject) {
        codeSourceService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "removeCodeSource", value = "删除源代码", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "versionId", name = "versionId", value = "版本id", required = "required"),
        @ApiImplicitParam(id = "packageId", name = "packageId", value = "源代码包id", required = "required")})
    @RequestMapping("/post/CodeSourceController/removeCodeSource")
    public void removeCodeSource(InputObject inputObject, OutputObject outputObject) {
        codeSourceService.removeCodeSource(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllReleaseCodeList", value = "查询所有已经发布并且时间有效的代码版本/源代码包/源代码", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "year", name = "year", value = "年份,格式yyyy", required = "required")})
    @RequestMapping("/post/CodeSourceController/queryAllReleaseCodeList")
    public void queryAllReleaseCodeList(InputObject inputObject, OutputObject outputObject) {
        codeSourceService.queryAllReleaseCodeList(inputObject, outputObject);
    }

}
