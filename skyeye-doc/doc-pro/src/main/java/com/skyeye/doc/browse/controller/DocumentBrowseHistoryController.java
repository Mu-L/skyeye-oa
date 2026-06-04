/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.browse.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.browse.service.DocumentBrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文档浏览历史控制层
 */
@RestController
@Api(value = "文档浏览历史", tags = "文档浏览历史", modelName = "文档管理")
public class DocumentBrowseHistoryController {

    @Autowired
    private DocumentBrowseHistoryService documentBrowseHistoryService;

    @ApiOperation(id = "recordDocumentBrowseHistory", value = "记录文档浏览历史", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档id", required = "required")})
    @RequestMapping("/post/DocumentBrowseHistoryController/recordDocumentBrowseHistory")
    public void recordDocumentBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryService.recordDocumentBrowseHistory(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyDocumentBrowseHistoryList", value = "查询我的文档浏览历史", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryController/queryMyDocumentBrowseHistoryList")
    public void queryMyDocumentBrowseHistoryList(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryService.queryMyDocumentBrowseHistoryList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMyDocumentBrowseHistoryById", value = "删除我的浏览历史", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DocumentBrowseHistoryController/deleteMyDocumentBrowseHistoryById")
    public void deleteMyDocumentBrowseHistoryById(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryService.deleteMyDocumentBrowseHistoryById(inputObject, outputObject);
    }

    @ApiOperation(id = "clearMyDocumentBrowseHistory", value = "清空我的浏览历史", method = "DELETE", allUse = "2")
    @RequestMapping("/post/DocumentBrowseHistoryController/clearMyDocumentBrowseHistory")
    public void clearMyDocumentBrowseHistory(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryService.clearMyDocumentBrowseHistory(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDocumentBrowseHistoryList", value = "后台查询文档浏览历史", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryController/queryDocumentBrowseHistoryList")
    public void queryDocumentBrowseHistoryList(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryService.queryPageList(inputObject, outputObject);
    }

}
