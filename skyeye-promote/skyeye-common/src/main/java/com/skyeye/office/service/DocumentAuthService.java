package com.skyeye.office.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.entity.DocumentAuth;

/**
 * @ClassName: DocumentAuthService
 * @Description: 文档权限管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
public interface DocumentAuthService extends SkyeyeBusinessService<DocumentAuth> {

    void grantAuth(InputObject inputObject, OutputObject outputObject);

    void revokeAuth(InputObject inputObject, OutputObject outputObject);

    void getAuthUsers(InputObject inputObject, OutputObject outputObject);

    void checkAuth(InputObject inputObject, OutputObject outputObject);
} 