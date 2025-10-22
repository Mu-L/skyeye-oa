/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.code.service.CodeVersionService;
import com.skyeye.doc.gitcode.dao.GitCodeIssueDao;
import com.skyeye.doc.gitcode.entity.GitCodeIssue;
import com.skyeye.doc.gitcode.service.GitCodeIssueService;
import com.skyeye.doc.gitcode.util.GitCodeApiClient;
import com.skyeye.doc.member.service.DocMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: GitCodeIssueServiceImpl
 * @Description: GitCode Issue服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Slf4j
@Service
@SkyeyeService(name = "GitCode Issue管理", groupName = "GitCode管理", tenant = TenantEnum.PLATE)
public class GitCodeIssueServiceImpl extends SkyeyeBusinessServiceImpl<GitCodeIssueDao, GitCodeIssue> implements GitCodeIssueService {

    @Autowired
    private GitCodeApiClient gitCodeApiClient;

    @Autowired
    private DocMemberService docMemberService;

    @Autowired
    private CodeVersionService codeVersionService;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        docMemberService.setNameMationForMap(beans, "createId", "memberName", StrUtil.EMPTY);
        codeVersionService.setMationForMap(beans, "versionId", "versionMation");
        return beans;
    }

    @Override
    protected QueryWrapper<GitCodeIssue> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<GitCodeIssue> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getVersionId), commonPageInfo.getObjectId());
        }
        if (WhetherEnum.ENABLE_USING.getKey().toString().equals(commonPageInfo.getType())) {
            // 是否只查询我发布的
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getCreateId), userId);
        }
        return queryWrapper;
    }

    @Override
    protected void createPrepose(GitCodeIssue entity) {
        // 调用GitCode API创建Issue
        JSONObject result = gitCodeApiClient.createIssue(entity.getTitle(), entity.getDescription(), entity.getAssigneeId(), entity.getLabels());
        entity.setIssueId(result.getStr("number")); // GitCode v5返回的Issue number
        entity.setProjectUrl(gitCodeApiClient.projectUrl);
    }

    @Override
    protected void updatePostpose(GitCodeIssue entity, String userId) {
        // 构建更新数据
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("title", entity.getTitle());
        updateData.put("description", entity.getDescription());
        updateData.put("state", entity.getState());
        updateData.put("assigneeId", entity.getAssigneeId());
        updateData.put("labels", entity.getLabels());

        GitCodeIssue gitCodeIssue = selectById(entity.getId());
        // 调用GitCode API更新Issue
        gitCodeApiClient.updateIssue(gitCodeIssue.getIssueId(), updateData);
    }

    @Override
    public GitCodeIssue selectById(String id) {
        GitCodeIssue gitCodeIssue = super.selectById(id);
        codeVersionService.setDataMation(gitCodeIssue, GitCodeIssue::getVersionId);
        return gitCodeIssue;
    }

    @Override
    public void updateIssueState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String state = params.get("state").toString();

        GitCodeIssue gitCodeIssue = selectById(id);
        if (ObjectUtil.isEmpty(gitCodeIssue) || StrUtil.isEmpty(gitCodeIssue.getId())) {
            return;
        }

        UpdateWrapper<GitCodeIssue> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(GitCodeIssue::getState), state);
        update(updateWrapper);
        refreshCache(id);

        // 调用GitCode API更新Issue状态
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("state", state);
        gitCodeApiClient.updateIssue(gitCodeIssue.getIssueId(), updateData);
    }

    @Override
    public void insertUploadImageToIssue(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String images = params.get("images").toString();
        String fileName = params.get("fileName").toString();
        try {
            // 调用GitCode API上传图片（使用base64编码）
            JSONObject result = gitCodeApiClient.uploadImage(images, fileName);

            if (result != null) {
                // 构建返回结果
                Map<String, Object> uploadResult = new HashMap<>();
                uploadResult.put("url", result.getStr("url"));
                uploadResult.put("alt", result.getStr("alt"));
                uploadResult.put("markdown", result.getStr("markdown"));
                uploadResult.put("fileName", fileName);

                outputObject.setBean(uploadResult);
                outputObject.settotal(1);
            } else {
                outputObject.setreturnMessage("图片上传失败");
            }

        } catch (Exception e) {
            log.error("上传图片到Issue失败", e);
            outputObject.setreturnMessage("上传图片失败: " + e.getMessage());
        }
    }

}
