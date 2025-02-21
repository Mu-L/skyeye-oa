package com.skyeye.office.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.office.dao.DocumentEditLogDao;
import com.skyeye.office.entity.DocumentEditLog;
import com.skyeye.office.service.DocumentEditLogService;
import com.skyeye.office.websocket.MessageType;
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
public class DocumentEditLogServiceImpl extends SkyeyeBusinessServiceImpl<DocumentEditLogDao,DocumentEditLog> implements DocumentEditLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEditLog(InputObject inputObject, OutputObject outputObject) {
        String  userId = inputObject.getLogParams().get("id").toString();
        String documentId = inputObject.getParams().get("documentId").toString();
        Integer version = Integer.parseInt(inputObject.getParams().get("version").toString());
        String operationType = inputObject.getParams().get("operationType").toString();
        String operationContent = inputObject.getParams().get("operationContent").toString();

        // 创建编辑日志记录
        DocumentEditLog editLog = new DocumentEditLog();
        editLog.setDocumentId(documentId);
        editLog.setVersion(version);
        editLog.setOperationType(operationType);
        editLog.setOperationContent(operationContent);

        super.createEntity(editLog,userId);
        outputObject.setBean(editLog);
    }

    @Override
    public void getEditLogs(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();

        // 查询编辑日志记录
        QueryWrapper<DocumentEditLog> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(DocumentEditLog::getDocumentId), documentId)
               .orderByDesc(MybatisPlusUtil.toColumns(DocumentEditLog::getCreateTime));
        List<DocumentEditLog> logList = list(wrapper);

        outputObject.setBean(logList);
    }

    @Override
    @Transactional
    public void addEditLog(String documentId, String userId, JSONObject editData) {
        // 获取编辑日志记录
        DocumentEditLog log = new DocumentEditLog();
        QueryWrapper<DocumentEditLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentEditLog::getDocumentId), documentId)
                .orderByDesc(MybatisPlusUtil.toColumns(DocumentEditLog::getVersion));
        List<DocumentEditLog> list = list(queryWrapper);
        if(CollectionUtil.isNotEmpty(list)){
            // 如果没有编辑日志记录，则创建第一个版本
            log.setVersion(list.get(CommonNumConstants.NUM_ONE).getVersion() + CommonNumConstants.NUM_ONE);
        }else {
            log.setVersion(CommonNumConstants.NUM_ONE);
        }
        log.setDocumentId(documentId);
        log.setOperationType(MessageType.EDIT.getType());
        log.setOperationContent(editData.toString());

        createEntity(log, userId);
    }
} 