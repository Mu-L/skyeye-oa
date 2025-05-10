package com.skyeye.notice.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.notice.entity.Notice;
import com.skyeye.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: NoticeController
 * @Description: 通知信息管理
 * @author: skyeye云系列--lqy
 * @date: 2024/4/24 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "通知信息管理", tags = "通知信息管理", modelName = "通知信息管理")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 新增通知
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertNotice", value = "新增通知", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Notice.class)
    @RequestMapping("/post/NoticeController/insertNotice")
    public void insertNotice(InputObject inputObject, OutputObject outputObject) {
        noticeService.createEntity(inputObject, outputObject);
    }

    /**
     * 根据类型获取我的通知列表(type字段)，不传则是获取全部通知
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryNoticeByType", value = "根据类型获取我的通知列表(type字段)，不传则是获取全部通知", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/NoticeController/queryNoticeByType")
    public void queryMyNoticeByType(InputObject inputObject, OutputObject outputObject) {
        noticeService.queryNoticeByType(inputObject, outputObject);
    }

    /**
     * 根据id进去通知详情
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryNoticeById", value = "根据id进去通知详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "require")
    })
    @RequestMapping("/post/NoticeController/queryNoticeById")
    public void queryNoticeById(InputObject inputObject, OutputObject outputObject) {
        noticeService.selectById(inputObject, outputObject);
    }

    /**
     * 根据id修改是否已读状态
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "updateStateById", value = "根据id修改是否已读状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "require")
    })
    @RequestMapping("/post/NoticeController/updateStateById")
    public void updateStateById(InputObject inputObject, OutputObject outputObject) {
        noticeService.updateStateById(inputObject, outputObject);
    }

    /**
     * 根据id删除通知
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteNoticeById", value = "根据id删除通知", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "require")
    })
    @RequestMapping("/post/NoticeController/deleteNoticeById")
    public void deleteNoticeById(InputObject inputObject, OutputObject outputObject) {
        noticeService.deleteById(inputObject, outputObject);
    }

    /**
     * 查询未读数量
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUnReadNum", value = "查询未读数量", method = "POST", allUse = "2")
    @RequestMapping("/post/NoticeController/queryUnReadNum")
    public void queryUnReadNum(InputObject inputObject, OutputObject outputObject) {
        noticeService.queryUnReadNum(inputObject, outputObject);
    }

    @ApiOperation(id = "updateAllNoticeRead", value = "一键已读", method = "POST", allUse = "2")
    @RequestMapping("/post/NoticeController/updateAllNoticeRead")
    public void updateAllNoticeRead(InputObject inputObject, OutputObject outputObject) {
        noticeService.updateAllNoticeRead(inputObject, outputObject);
    }

    @ApiOperation(id = "sharePostOrComment", value = "分享帖子或者帖子评论", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "postId", name = "postId", value = "帖子id", required = "require"),
            @ApiImplicitParam(id = "commentId", name = "commentId", value = "评论id"),
            @ApiImplicitParam(id= "userId", name = "userId", value = "分享的用户id", required = "required"),
            @ApiImplicitParam(id = "describe", name = "describe", value = "分享描述")
    })
    @RequestMapping("/post/NoticeController/sharePostOrComment")
    public void sharePostOrComment(InputObject inputObject, OutputObject outputObject) {
        noticeService.sharePostOrComment(inputObject, outputObject);
    }

    @ApiOperation(id = "shareVideoOrComment", value = "分享视频或者视频评论", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "videoId", name = "videoId", value = "帖子id", required = "require"),
            @ApiImplicitParam(id = "commentId", name = "commentId", value = "评论id"),
            @ApiImplicitParam(id= "userId", name = "userId", value = "分享的用户id", required = "required"),
            @ApiImplicitParam(id = "describe", name = "describe", value = "分享描述")
    })
    @RequestMapping("/post/NoticeController/shareVideoOrComment")
    public void shareVideoOrComment(InputObject inputObject, OutputObject outputObject) {
        noticeService.shareVideoOrComment(inputObject, outputObject);
    }

    @ApiOperation(id = "shareCircle", value = "分享圈子", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "circleId", name = "circleId", value = "圈子id", required = "require"),
            @ApiImplicitParam(id= "userId", name = "userId", value = "分享的用户id", required = "required"),
            @ApiImplicitParam(id = "describe", name = "describe", value = "分享描述")
    })
    @RequestMapping("/post/NoticeController/shareCircle")
    public void shareCircle(InputObject inputObject, OutputObject outputObject) {
        noticeService.shareCircle(inputObject, outputObject);
    }


}
