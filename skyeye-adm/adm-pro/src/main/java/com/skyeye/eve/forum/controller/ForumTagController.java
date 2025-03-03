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
import com.skyeye.eve.forum.entity.ForumTag;
import com.skyeye.eve.forum.service.ForumTagService;
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
@Api(value = "论坛标签管理", tags = "论坛标签管理", modelName = "论坛标签管理")
public class ForumTagController {

    @Autowired
    private ForumTagService forumTagService;

    /**
     * 获取论坛标签列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryForumTagList", value = "获取论坛标签列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumTagController/queryForumTagList")
    public void queryForumTagList(InputObject inputObject, OutputObject outputObject) {
        forumTagService.queryPageList(inputObject, outputObject);
    }


    /**
     * 添加论坛标签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertForumTagMation", value = "添加/编辑论坛标签", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ForumTag.class)
    @RequestMapping("/post/ForumTagController/insertForumTagMation")
    public void insertForumTagMation(InputObject inputObject, OutputObject outputObject) {
        forumTagService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除论坛标签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteForumTagById", value = "删除论坛标签", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "标签id", required = "required")})
    @RequestMapping("/post/ForumTagController/deleteForumTagById")
    public void deleteForumTagById(InputObject inputObject, OutputObject outputObject) {
        forumTagService.deleteForumTagById(inputObject, outputObject);
    }

    /**
     * 通过id查找对应的论坛标签信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryForumTagById", value = "通过id查找对应的论坛标签信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "标签id", required = "required")})
    @RequestMapping("/post/ForumTagController/queryForumTagById")
    public void queryForumTagById(InputObject inputObject, OutputObject outputObject) {
        forumTagService.selectById(inputObject, outputObject);
    }

    /**
     * 获取已经上线的论坛标签列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryForumTagUpStateList", value = "获取已经上线的论坛标签列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumTagController/queryForumTagUpStateList")
    public void queryForumTagUpStateList(InputObject inputObject, OutputObject outputObject) {
        forumTagService.queryForumTagUpStateList(inputObject, outputObject);
    }

}
