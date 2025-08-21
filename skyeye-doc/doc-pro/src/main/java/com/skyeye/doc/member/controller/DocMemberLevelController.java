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
import com.skyeye.doc.member.entity.DocMemberLevel;
import com.skyeye.doc.member.service.DocMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocMemberLevelController
 * @Description: 会员等级控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/20 9:12
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "会员等级管理", tags = "会员等级管理", modelName = "会员等级管理")
public class DocMemberLevelController {

    @Autowired
    private DocMemberLevelService docMemberLevelService;

    @ApiOperation(id = "queryDocMemberLevelList", value = "查询会员等级列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DocMemberLevelController/queryDocMemberLevelList")
    public void queryDocMemberLevelList(InputObject inputObject, OutputObject outputObject) {
        docMemberLevelService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDocMemberLevel", value = "新增/编辑会员等级", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DocMemberLevel.class)
    @RequestMapping("/post/DocMemberLevelController/writeDocMemberLevel")
    public void writeDocMemberLevel(InputObject inputObject, OutputObject outputObject) {
        docMemberLevelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDocMemberLevelById", value = "根据id删除会员等级", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DocMemberLevelController/deleteDocMemberLevelById")
    public void deleteDocMemberLevelById(InputObject inputObject, OutputObject outputObject) {
        docMemberLevelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllDocMemberLevelList", value = "查询所有会员等级", method = "GET", allUse = "2")
    @RequestMapping("/post/DocMemberLevelController/queryAllDocMemberLevelList")
    public void queryAllDocMemberLevelList(InputObject inputObject, OutputObject outputObject) {
        docMemberLevelService.queryAllDocMemberLevelList(inputObject, outputObject);
    }

}
