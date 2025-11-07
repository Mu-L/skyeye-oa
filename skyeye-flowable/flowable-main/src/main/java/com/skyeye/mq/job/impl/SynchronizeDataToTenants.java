/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.activiti.service.ActivitiModelService;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.entity.ActFlowMation;
import com.skyeye.eve.service.ActFlowService;
import com.skyeye.exception.CustomException;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SynchronizeDataToTenants
 * @Description: 同步工作流数据到指定租户  消息监听器配置为广播模式，所有消费者实例都会消费同一条消息
 * @author: skyeye云系列--卫志强
 * @date: 2025/11/6 11:55
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.synchronize-data-to-tenants}",
    consumerGroup = "${topic.synchronize-data-to-tenants}",
    selectorExpression = "${spring.profiles.active}",
    messageModel = MessageModel.BROADCASTING)
public class SynchronizeDataToTenants implements RocketMQListener<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeDataToTenants.class);

    @Autowired
    private ActivitiModelService activitiModelService;

    @Autowired
    private ActFlowService actFlowService;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        Map<String, Object> tenantMap = JSONUtil.toBean(map.get("content").toString(), null);
        String tenantId = tenantMap.get("id").toString();
        String userId = map.get("userId").toString();
        // 平台租户拥有的工作流
        List<ActFlowMation> platformTenantFlows = actFlowService.queryActFlowMationByTenantId(TenantTypeEnum.PLATFORM.getCode());
        // 当前租户拥有的工作流
        List<ActFlowMation> currentTenantFlows = actFlowService.queryActFlowMationByTenantId(tenantId);
        List<String> currentTenantFlowModelKeys = currentTenantFlows.stream().map(ActFlowMation::getModelKey).collect(Collectors.toList());

        // 设置租户上下文
        TenantContext.setTenantId(tenantId);
        // 从模板文件读取BPMN XML模板
        String bpmnXml = getBpmnXmlTemplate();
        platformTenantFlows.forEach(platformTenantFlow -> {
            String modelKey = platformTenantFlow.getModelKey();
            if (currentTenantFlowModelKeys.contains(modelKey)) {
                // 跳过当前租户已有的工作流
                return;
            }
            // 替换模板中的processId和processName
            String newBpmnXml = bpmnXml.replace("{processId}", modelKey);
            newBpmnXml = newBpmnXml.replace("{processName}", platformTenantFlow.getFlowName());

            // 为指定租户创建工作流模型
            String newModelId = activitiModelService.createModelFromBpmnXml(
                newBpmnXml,
                platformTenantFlow.getFlowName(),
                modelKey,
                tenantId
            );
            platformTenantFlow.setModelId(newModelId);
            platformTenantFlow.setTenantId(null);

            LOGGER.info("成功为租户 {} 创建工作流，模型ID: {}, 工作流名称: {}, 模型Key: {}",
                tenantId, newModelId, platformTenantFlow.getFlowName(), modelKey);
        });

        List<ActFlowMation> newModelTenantFlows = platformTenantFlows.stream()
            .filter(platformTenantFlow -> !currentTenantFlowModelKeys.contains(platformTenantFlow.getModelKey()))
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newModelTenantFlows)) {
            // 保存工作流信息
            actFlowService.createEntity(newModelTenantFlows, userId);
        }
    }

    /**
     * 从模板文件读取BPMN XML模板
     *
     * @return BPMN XML模板字符串
     */
    private String getBpmnXmlTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource("templates/workflow-template.xml");
            InputStream inputStream = resource.getInputStream();
            String template = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            IoUtil.close(inputStream);
            return template;
        } catch (Exception e) {
            LOGGER.error("读取BPMN XML模板失败", e);
            throw new CustomException("读取BPMN XML模板失败: " + e.getMessage());
        }
    }

}
