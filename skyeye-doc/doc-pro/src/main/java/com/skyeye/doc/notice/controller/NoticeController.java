/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.notice.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: NoticeController
 * @Description: 消息通知控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/21 20:54
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "消息通知", tags = "消息通知", modelName = "消息通知")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation(id = "queryNoticeList", value = "获取消息通知列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/NoticeController/queryNoticeList")
    public void queryNoticeList(InputObject inputObject, OutputObject outputObject) {
        noticeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNotReadNoticeNum", value = "获取未读消息通知的数量", method = "GET", allUse = "2")
    @RequestMapping("/post/NoticeController/queryNotReadNoticeNum")
    public void queryNotReadNoticeNum(InputObject inputObject, OutputObject outputObject) {
        noticeService.queryNotReadNoticeNum(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNoticeById", value = "根据ID获取消息通知详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "消息通知ID", required = "required")})
    @RequestMapping("/post/NoticeController/queryNoticeById")
    public void queryNoticeById(InputObject inputObject, OutputObject outputObject) {
        noticeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "markAllNoticeAsRead", value = "一键已读", method = "GET", allUse = "2")
    @RequestMapping("/post/NoticeController/markAllNoticeAsRead")
    public void markAllNoticeAsRead(InputObject inputObject, OutputObject outputObject) {
        noticeService.markAllNoticeAsRead(inputObject, outputObject);
    }

}
