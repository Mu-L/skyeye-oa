package com.skyeye.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.office.entity.DocumentAuth;
import com.skyeye.office.service.DocumentAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName: DocumentAuthServiceImpl
 * @Description: 文档权限管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Service
public class DocumentAuthServiceImpl extends SkyeyeBusinessServiceImpl<DocumentAuth> implements DocumentAuthService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantAuth(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String userId = inputObject.getParams().getString("userId");
        String authType = inputObject.getParams().getString("authType");

        // 检查是否已存在权限记录
        DocumentAuth existAuth = super.selectOne(ToolUtil.getWrapper(DocumentAuth.class)
            .eq("document_id", documentId)
            .eq("user_id", userId));

        if (existAuth != null) {
            // 更新权限类型
            existAuth.setAuthType(authType);
            super.updateById(existAuth);
            outputObject.setBean(existAuth);
        } else {
            // 创建新的权限记录
            DocumentAuth auth = new DocumentAuth();
            auth.setDocumentId(documentId);
            auth.setUserId(userId);
            auth.setAuthType(authType);
            super.createEntity(auth);
            outputObject.setBean(auth);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeAuth(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String userId = inputObject.getParams().getString("userId");

        super.deleteByWrapper(ToolUtil.getWrapper(DocumentAuth.class)
            .eq("document_id", documentId)
            .eq("user_id", userId));
    }

    @Override
    public void getAuthUsers(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");

        List<DocumentAuth> authList = super.selectList(ToolUtil.getWrapper(DocumentAuth.class)
            .eq("document_id", documentId));

        outputObject.setBean(authList);
    }

    @Override
    public void checkAuth(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String userId = inputObject.getParams().getString("userId");

        DocumentAuth auth = super.selectOne(ToolUtil.getWrapper(DocumentAuth.class)
            .eq("document_id", documentId)
            .eq("user_id", userId));

        outputObject.setBean(auth);
    }
} 