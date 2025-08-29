/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.gitcode.entity.GitCodeIssueComment;
import com.skyeye.doc.gitcode.service.GitCodeIssueCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: GitCodeIssueCommentController
 * @Description: GitCode Issue评论管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "GitCode Issue评论管理", tags = "GitCode Issue评论管理", modelName = "GitCode Issue评论管理")
public class GitCodeIssueCommentController {

    @Autowired
    private GitCodeIssueCommentService gitCodeIssueCommentService;

    @ApiOperation(id = "writeIssueComment", value = "创建Issue评论", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = GitCodeIssueComment.class)
    @RequestMapping("/post/GitCodeIssueCommentController/writeIssueComment")
    public void writeIssueComment(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueCommentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteIssueCommentById", value = "删除Issue评论", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "commentId", name = "commentId", value = "评论ID", required = "required")})
    @RequestMapping("/post/GitCodeIssueCommentController/deleteIssueCommentById")
    public void deleteIssueCommentById(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueCommentService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueCommentList", value = "获取Issue评论列表", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/GitCodeIssueCommentController/queryIssueCommentList")
    public void queryIssueCommentList(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueCommentService.queryPageList(inputObject, outputObject);
    }

}
