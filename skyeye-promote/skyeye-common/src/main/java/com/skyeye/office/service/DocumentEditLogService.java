package com.skyeye.office.service;

import com.alibaba.fastjson.JSONObject;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.entity.DocumentEditLog;

/**
 * @ClassName: DocumentEditLogService
 * @Description: 文档编辑日志服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
public interface DocumentEditLogService extends SkyeyeBusinessService<DocumentEditLog> {

    void addEditLog(InputObject inputObject, OutputObject outputObject);

    void getEditLogs(InputObject inputObject, OutputObject outputObject);

    void addEditLog(String documentId, String userId, JSONObject editData);
} 