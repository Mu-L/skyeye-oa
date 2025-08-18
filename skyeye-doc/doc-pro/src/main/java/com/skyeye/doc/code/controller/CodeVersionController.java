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
import com.skyeye.doc.code.entity.CodeVersion;
import com.skyeye.doc.code.service.CodeVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CodeVersionController
 * @Description: 代码版本控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 21:12
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "代码版本管理", tags = "代码版本管理", modelName = "代码版本管理")
public class CodeVersionController {

    @Autowired
    private CodeVersionService codeVersionService;

    @ApiOperation(id = "queryCodeVersionList", value = "查询代码版本列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CodeVersionController/queryCodeVersionList")
    public void queryCodeVersionList(InputObject inputObject, OutputObject outputObject) {
        codeVersionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCodeVersion", value = "新增/编辑代码版本", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CodeVersion.class)
    @RequestMapping("/post/CodeVersionController/writeCodeVersion")
    public void writeCodeVersion(InputObject inputObject, OutputObject outputObject) {
        codeVersionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCodeVersionById", value = "根据id删除代码版本", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CodeVersionController/deleteCodeVersionById")
    public void deleteCodeVersionById(InputObject inputObject, OutputObject outputObject) {
        codeVersionService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllCodeVersionList", value = "查询所有代码版本", method = "GET", allUse = "2")
    @RequestMapping("/post/CodeVersionController/queryAllCodeVersionList")
    public void queryAllCodeVersionList(InputObject inputObject, OutputObject outputObject) {
        codeVersionService.queryAllCodeVersionList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllReleaseCodeVersionList", value = "查询所有已经发布并且时间有效的代码版本", method = "GET", allUse = "2")
    @RequestMapping("/post/CodeVersionController/queryAllReleaseCodeVersionList")
    public void queryAllReleaseCodeVersionList(InputObject inputObject, OutputObject outputObject) {
        codeVersionService.queryAllReleaseCodeVersionList(inputObject, outputObject);
    }

}
