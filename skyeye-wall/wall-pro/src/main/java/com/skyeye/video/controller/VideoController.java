package com.skyeye.video.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.video.entity.Video;
import com.skyeye.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @ClassName: VideoController
 * @Description: 视频管理
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "视频管理", tags = "视频管理", modelName = "视频管理")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 新增/编辑视频
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeVideo", value = "新增/编辑视频", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Video.class)
    @RequestMapping("/post/VideoController/writeVideo")
    public void writeVideo(InputObject inputObject, OutputObject outputObject) {
        videoService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询视频信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryVideoById", value = "根据id查询视频信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VideoController/queryVideoById")
    public void queryVideoById(InputObject inputObject, OutputObject outputObject) {
        videoService.selectById(inputObject, outputObject);
    }

    /**
     * 根据id删除视频信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteVideoById", value = "根据id删除视频信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VideoController/deleteVideoById")
    public void deleteVideoById(InputObject inputObject, OutputObject outputObject) {
        videoService.deleteById(inputObject, outputObject);
    }

    /**
     * 分页获取我的视频列表
     *
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyVideoList", value = "分页获取我的视频列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoController/queryMyVideoList")
    public void queryMyVideoList(InputObject inputObject, OutputObject outputObject) {
        videoService.queryMyVideoList(inputObject, outputObject);
    }


    /**
     * 点赞或取消点赞
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "supportOrNotVideo", value = "点赞或取消点赞", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "videoId", name = "videoId", value = "视频id", required = "required")
    })
    @RequestMapping("/post/VideoController/supportOrNotVideo")
    public void supportOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        videoService.supportOrNotVideo(inputObject, outputObject);
    }

    /**
     * 收藏或取消收藏
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "collectOrNotVideo", value = "收藏或取消收藏", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "videoId", name = "videoId", value = "视频id", required = "required")
    })
    @RequestMapping("/post/VideoController/collectOrNotVideo")
    public void collectOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        videoService.collectOrNotVideo(inputObject, outputObject);
    }

    /**
     * 分页获取我点赞的视频
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMySupportVideo", value = "分页获取我点赞的视频", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoController/queryMySupportVideo")
    public void queryMySupportVideo(InputObject inputObject, OutputObject outputObject) {
        videoService.queryMySupportVideo(inputObject, outputObject);
    }

    /**
     * 分页获取我收藏的视频
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyCollectVideo", value = "分页获取我收藏的视频", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoController/queryMyCollectVideo")
    public void queryMyCollectVideo(InputObject inputObject, OutputObject outputObject) {
        videoService.queryMyCollectVideo(inputObject, outputObject);
    }

    /**
     * 刷新浏览量
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "refreshVisitVideo", value = "刷新浏览量", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "videoId", name = "videoId", value = "视频id", required = "required")
    })
    @RequestMapping("/post/VideoController/refreshVisitVideo")
    public void refreshVisitVideo(InputObject inputObject, OutputObject outputObject) {
        videoService.refreshVisitVideo(inputObject, outputObject);
    }

    /**
     * 分页获取全部视频
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllVideoList", value = "分页获取全部视频，根据点赞数量倒序排序", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VideoController/queryAllVideoList")
    public void queryAllVideoList(InputObject inputObject, OutputObject outputObject) {
        videoService.queryAllVideoList(inputObject, outputObject);
    }
}
