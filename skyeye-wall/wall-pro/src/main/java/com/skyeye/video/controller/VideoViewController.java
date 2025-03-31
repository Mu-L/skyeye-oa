package com.skyeye.video.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.video.entity.VideoView;
import com.skyeye.video.service.VideoViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: VideoViewController
 * @Description: 视频浏览记录管理
 * @author: skyeye云系列--lqy
 * @date: 2025/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "视频浏览记录管理", tags = "视频浏览记录管理", modelName = "视频浏览记录管理")
public class VideoViewController {

    @Autowired
    private VideoViewService videoViewService;

    /**
     * 新增浏览记录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeVideoView", value = "新增浏览记录", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = VideoView.class)
    @RequestMapping("/post/VideoViewController/writeVideoView")
    public void writeVideoView(InputObject inputObject, OutputObject outputObject) {
        videoViewService.createEntity(inputObject, outputObject);
    }

    /**
     * 获取浏览记录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllVideoView", value = "获取用户userId（objectId）浏览记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoViewController/queryAllVideoView")
    public void queryAllVideoView(InputObject inputObject, OutputObject outputObject) {
        videoViewService.queryAllVideoView(inputObject, outputObject);
    }



    /**
     * 根据id删除浏览记录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteVideoViewById", value = "根据id删除浏览记录", method = "DELETE", allUse = "2")
    @ApiImplicitParams(classBean = VideoView.class)
    @RequestMapping("/post/VideoViewController/deleteVideoViewById")
    public void deleteVideoViewById(InputObject inputObject, OutputObject outputObject) {
        videoViewService.deleteById(inputObject, outputObject);
    }

    /**
     * 一键删除浏览记录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteAllVideoView", value = "一键删除浏览记录", method = "DELETE", allUse = "2")
    @RequestMapping("/post/VideoViewController/deleteAllVideoView")
    public void deleteAllVideoView(InputObject inputObject, OutputObject outputObject) {
        videoViewService.deleteAllVideoView(inputObject,outputObject);
    }



}
