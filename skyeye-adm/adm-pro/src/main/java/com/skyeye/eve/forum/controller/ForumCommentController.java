package com.skyeye.eve.forum.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumComment;
import com.skyeye.eve.forum.service.ForumCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "论坛评论管理", tags = "论坛评论管理", modelName = "论坛评论管理")
public class ForumCommentController {

    @Autowired
    private ForumCommentService forumContentService;

    /**
     * 新增帖子评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "saveOrUpdateEntity", value = "新增帖子评论", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ForumComment.class)
    @RequestMapping("/post/ForumCommentController/insertForumCommentMation")
    public void insertForumCommentMation(InputObject inputObject, OutputObject outputObject) {
        forumContentService.createEntity(inputObject, outputObject);
    }

    /**
     * 获取帖子评论信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "saveOrUpdateEntity", value = "新增帖子评论", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ForumCommentController/queryForumCommentList")
    public void queryForumCommentList(InputObject inputObject, OutputObject outputObject) {
        forumContentService.queryList(inputObject, outputObject);
    }
}
