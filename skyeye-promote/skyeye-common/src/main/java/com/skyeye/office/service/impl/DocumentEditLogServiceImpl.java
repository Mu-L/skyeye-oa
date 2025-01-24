package com.skyeye.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.office.entity.DocumentEditLog;
import com.skyeye.office.service.DocumentEditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName: DocumentEditLogServiceImpl
 * @Description: 文档编辑日志服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Service
public class DocumentEditLogServiceImpl extends SkyeyeBusinessServiceImpl<DocumentEditLog> implements DocumentEditLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEditLog(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        Integer version = inputObject.getParams().getInteger("version");
        String operationType = inputObject.getParams().getString("operationType");
        String operationContent = inputObject.getParams().getString("operationContent");

        // 创建编辑日志记录
        DocumentEditLog editLog = new DocumentEditLog();
        editLog.setDocumentId(documentId);
        editLog.setVersion(version);
        editLog.setOperationType(operationType);
        editLog.setOperationContent(operationContent);

        super.createEntity(editLog);
        outputObject.setBean(editLog);
    }

    @Override
    public void getEditLogs(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");

        // 查询编辑日志记录
        QueryWrapper<DocumentEditLog> wrapper = new QueryWrapper<>();
        wrapper.eq("document_id", documentId)
            .orderByDesc("create_time");

        List<DocumentEditLog> logList = super.selectList(wrapper);
        outputObject.setBean(logList);
    }
} 