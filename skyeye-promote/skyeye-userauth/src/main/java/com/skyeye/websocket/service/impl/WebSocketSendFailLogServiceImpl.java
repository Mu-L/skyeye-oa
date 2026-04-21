/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.websocket.dao.WebSocketSendFailLogDao;
import com.skyeye.websocket.entity.WebSocketSendFailLog;
import com.skyeye.websocket.service.WebSocketSendFailLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * WebSocket发送失败日志服务
 */
@Slf4j
@Service
@SkyeyeService(name = "WebSocket发送失败日志", groupName = "系统监控模块", tenant = TenantEnum.NO_ISOLATION)
public class WebSocketSendFailLogServiceImpl extends SkyeyeBusinessServiceImpl<WebSocketSendFailLogDao, WebSocketSendFailLog> implements WebSocketSendFailLogService {

    @Override
    protected QueryWrapper<WebSocketSendFailLog> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<WebSocketSendFailLog> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(WebSocketSendFailLog::getCreateTime));
        return queryWrapper;
    }

    @Override
    public void saveSendFailLog(String targetUserId, String sessionId, String sendStage, String messageContent, String errorMessage, String nodeId, String tenantId) {
        WebSocketSendFailLog entity = new WebSocketSendFailLog();
        entity.setId(ToolUtil.getSurFaceId());
        entity.setTargetUserId(targetUserId);
        entity.setTenantId(ToolUtil.isBlank(tenantId) ? null : tenantId);
        entity.setSessionId(sessionId);
        entity.setSendStage(sendStage);
        entity.setMessageContent(messageContent);
        entity.setErrorMessage(errorMessage);
        entity.setNodeId(nodeId);
        entity.setCreateTime(DateUtil.getTimeAndToString());
        createEntity(entity, StrUtil.EMPTY);
    }
}

