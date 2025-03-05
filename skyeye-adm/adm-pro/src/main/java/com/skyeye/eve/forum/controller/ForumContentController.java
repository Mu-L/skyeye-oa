/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.service.ForumContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "论坛话题管理", tags = "论坛话题管理", modelName = "论坛话题管理")
public class ForumContentController {

    @Autowired
    private ForumContentService forumContentService;

    /**
     * 获取我的帖子列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyForumContentList", value = "获取我的帖子列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumContentController/queryMyForumContentList")
    public void queryMyForumContentList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryMyForumContentList(inputObject, outputObject);
    }

    /**
     * 获取所有帖子列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllForumContentList", value = "获取所有帖子列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumContentController/queryAllForumContentList")
    public void queryAllForumContentList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑我的帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "saveOrUpdateEntity", value = "新增/编辑我的帖子", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ForumContent.class)
    @RequestMapping("/post/ForumContentController/saveOrUpdateEntity")
    public void insertForumContent(InputObject inputObject, OutputObject outputObject) {
        forumContentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteForumContentById", value = "删除帖子", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ForumContentController/deleteForumContentById")
    public void deleteForumContentById(InputObject inputObject, OutputObject outputObject) {
        forumContentService.deleteForumContentById(inputObject, outputObject);
    }

    /**
     * 帖子详情
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryForumContentById", value = "帖子详情", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ForumContentController/queryForumContentById")
    public void queryForumContentById(InputObject inputObject, OutputObject outputObject) {
        forumContentService.selectById(inputObject, outputObject);
    }

    /**
     * 获取最新帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryNewForumContentList", value = "获取最新帖子", method = "POST", allUse = "2")
    @RequestMapping("/post/ForumContentController/queryNewForumContentList")
    public void queryNewForumContentList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryNewForumContentList(inputObject, outputObject);
    }

    /**
     * 获取我的浏览信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryForumMyBrowerList", value = "获取我的浏览信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumContentController/queryForumMyBrowerList")
    public void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryForumMyBrowerList(inputObject, outputObject);
    }

    /**
     * 获取最新的前15条评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryNewCommentList", value = "获取最新的前15条评论", method = "POST", allUse = "2")
    @RequestMapping("/post/ForumContentController/queryNewCommentList")
    public void queryNewCommentList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryNewCommentList(inputObject, outputObject);
    }

    /**
     * 根据标签id获取帖子列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryForumListByTagId", value = "根据标签id获取帖子列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumContentController/queryForumListByTagId")
    public void queryForumListByTagId(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryForumListByTagId(inputObject, outputObject);
    }

    /**
     * 获取活跃用户
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryActiveUsersList", value = "获取活跃用户", method = "POST", allUse = "2")
    @RequestMapping("/post/ForumContentController/queryActiveUsersList")
    public void queryActiveUsersList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryActiveUsersList(inputObject, outputObject);
    }

    /**
     * 获取用户搜索的帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/ForumContentController/querySearchForumList")
    public void querySearchForumList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.querySearchForumList(inputObject, outputObject);
    }

}
