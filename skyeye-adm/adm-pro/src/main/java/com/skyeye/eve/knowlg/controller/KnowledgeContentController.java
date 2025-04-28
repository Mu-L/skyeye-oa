/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.knowlg.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.knowlg.entity.KnowledgeContent;
import com.skyeye.eve.knowlg.service.KnowledgeContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: KnowledgeContentController
 * @Description: 知识库管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/21 15:27
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "知识库", tags = "知识库", modelName = "知识库管理")
public class KnowledgeContentController {

    @Autowired
    private KnowledgeContentService knowledgeContentService;

    @ApiOperation(id = "knowledgecontent001", value = "获取我的知识库列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/KnowledgeContentController/queryKnowledgeContentList")
    public void queryKnowledgeContentList(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.queryPageList(inputObject, outputObject);
    }


    @ApiOperation(id = "writeKnowledgeContent", value = "新增/编辑知识库", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = KnowledgeContent.class)
    @RequestMapping("/post/KnowledgeContentController/writeKnowledgeContent")
    public void writeKnowledgeContent(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteKnowledgeContentById", value = "删除知识库", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id", required = "required")})
    @RequestMapping("/post/KnowledgeContentController/deleteKnowledgeContentById")
    public void deleteKnowledgeContentById(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryKnowledgeContentById", value = "知识库详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id", required = "required")})
    @RequestMapping("/post/KnowledgeContentController/queryKnowledgeContentById")
    public void queryKnowledgeContentById(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "knowledgecontent010", value = "获取待审核的知识库列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/KnowledgeContentController/queryAllKnowledgeContentList")
    public void queryAllKnowledgeContentList(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.queryAllKnowledgeContentList(inputObject, outputObject);
    }

    @ApiOperation(id = "knowledgecontent012", value = "获取知识库信息用于回显审核", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "知识库id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "审核结果", required = "required,num"),
        @ApiImplicitParam(id = "examineNopassReason", name = "examineNopassReason", value = "审核不通过原因")})
    @RequestMapping("/post/KnowledgeContentController/editKnowledgeContentToCheck")
    public void editKnowledgeContentToCheck(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.editKnowledgeContentToCheck(inputObject, outputObject);
    }

    @ApiOperation(id = "knowledgecontent016", value = "获取企业知识库列表(已审核通过)", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/KnowledgeContentController/queryAllPassKnowledgeContentList")
    public void queryAllPassKnowledgeContentList(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.queryAllPassKnowledgeContentList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEightPassKnowlgList", value = "获取近期八条已审核的知识库", method = "GET", allUse = "2")
    @RequestMapping("/post/KnowledgeContentController/queryEightPassKnowlgList")
    public void queryEightPassKnowlgList(InputObject inputObject, OutputObject outputObject) {
        knowledgeContentService.queryEightPassKnowlgList(inputObject, outputObject);
    }

}
