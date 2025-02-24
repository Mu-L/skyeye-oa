/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumHistoryView;
import com.skyeye.eve.forum.service.ForumHistoryViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: HistoryPostController
 * @Description: 历史帖子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "历史帖子管理", tags = "历史帖子管理", modelName = "历史帖子管理")
public class ForumHistoryViewController {

    @Autowired
    private ForumHistoryViewService forumHistoryViewService;

    /**
     * 新增历史帖子信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertForumHistoryView", value = "新增历史帖子信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ForumHistoryView.class)
    @RequestMapping("/post/HistoryPostController/insertForumHistoryView")
    public void insertForumHistoryView(InputObject inputObject, OutputObject outputObject) {
        forumHistoryViewService.createEntity(inputObject, outputObject);
    }

//    /**
//     * 一键删除历史帖子信息
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "deleteMyHistoryPost", value = "一键删除历史帖子信息", method = "POST", allUse = "2")
//    @RequestMapping("/post/HistoryPostController/deleteMyHistoryPost")
//    public void deleteMyHistoryPost(InputObject inputObject, OutputObject outputObject) {
//        historyPostService.deleteMyHistoryPost(inputObject, outputObject);
//    }
//
//    /**
//     * 批量删除历史帖子信息
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "deleteHistoryPostByIds", value = "批量删除历史帖子信息", method = "POST", allUse = "2")
//    @ApiImplicitParams({
//        @ApiImplicitParam(id = "ids", name = "ids", value = "id列表", required = "required")})
//    @RequestMapping("/post/HistoryPostController/deleteHistoryPostByIds")
//    public void deleteHistoryPostByIds(InputObject inputObject, OutputObject outputObject) {
//        historyPostService.deleteHistoryPostByIds(inputObject, outputObject);
//    }
}
