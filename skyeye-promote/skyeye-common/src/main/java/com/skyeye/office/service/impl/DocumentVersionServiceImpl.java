package com.skyeye.office.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.office.dao.DocumentVersionDao;
import com.skyeye.office.entity.DocumentVersion;
import com.skyeye.office.service.DocumentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DocumentVersionServiceImpl
 * @Description: 文档版本管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Service
public class DocumentVersionServiceImpl extends SkyeyeBusinessServiceImpl<DocumentVersionDao,DocumentVersion> implements DocumentVersionService {

    @Autowired
    private DocumentVersionDao documentVersionDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVersion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = InputObject.getLogParamsStatic();
        String documentId = map.get("documentId").toString();
        String fileUrl = map.get("fileUrl").toString();
        String versionDesc = map.get("versionDesc").toString();
        Long fileSize = (Long) map.get("fileSize");
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
        super.createEntity(version, StrUtil.EMPTY);
        outputObject.setBean(version);
    }

    @Override
    public void getVersionHistory(InputObject inputObject, OutputObject outputObject) {
        String documentId = InputObject.getLogParamsStatic().get("documentId").toString();
//        String documentId = inputObject.getParams().getString("documentId");

        // 查询版本历史记录
        QueryWrapper<DocumentVersion> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(DocumentVersion::getDocumentId), documentId)
            .orderByDesc(MybatisPlusUtil.toColumns(DocumentVersion::getVersion));
        List<DocumentVersion> versionList = list(wrapper);
        outputObject.setBean(versionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackVersion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = InputObject.getLogParamsStatic();
        String documentId = map.get("documentId").toString();
        Integer version = (Integer) map.get("version");
        // 获取指定版本信息
        QueryWrapper<DocumentVersion> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(DocumentVersion::getDocumentId), documentId);
        wrapper.eq(MybatisPlusUtil.toColumns(DocumentVersion::getVersion), version);
        DocumentVersion targetVersion = documentVersionDao.selectOne(wrapper);
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
        super.createEntity(newVersion,StrUtil.EMPTY);
        outputObject.setBean(newVersion);
    }

    /**
     * 获取文档的最新版本号
     */
    private Integer getLatestVersion(String documentId) {
        QueryWrapper<DocumentVersion> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(DocumentVersion::getDocumentId), documentId)
            .orderByDesc(MybatisPlusUtil.toColumns(DocumentVersion::getVersion))
            .last("limit 1");
        DocumentVersion latestVersion = documentVersionDao.selectOne(wrapper);
        return latestVersion != null ? latestVersion.getVersion() : 0;
    }
} 