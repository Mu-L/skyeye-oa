package com.skyeye.office.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.service.DocumentCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocumentCommentController
 * @Description: 文档评论控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@RestController
@Api(value = "文档评论", tags = "文档评论", modelName = "文档协同编辑模块")
public class DocumentCommentController {

    @Autowired
    private DocumentCommentService documentCommentService;

    /**
     * 添加评论
     */
    @ApiOperation(id = "addComment", value = "添加评论", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "content", name = "content", value = "评论内容", required = "required"),
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父评论ID")
    })
    @RequestMapping("/post/DocumentCommentController/addComment")
    public void addComment(InputObject inputObject, OutputObject outputObject) {
        documentCommentService.addComment(inputObject, outputObject);
    }

    /**
     * 删除评论
     */
    @ApiOperation(id = "deleteComment", value = "删除评论", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "评论ID", required = "required")
    })
    @RequestMapping("/post/DocumentCommentController/deleteComment")
    public void deleteComment(InputObject inputObject, OutputObject outputObject) {
        documentCommentService.deleteComment(inputObject, outputObject);
    }

    /**
     * 获取评论列表
     */
    @ApiOperation(id = "getCommentList", value = "获取评论列表", method = "GET", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required")
    })
    @RequestMapping("/post/DocumentCommentController/getCommentList")
    public void getCommentList(InputObject inputObject, OutputObject outputObject) {
        documentCommentService.getCommentList(inputObject, outputObject);
    }

    /**
     * 获取回复列表
     */
    @ApiOperation(id = "getReplyList", value = "获取回复列表", method = "GET", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父评论ID", required = "required")
    })
    @RequestMapping("/post/DocumentCommentController/getReplyList")
    public void getReplyList(InputObject inputObject, OutputObject outputObject) {
        documentCommentService.getReplyList(inputObject, outputObject);
    }
} 