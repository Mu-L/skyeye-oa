package com.skyeye.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.office.entity.DocumentVersion;
import com.skyeye.office.service.DocumentVersionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName: DocumentVersionServiceImpl
 * @Description: 文档版本管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Service
public class DocumentVersionServiceImpl extends SkyeyeBusinessServiceImpl<DocumentVersion> implements DocumentVersionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVersion(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String fileUrl = inputObject.getParams().getString("fileUrl");
        String versionDesc = inputObject.getParams().getString("versionDesc");
        Long fileSize = inputObject.getParams().getLong("fileSize");

        // 获取最新版本号
        Integer latestVersion = getLatestVersion(documentId);
        Integer newVersion = latestVersion + 1;

        // 创建新版本记录
        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(documentId);
        version.setVersion(newVersion);
        version.setFileUrl(fileUrl);
        version.setFileSize(fileSize);
        version.setVersionDesc(versionDesc);

        super.createEntity(version);
        outputObject.setBean(version);
    }

    @Override
    public void getVersionHistory(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");

        // 查询版本历史记录
        QueryWrapper<DocumentVersion> wrapper = new QueryWrapper<>();
        wrapper.eq("document_id", documentId)
            .orderByDesc("version");

        List<DocumentVersion> versionList = super.selectList(wrapper);
        outputObject.setBean(versionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackVersion(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        Integer version = inputObject.getParams().getInteger("version");

        // 获取指定版本信息
        DocumentVersion targetVersion = super.selectOne(ToolUtil.getWrapper(DocumentVersion.class)
            .eq("document_id", documentId)
            .eq("version", version));

        if (targetVersion == null) {
            throw new RuntimeException("指定版本不存在");
        }

        // 创建新版本（基于回滚的版本）
        DocumentVersion newVersion = new DocumentVersion();
        newVersion.setDocumentId(documentId);
        newVersion.setVersion(getLatestVersion(documentId) + 1);
        newVersion.setFileUrl(targetVersion.getFileUrl());
        newVersion.setFileSize(targetVersion.getFileSize());
        newVersion.setVersionDesc("回滚自版本" + version);

        super.createEntity(newVersion);
        outputObject.setBean(newVersion);
    }

    /**
     * 获取文档的最新版本号
     */
    private Integer getLatestVersion(String documentId) {
        QueryWrapper<DocumentVersion> wrapper = new QueryWrapper<>();
        wrapper.eq("document_id", documentId)
            .orderByDesc("version")
            .last("limit 1");

        DocumentVersion latestVersion = super.selectOne(wrapper);
        return latestVersion != null ? latestVersion.getVersion() : 0;
    }
} 