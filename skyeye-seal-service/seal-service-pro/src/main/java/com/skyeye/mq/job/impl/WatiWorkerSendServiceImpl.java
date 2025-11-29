/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Joiner;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.common.constans.CommonCharConstants;
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
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WatiWorkerSendServiceImpl
 * @Description: 派工通知
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RocketMQMessageListener(
        topic = "${topic.wati-worker-send-service}",
        consumerGroup = "${topic.wati-worker-send-service}",
        selectorExpression = "${spring.profiles.active}")
public class WatiWorkerSendServiceImpl implements RocketMQListener<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatiWorkerSendServiceImpl.class);

    @Autowired
    private AfterSealService afterSealService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private IUserNoticeService iUserNoticeService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        try {
            String tenantId;
            if (tenantEnable) {
                tenantId = map.get("tenantId").toString();
                TenantContext.setTenantId(tenantId);
            }
            // 任务开始
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            // 工单id
            String serviceId = map.get("serviceId").toString();
            // 获取工单接收人和协助人id
            AfterSeal afterSeal = afterSealService.selectById(serviceId);
            // 如果工单信息不为空
            if (ObjectUtil.isNotEmpty(afterSeal)) {
                // 调用消息系统添加通知
                List<UserMessage> userMessageBoxList = new ArrayList<>();
                String content;
                // 1.接收人通知
                if (StrUtil.isNotEmpty(afterSeal.getServiceUserId())) {
                    Map<String, Object> userMation = iAuthUserService.queryDataMationById(afterSeal.getServiceUserId());
                    // 1.1内部消息
                    content = NoticeUserMessageTypeEnum.getNoticeServiceUserContent(afterSeal.getOddNumber(), userMation.get("name").toString());

                    UserMessage userMessage = getUserNotice(afterSeal.getServiceUserId(), content);
                    userMessageBoxList.add(userMessage);

                    // 1.2发送邮件
                    String email = userMation.get("email").toString();
                    if (ToolUtil.isEmail(email) && !ToolUtil.isBlank(email)) {
                        new MailUtil().send(email, NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getValue(), content);
                    }
                }
                // 2.协助人通知
                if (CollectionUtil.isNotEmpty(afterSeal.getCooperationUserId())) {
                    // 获取协助人
                    List<Map<String, Object>> cooperationUser = iAuthUserService
                            .queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(afterSeal.getCooperationUserId()));

                    for (Map<String, Object> user : cooperationUser) {
                        // 2.1内部消息
                        content = NoticeUserMessageTypeEnum.getNoticeCooperationUserContent(afterSeal.getOddNumber(), user.get("name").toString());
                        UserMessage userMessage = getUserNotice(user.get("id").toString(), content);
                        userMessageBoxList.add(userMessage);
                        // 2.2发送邮件
                        String email = user.get("email").toString();
                        if (ToolUtil.isEmail(email) && !ToolUtil.isBlank(email)) {
                            new MailUtil().send(email, NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getValue(), content);
                        }
                    }
                }
                if (!userMessageBoxList.isEmpty()) {
                    iUserNoticeService.insertUserNoticeMation(userMessageBoxList);
                }
            }
            // 任务完成
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            LOGGER.warn("Dispatch notice failed, reason is {}.", e);
            // 任务失败
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.EMPTY);
        }
    }

    private UserMessage getUserNotice(String userId, String content) {
        UserMessage userMessage = new UserMessage();
        userMessage.setName(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getValue());
        userMessage.setRemark(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getRemark());
        userMessage.setContent(content);
        userMessage.setReceiveId(userId);
        userMessage.setType(NoticeUserMessageTypeEnum.WORK_ORDER_REMINDER.getKey());
        userMessage.setCreateUserId(CommonConstants.ADMIN_USER_ID);
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
