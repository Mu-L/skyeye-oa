/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.job.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.NoticeUserMessageTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.rest.mq.JobMateUpdateMation;
import com.skyeye.eve.rest.notice.UserMessage;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.IUserNoticeService;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 设备维修单派工通知
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.equipment-repair-dispatch-service}",
    consumerGroup = "${topic.equipment-repair-dispatch-service}",
    selectorExpression = "${spring.profiles.active}")
public class EquipmentRepairDispatchNoticeServiceImpl implements RocketMQListener<String> {

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private IUserNoticeService iUserNoticeService;

    @Autowired
    private Executor watiWorkerSendEmailExecutor;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        try {
            if (tenantEnable) {
                TenantContext.setTenantId(map.get("tenantId").toString());
            }
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            String serviceId = map.get("serviceId").toString();
            log.info("开始设备维修派工通知，维修单id：{}，主题：{}，消息内容：{}", serviceId,
                MqConstants.JobMateMationJobType.EQUIPMENT_REPAIR_DISPATCH, data);
            EquipmentRepairOrder repairOrder = equipmentRepairOrderService.selectById(serviceId);
            if (ObjectUtil.isNotEmpty(repairOrder)) {
                String repairOrderStr = JSONUtil.toJsonStr(repairOrder);
                List<UserMessage> userMessageBoxList = new ArrayList<>();
                log.info("接收人是：{}", repairOrder.getServiceUserId());
                if (StrUtil.isNotEmpty(repairOrder.getServiceUserId())) {
                    Map<String, Object> userMation = iAuthUserService.queryDataMationById(repairOrder.getServiceUserId());
                    String content = NoticeUserMessageTypeEnum.getNoticeServiceUserContent(
                        repairOrder.getOddNumber(), userMation.get("name").toString());
                    UserMessage userMessage = getUserNotice(repairOrder.getServiceUserId(), content,
                        userMation.getOrDefault("email", StrUtil.EMPTY).toString(), repairOrderStr);
                    userMessageBoxList.add(userMessage);
                }
                log.info("通知列表：{}", JSONUtil.toJsonStr(userMessageBoxList));
                if (!userMessageBoxList.isEmpty()) {
                    iUserNoticeService.insertUserNoticeMation(userMessageBoxList);
                    watiWorkerSendEmailExecutor.execute(() -> {
                        for (UserMessage userMessage : userMessageBoxList) {
                            if (ToolUtil.isEmail(userMessage.getEmail()) && !ToolUtil.isBlank(userMessage.getEmail())) {
                                new MailUtil().send(userMessage.getEmail(),
                                    NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getValue(), userMessage.getContent());
                            }
                        }
                    });
                }
            }
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            log.warn("Equipment repair dispatch notice failed, reason is {}.", e);
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.EMPTY);
        }
    }

    private UserMessage getUserNotice(String userId, String content, String email, String objectData) {
        UserMessage userMessage = new UserMessage();
        userMessage.setName(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getValue());
        userMessage.setRemark(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getRemark());
        userMessage.setEmail(email);
        userMessage.setContent(content);
        userMessage.setReceiveId(userId);
        userMessage.setType(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getKey());
        userMessage.setCreateUserId(CommonConstants.ADMIN_USER_ID);
        userMessage.setObjectData(objectData);
        return userMessage;
    }

    private void updateJobMation(String jobId, String status, String responseBody) {
        JobMateUpdateMation jobMateUpdateMation = new JobMateUpdateMation();
        jobMateUpdateMation.setJobId(jobId);
        jobMateUpdateMation.setStatus(status);
        jobMateUpdateMation.setResponseBody(responseBody);
        iJobMateMationService.comMQJobMation(jobMateUpdateMation);
    }

}
