package com.skyeye.videocomment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videocomment.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: VideoCommentController
 * @Description: 视频评论管理
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "视频评论管理", tags = "视频评论管理", modelName = "视频评论管理")
public class VideoCommentController {
    @Autowired
    private VideoCommentService videoCommentService;

    /**
     * 新增视频评论信息   评论总数量+1
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertVideoComment", value = "新增视频评论信息", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = VideoComment.class)
    @RequestMapping("/post/VideoCommentController/insertVideoComment")
    public void insertVideoComment(InputObject inputObject, OutputObject outputObject) {
        videoCommentService.createEntity(inputObject, outputObject);
    }

    /**
     * 根据ID删除视频评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteVideoCommentById", value = "根据ID删除视频评论信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VideoCommentController/deleteVideoCommentById")
    public void deleteVideoCommentById(InputObject inputObject, OutputObject outputObject) {
        videoCommentService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据视频id获取评论信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCommentListByVideoId", value = "根据视频id获取评论信息", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoCommentController/queryCommentListByVideoId")
    public void queryCommentListByVideoId(InputObject inputObject, OutputObject outputObject) {
        videoCommentService.queryCommentListByVideoId(inputObject, outputObject);
    }

    /**
     * 点赞或取消点赞评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "supportOrNotComment", value = "点赞或取消点赞评论", method = "POST", allUse = "0")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "commentId", name = "commentId", value = "评论id", required = "required")
    })
    @RequestMapping("/post/VideoCommentController/supportOrNotComment")
    public void supportOrNotComment(InputObject inputObject, OutputObject outputObject) {
        videoCommentService.supportOrNotComment(inputObject, outputObject);
    }

    /**
     * 根据ID获取评论信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCommentById", value = "根据ID获取评论信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VideoCommentController/queryCommentById")
    public void queryCommentById(InputObject inputObject, OutputObject outputObject) {
        videoCommentService.selectById(inputObject, outputObject);
    }
}
