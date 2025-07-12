/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.rest.mq.JobMateUpdateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.history.entity.AutoHistoryCase;
import com.skyeye.usercase.entity.AutoCase;
import com.skyeye.usercase.service.AutoCaseService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: ExecuteCaseServiceImpl
 * @Description: 执行用例的异步任务
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/18 20:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RocketMQMessageListener(
        topic = "${topic.usercase-execute-service}",
        consumerGroup = "${topic.usercase-execute-service}",
        selectorExpression = "${spring.profiles.active}")
public class ExecuteCaseServiceImpl implements RocketMQListener<String> {

    @Autowired
    private AutoCaseService autoCaseService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        try {
            String tenantId = StrUtil.EMPTY;
            if (tenantEnable) {
                tenantId = map.get("tenantId").toString();
                TenantContext.setTenantId(tenantId);
            }
            // 任务开始
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            AutoHistoryCase autoHistoryCase = JSONUtil.toBean(map.get("autoHistoryCaseStr").toString(), AutoHistoryCase.class);
            AutoCase autoCase = JSONUtil.toBean(map.get("autoCaseStr").toString(), AutoCase.class);
            autoCaseService.updateHistoryCase(autoCase, true, autoHistoryCase);
            // 任务完成
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            // 任务失败
            updateJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.EMPTY);
        }
    }

    private void updateJobMation(String jobId, String status, String responseBody) {
        JobMateUpdateMation jobMateUpdateMation = new JobMateUpdateMation();
        jobMateUpdateMation.setJobId(jobId);
        jobMateUpdateMation.setStatus(status);
        jobMateUpdateMation.setResponseBody(responseBody);
        iJobMateMationService.comMQJobMation(jobMateUpdateMation);
    }

}
