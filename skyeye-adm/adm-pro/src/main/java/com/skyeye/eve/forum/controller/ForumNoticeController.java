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
import com.skyeye.eve.forum.service.ForumNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName: ForumTagController
 * @Description: 论坛标签管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "论坛通知管理", tags = "论坛通知管理", modelName = "论坛通知管理")
public class ForumNoticeController {

    @Autowired
    private ForumNoticeService forumNoticeService;

    @ApiOperation(id = "queryMyNoticeList", value = "获取我的通知列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumNoticeController/queryMyNoticeList")
    public void queryMyNoticeList(InputObject inputObject, OutputObject outputObject) {
        forumNoticeService.queryMyNoticeList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteForumNoticeById", value = "根据通知id删除通知", method = "DELETE", allUse = "2")
    @ApiImplicitParams(
            @ApiImplicitParam(id = "id", name = "id",value = "主键id", required = "required"))
    @RequestMapping("/post/ForumNoticeController/deleteForumNoticeById")
    public void deleteForumNoticeById(InputObject inputObject, OutputObject outputObject) {
        forumNoticeService.deleteById(inputObject, outputObject);
    }

}
