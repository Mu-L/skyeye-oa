package com.skyeye.office.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.entity.DocumentVersion;

/**
 * @ClassName: DocumentVersionService
 * @Description: 文档版本管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
public interface DocumentVersionService extends SkyeyeBusinessService<DocumentVersion> {

    void createVersion(InputObject inputObject, OutputObject outputObject);

    void getVersionHistory(InputObject inputObject, OutputObject outputObject);

    void rollbackVersion(InputObject inputObject, OutputObject outputObject);
} 