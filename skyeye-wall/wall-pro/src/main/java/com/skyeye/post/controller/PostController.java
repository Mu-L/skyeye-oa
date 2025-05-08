/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.post.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.post.entity.Post;
import com.skyeye.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PostController
 * @Description: 帖子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "帖子管理", tags = "帖子管理", modelName = "帖子管理")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取帖子信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPostList", value = "获取帖子信息列表", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PostController/queryPostList")
    public void queryPostList(InputObject inputObject, OutputObject outputObject) {
        postService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID获取帖子信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPostById", value = "根据ID获取帖子信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PostController/queryPostById")
    public void queryPostById(InputObject inputObject, OutputObject outputObject) {
        postService.selectById(inputObject, outputObject);
    }

    /**
     * 新增帖子信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertPost", value = "新增帖子信息", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = Post.class)
    @RequestMapping("/post/PostController/insertPost")
    public void insertPost(InputObject inputObject, OutputObject outputObject) {
        postService.createEntity(inputObject, outputObject);
    }

    /**
     * 根据ID删除帖子信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deletePostById", value = "根据ID删除帖子信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PostController/deletePostById")
    public void deletePostById(InputObject inputObject, OutputObject outputObject) {
        postService.deleteById(inputObject, outputObject);
    }

    /**
     * 通过点赞获取帖子信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPostListByUpvote", value = "通过点赞获取帖子信息列表", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PostController/queryPostListByUpvote")
    public void queryPostListByUpvote(InputObject inputObject, OutputObject outputObject) {
        postService.queryPostListByUpvote(inputObject, outputObject);
    }

    /**
     * 通过评论获取帖子信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPostListByComment", value = "通过评论获取帖子信息列表", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PostController/queryPostListByComment")
    public void queryPostListByComment(InputObject inputObject, OutputObject outputObject) {
        postService.queryPostListByComment(inputObject, outputObject);
    }

    /**
     * 获取热门帖子信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryHotPostList", value = "获取热门帖子信息列表", method = "POST", allUse = "0")
    @RequestMapping("/post/PostController/queryHotPostList")
    public void queryHotPostList(InputObject inputObject, OutputObject outputObject) {
        postService.queryHotPostList(inputObject, outputObject);
    }

    /**
     * 获取用户的总点赞、评论、帖子数量
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUserPostCount", value = "获取用户的总点赞、评论、帖子数量", method = "POST", allUse = "0")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户id", required = "required")
    )
    @RequestMapping("/post/PostController/queryUserPostCount")
    public void queryUserPostCount(InputObject inputObject, OutputObject outputObject) {
        postService.queryUserPostCount(inputObject, outputObject);
    }

    /**
     * 管理员删除帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deletePost", value = "管理员删除帖子", method = "DELETE", allUse = "2")
    @ApiImplicitParams(
        @ApiImplicitParam(id = "id", name = "id", value = "帖子id", required = "required")
    )
    @RequestMapping("/post/PostController/deletePost")
    public void deletePost(InputObject inputObject, OutputObject outputObject) {
        postService.deletePost(inputObject, outputObject);
    }
}