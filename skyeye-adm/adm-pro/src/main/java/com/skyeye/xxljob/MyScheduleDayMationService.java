/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.NoticeUserMessageTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.rest.notice.UserMessage;
import com.skyeye.eve.schedule.classenum.ScheduleState;
import com.skyeye.eve.schedule.entity.ScheduleDay;
import com.skyeye.eve.schedule.service.ScheduleDayService;
import com.skyeye.eve.service.*;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MyScheduleDayMationService
 * @Description: 我的日程提醒
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/7 15:23
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class MyScheduleDayMationService {

    @Autowired
    private ScheduleDayService scheduleDayService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private IUserNoticeService iUserNoticeService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @XxlJob("myScheduleDayMationService")
    public void call() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String userId = paramMap.get("userId");
        String scheduleId = paramMap.get("objectId");
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        Map<String, Object> userMation = iAuthUserService.queryDataMationById(userId);
        // 获取日程信息
        ScheduleDay scheduleDay = scheduleDayService.selectById(scheduleId);
        //发送消息
        String content = "尊敬的" + userMation.get("userName").toString() + ",您好：<br/>您于" + scheduleDay.getCreateTime() + "设定的日程《" +
            scheduleDay.getName() + "》即将于" + scheduleDay.getStartTime() + "开始，请做好准备哦。";
        if (!ToolUtil.isBlank(scheduleDay.getRemark())) {
            content += "<br>备注信息：" + scheduleDay.getRemark();
        }

        // 调用消息系统添加通知
        insertUserNotice(userId, content);

        // 发送邮件
        if (!MapUtil.checkKeyIsNull(userMation, "email")) {
            Map<String, Object> emailNotice = new HashMap<>();
            emailNotice.put("title", "日程提醒");
            emailNotice.put("content", content);
            emailNotice.put("email", userMation.get("email").toString());
            emailNotice.put("type", MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType());
            JobMateMation jobMateMation = new JobMateMation();
            jobMateMation.setJsonStr(JSONUtil.toJsonStr(emailNotice));
            jobMateMation.setUserId(userId);
            iJobMateMationService.sendMQProducer(jobMateMation);
        }

        // 修改日程状态
        scheduleDayService.editScheduleStateById(scheduleId, ScheduleState.REMINDED_SCHEDULE.getKey());
        iQuartzService.stopAndDeleteTaskQuartz(scheduleId);
    }

    private void insertUserNotice(String userId, String content) {
        UserMessage userMessage = new UserMessage();
        userMessage.setName("日程提醒");
        userMessage.setRemark("您有一条新的日程信息，请及时阅读。");
        userMessage.setContent(content);
        userMessage.setReceiveId(userId);
        userMessage.setType(NoticeUserMessageTypeEnum.SCHEDULE_MESSAGE.getKey());
        userMessage.setCreateUserId(CommonConstants.ADMIN_USER_ID);
        iUserNoticeService.insertUserNoticeMation(Arrays.asList(userMessage));
    }

}
