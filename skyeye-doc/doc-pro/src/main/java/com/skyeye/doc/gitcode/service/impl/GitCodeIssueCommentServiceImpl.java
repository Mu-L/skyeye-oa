/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.gitcode.dao.GitCodeIssueCommentDao;
import com.skyeye.doc.gitcode.entity.GitCodeIssue;
import com.skyeye.doc.gitcode.entity.GitCodeIssueComment;
import com.skyeye.doc.gitcode.service.GitCodeIssueCommentService;
import com.skyeye.doc.gitcode.service.GitCodeIssueService;
import com.skyeye.doc.gitcode.util.GitCodeApiClient;
import com.skyeye.doc.member.service.DocMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: GitCodeIssueCommentServiceImpl
 * @Description: GitCode Issue评论服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "GitCode Issue评论管理", groupName = "GitCode管理", tenant = TenantEnum.PLATE)
public class GitCodeIssueCommentServiceImpl extends SkyeyeBusinessServiceImpl<GitCodeIssueCommentDao, GitCodeIssueComment> implements GitCodeIssueCommentService {

    @Autowired
    private GitCodeApiClient gitCodeApiClient;

    @Autowired
    private GitCodeIssueService gitCodeIssueService;

    @Autowired
    private DocMemberService docMemberService;

    @Override
    protected QueryWrapper<GitCodeIssueComment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<GitCodeIssueComment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(GitCodeIssueComment::getIssueId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        docMemberService.setNameMationForMap(beans, "createId", "memberName", StrUtil.EMPTY);
        return beans;
    }

    @Override
    protected void createPrepose(GitCodeIssueComment entity) {
        entity.setProjectUrl(gitCodeApiClient.projectUrl);
        GitCodeIssue gitCodeIssue = gitCodeIssueService.selectById(entity.getIssueId());
        // 调用GitCode API创建Issue评论
        JSONObject result = gitCodeApiClient.createIssueComment(gitCodeIssue.getIssueId(), entity.getBody());
        entity.setCommentId(result.getStr("id")); // GitCode返回的评论ID
    }

    @Override
    protected void updatePostpose(GitCodeIssueComment entity, String userId) {
        GitCodeIssueComment oldGitIssueComment = selectById(entity.getId());
        GitCodeIssue gitCodeIssue = gitCodeIssueService.selectById(entity.getIssueId());
        // 调用GitCode API更新Issue评论
        gitCodeApiClient.updateIssueComment(gitCodeIssue.getIssueId(), oldGitIssueComment.getCommentId(), entity.getBody());
    }

    @Override
    protected void deletePostpose(GitCodeIssueComment entity) {
        GitCodeIssue gitCodeIssue = gitCodeIssueService.selectById(entity.getIssueId());
        // 调用GitCode API删除Issue评论
        gitCodeApiClient.deleteIssueComment(gitCodeIssue.getIssueId(), entity.getCommentId());
    }

}
