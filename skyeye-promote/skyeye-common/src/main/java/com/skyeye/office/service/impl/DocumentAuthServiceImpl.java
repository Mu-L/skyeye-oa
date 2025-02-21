package com.skyeye.office.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.office.dao.DocumentAuthDao;
import com.skyeye.office.entity.DocumentAuth;
import com.skyeye.office.service.DocumentAuthService;
import com.sun.javafx.geom.Quat4f;
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
@SkyeyeService(name = "文档权限管理服务管理", groupName = "文档权限管理服务管理")
public class DocumentAuthServiceImpl extends SkyeyeBusinessServiceImpl<DocumentAuthDao,DocumentAuth> implements DocumentAuthService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantAuth(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        String userId = inputObject.getParams().get("userId").toString();
        String authType = inputObject.getParams().get("authType").toString();
        // 检查是否已存在权限记录
        QueryWrapper<DocumentAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentAuth::getDocumentId), documentId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentAuth::getUserId), userId);
        DocumentAuth existAuth = getOne(queryWrapper);

        String currentUserId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        if (ObjectUtil.isNotEmpty(existAuth)) {
            // 更新权限类型
            existAuth.setAuthType(authType);
            super.updateEntity(existAuth,currentUserId);
            outputObject.setBean(existAuth);
        } else {
            // 创建新的权限记录
            DocumentAuth auth = new DocumentAuth();
            auth.setDocumentId(documentId);
            auth.setUserId(userId);
            auth.setAuthType(authType);
            super.createEntity(auth, currentUserId);
            outputObject.setBean(auth);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeAuth(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        String userId = inputObject.getParams().get("userId").toString();
        QueryWrapper<DocumentAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentAuth::getDocumentId), documentId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentAuth::getUserId), userId);
        remove(queryWrapper);
    }

    @Override
    public void getAuthUsers(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        QueryWrapper<DocumentAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentAuth::getDocumentId), documentId);
        List<DocumentAuth> authList = list(queryWrapper);
        outputObject.setBeans(authList);
        outputObject.settotal(authList.size());
    }

    @Override
    public void checkAuth(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        String userId = inputObject.getParams().get("userId").toString();
        QueryWrapper<DocumentAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentAuth::getDocumentId), documentId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentAuth::getUserId), userId);
        DocumentAuth auth = getOne(queryWrapper);
        outputObject.setBean(auth);
    }
} 