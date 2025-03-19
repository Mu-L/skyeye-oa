package com.skyeye.videotag.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.videotag.entity.VideoTag;
import com.skyeye.videotag.service.VideoTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: VideoTagController
 * @Description: 视频标签管理
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "视频标签管理", tags = "视频标签管理", modelName = "视频标签管理")
public class VideoTagController {

    @Autowired
    private VideoTagService videoTagService;


    /**
     * 添加视频标签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertVideoTag", value = "添加/编辑视频标签", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = VideoTag.class)
    @RequestMapping("/post/VideoTagController/insertVideoTag")
    public void insertVideoTag(InputObject inputObject, OutputObject outputObject) {
        videoTagService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取视频标签列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryVideoTagList", value = "获取视频标签列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoTagController/queryVideoTagList")
    public void queryVideoTagList(InputObject inputObject, OutputObject outputObject) {
        videoTagService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除视频标签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteVideoTagById", value = "删除视频标签", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "标签id", required = "required")})
    @RequestMapping("/post/VideoTagController/deleteVideoTagById")
    public void deleteVideoTagById(InputObject inputObject, OutputObject outputObject) {
        videoTagService.deleteVideoTagById(inputObject, outputObject);
    }

    /**
     * 通过id查找对应的视频标签信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryVideoTagById", value = "通过id查找对应的视频标签信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "标签id", required = "required")})
    @RequestMapping("/post/VideoTagController/queryVideoTagById")
    public void queryVideoTagById(InputObject inputObject, OutputObject outputObject) {
        videoTagService.selectById(inputObject, outputObject);
    }

    /**
     * 获取已经上线的视频标签列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryVideoTagUpStateList", value = "获取已经上线的视频标签列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoTagController/queryVideoTagUpStateList")
    public void queryVideoTagUpStateList(InputObject inputObject, OutputObject outputObject) {
        videoTagService.queryVideoTagUpStateList(inputObject, outputObject);
    }









}
