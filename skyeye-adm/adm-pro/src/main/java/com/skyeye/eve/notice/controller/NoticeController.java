/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.notice.entity.Notice;
import com.skyeye.eve.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: NoticeController
 * @Description: 公告管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/30 20:27
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "公告管理", tags = "公告管理", modelName = "公告管理")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @ApiOperation(id = "queryNoticeList", value = "获取公告列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/NoticeController/queryNoticeList")
    public void queryNoticeList(InputObject inputObject, OutputObject outputObject) {
        noticeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeNotice", value = "新增/编辑公告信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Notice.class)
    @RequestMapping("/post/NoticeController/writeNotice")
    public void writeNotice(InputObject inputObject, OutputObject outputObject) {
        noticeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteNoticeById", value = "根据ID删除公告", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/NoticeController/deleteNoticeById")
    public void deleteNoticeById(InputObject inputObject, OutputObject outputObject) {
        noticeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryUserReceivedNotice", value = "获取用户收到的公告", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/NoticeController/queryUserReceivedNotice")
    public void queryUserReceivedNotice(InputObject inputObject, OutputObject outputObject) {
        noticeService.queryUserReceivedNotice(inputObject, outputObject);
    }

    @ApiOperation(id = "queryUserReceivedTopNotice", value = "获取用户收到的前8条公告", method = "GET", allUse = "2")
    @RequestMapping("/post/NoticeController/queryUserReceivedTopNotice")
    public void queryUserReceivedTopNotice(InputObject inputObject, OutputObject outputObject) {
        noticeService.queryUserReceivedTopNotice(inputObject, outputObject);
    }

}
