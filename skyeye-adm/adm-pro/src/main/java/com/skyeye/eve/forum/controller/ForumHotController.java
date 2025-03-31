/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.service.ForumHotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ForumHotController
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "获取热门贴管理", tags = "获取热门贴管理", modelName = "获取热门贴管理")
public class ForumHotController {

    @Autowired
    private ForumHotService forumHotService;

    /**
     * 获取热门贴
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryHotForumList", value = "获取热门贴", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumHotController/queryHotForumList")
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) {
        forumHotService.queryHotForumList(inputObject, outputObject);
    }

    /**
     * 获取热门标签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryHotTagList", value = "获取热门标签", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumHotController/queryHotTagList")
    public void queryHotTagList(InputObject inputObject, OutputObject outputObject) {
        forumHotService.queryHotTagList(inputObject, outputObject);
    }

}
