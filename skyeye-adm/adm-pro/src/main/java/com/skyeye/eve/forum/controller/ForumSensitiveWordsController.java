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
import com.skyeye.eve.forum.entity.ForumSensitiveWords;
import com.skyeye.eve.forum.service.ForumSensitiveWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "论坛敏感词管理", tags = "论坛敏感词管理", modelName = "论坛敏感词管理")
public class ForumSensitiveWordsController {

    @Autowired
    private ForumSensitiveWordsService forumSensitiveWordsService;

    /**
     * 获取论坛敏感词列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryForumSensitiveWordsList", value = "获取论坛敏感词列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumSensitiveWordsController/queryForumSensitiveWordsList")
    public void queryForumSensitiveWordsList(InputObject inputObject, OutputObject outputObject) {
        forumSensitiveWordsService.queryPageList(inputObject, outputObject);
    }


    /**
     * 添加/编辑论坛敏感词
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertForumSensitiveWordsMation", value = "添加/编辑论坛敏感词", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ForumSensitiveWords.class)
    @RequestMapping("/post/ForumSensitiveWordsController/insertForumSensitiveWordsMation")
    public void insertForumSensitiveWordsMation(InputObject inputObject, OutputObject outputObject) {
        forumSensitiveWordsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除论坛敏感词
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteForumSensitiveWordsById", value = "根据id删除论坛敏感词", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ForumSensitiveWordsController/deleteForumSensitiveWordsById")
    public void deleteForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) {
        forumSensitiveWordsService.deleteById(inputObject, outputObject);
    }

    /**
     * 通过id查找对应的论坛敏感词信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectForumSensitiveWordsById", value = "通过id查找对应的论坛敏感词信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ForumSensitiveWordsController/selectForumSensitiveWordsById")
    public void selectForumSensitiveWordsById(InputObject inputObject, OutputObject outputObject) {
        forumSensitiveWordsService.selectById(inputObject, outputObject);
    }
}
