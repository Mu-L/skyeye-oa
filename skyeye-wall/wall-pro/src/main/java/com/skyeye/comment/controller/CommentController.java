/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.comment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.comment.entity.Comment;
import com.skyeye.comment.service.CommentService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CommentController
 * @Description: 评论信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31.
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "评论管理", tags = "评论管理", modelName = "评论管理")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCommentList", value = "获取评论信息列表", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CommentController/queryCommentList")
    public void queryCommentList(InputObject inputObject, OutputObject outputObject) {
        commentService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增评论信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertComment", value = "新增评论信息", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = Comment.class)
    @RequestMapping("/post/CommentController/insertComment")
    public void insertComment(InputObject inputObject, OutputObject outputObject) {
        commentService.createEntity(inputObject, outputObject);
    }

    /**
     * 根据ID删除评论信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteCommentById", value = "根据ID删除评论信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CommentController/deleteCommentById")
    public void deleteCommentById(InputObject inputObject, OutputObject outputObject) {
        commentService.deleteById(inputObject, outputObject);
    }
}