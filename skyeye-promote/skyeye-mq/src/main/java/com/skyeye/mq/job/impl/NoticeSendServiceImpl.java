/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.MailUtil;
import com.skyeye.service.JobMateMationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: NoticeSendServiceImpl
 * @Description: 公告通知
 * @author: skyeye云系列--卫志强
 * @date: 2025/11/29 11:18
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "${topic.notice-send-service}",
    consumerGroup = "${topic.notice-send-service}",
    selectorExpression = "${spring.profiles.active}")
public class NoticeSendServiceImpl implements RocketMQListener<String> {

    @Autowired
    private JobMateMationService jobMateMationService;

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
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, "");
            String emailUserListStr = map.get("email").toString();
            List<Map<String, Object>> beans = JSONUtil.toList(emailUserListStr, null);
            for (int i = 0; i < beans.size(); i++) {
                Map<String, Object> bean = beans.get(i);
                if (CollectionUtil.isEmpty(bean)) {
                    continue;
                }
                String email = bean.getOrDefault("email", StrUtil.EMPTY).toString();
                if (StrUtil.isEmpty(email)) {
                    continue;
                }
                // 邮件账号不为空，发送邮件
                new MailUtil().send(email, map.get("title").toString(), map.get("content").toString());
            }
            // 任务完成
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, "");
        } catch (Exception e) {
            log.warn("notification failed, reason is {}.", e);
            // 任务失败
            jobMateMationService.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, "");
        }
    }

}
