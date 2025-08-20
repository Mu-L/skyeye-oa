/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.member.entity.DocMemverLevel;
import com.skyeye.doc.member.service.DocMemverLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocMemverLevelController
 * @Description: 会员等级控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/20 9:12
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "会员等级管理", tags = "会员等级管理", modelName = "会员等级管理")
public class DocMemverLevelController {

    @Autowired
    private DocMemverLevelService docMemverLevelService;

    @ApiOperation(id = "queryDocMemverLevelList", value = "查询会员等级列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DocMemverLevelController/queryDocMemverLevelList")
    public void queryDocMemverLevelList(InputObject inputObject, OutputObject outputObject) {
        docMemverLevelService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDocMemverLevel", value = "新增/编辑会员等级", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DocMemverLevel.class)
    @RequestMapping("/post/DocMemverLevelController/writeDocMemverLevel")
    public void writeDocMemverLevel(InputObject inputObject, OutputObject outputObject) {
        docMemverLevelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDocMemverLevelById", value = "根据id删除会员等级", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DocMemverLevelController/deleteDocMemverLevelById")
    public void deleteDocMemverLevelById(InputObject inputObject, OutputObject outputObject) {
        docMemverLevelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllDocMemverLevelList", value = "查询所有会员等级", method = "GET", allUse = "2")
    @RequestMapping("/post/DocMemverLevelController/queryAllDocMemverLevelList")
    public void queryAllDocMemverLevelList(InputObject inputObject, OutputObject outputObject) {
        docMemverLevelService.queryAllDocMemverLevelList(inputObject, outputObject);
    }

}
