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
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.rest.notice.UserMessage;
import com.skyeye.eve.schedule.classenum.ScheduleState;
import com.skyeye.eve.schedule.dao.ScheduleDayDao;
import com.skyeye.eve.schedule.entity.ScheduleDay;
import com.skyeye.eve.schedule.service.ScheduleDayService;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.eve.service.IUserNoticeService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AllScheduleMationService
 * @Description: 通知所有人日程提醒
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/7 15:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class AllScheduleMationService {

    @Autowired
    private ScheduleDayDao scheduleDayDao;

    @Autowired
    private ScheduleDayService scheduleDayService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private IUserNoticeService iUserNoticeService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @XxlJob("allScheduleMationService")
    public void call() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String scheduleId = paramMap.get("objectId");
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        ScheduleDay scheduleDay = scheduleDayService.selectById(scheduleId);
        List<Map<String, Object>> users = scheduleDayDao.queryAllUserAndEmailISNotNull(tenantId);
        List<Map<String, Object>> userJson = new ArrayList<>();
        for (Map<String, Object> user : users) {
            //发送消息
            String content = "尊敬的" + user.get("userName").toString() + ",您好：<br/>《" + scheduleDay.getName() + "》放假时间为：" + scheduleDay.getStartTime() + " ~ "
                + scheduleDay.getEndTime() + "<br/>请注意安排好您的工作时间，祝您出行愉快。";
            if (!StrUtil.isBlank(scheduleDay.getRemark())) {
                content += "<br>备注信息：" + scheduleDay.getRemark();
            }
            Map<String, Object> uJson = new HashMap<>();
            uJson.put("content", content);
            uJson.put("userId", user.get("userId"));
            userJson.add(uJson);
            //发送邮件
            if (!StrUtil.isBlank(user.get("email").toString()) && user.containsKey("email")) {
                Map<String, Object> emailNotice = new HashMap<>();
                emailNotice.put("title", "日程提醒");
                emailNotice.put("content", content);
                emailNotice.put("email", user.get("email").toString());
                emailNotice.put("type", MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType());
                JobMateMation jobMateMation = new JobMateMation();
                jobMateMation.setJsonStr(JSONUtil.toJsonStr(emailNotice));
                jobMateMation.setUserId(paramMap.get("userId"));
                iJobMateMationService.sendMQProducer(jobMateMation);
            }
        }
        if (!userJson.isEmpty()) {
            insertUserNotice(userJson);
        }
        // 修改日程状态
        scheduleDayService.editScheduleStateById(scheduleId, ScheduleState.REMINDED_SCHEDULE.getKey());
        iQuartzService.stopAndDeleteTaskQuartz(paramMap.get("objectId"));
    }

    private void insertUserNotice(List<Map<String, Object>> userJson) {
        // 调用消息系统添加通知
        List<UserMessage> userMessageList = new ArrayList<>();
        for (int i = 0; i < userJson.size(); i++) {
            Map<String, Object> userJsonObject = userJson.get(i);
            UserMessage userMessage = new UserMessage();
            userMessage.setName("日程提醒");
            userMessage.setRemark("您有一条新的日程信息，请及时阅读。");
            userMessage.setContent(userJsonObject.get("content").toString());
            userMessage.setReceiveId(userJsonObject.get("userId").toString());
            userMessage.setType(NoticeUserMessageTypeEnum.SCHEDULE_MESSAGE.getKey());
            userMessage.setCreateUserId(CommonConstants.ADMIN_USER_ID);
            userMessageList.add(userMessage);
        }
        if (!userMessageList.isEmpty()) {
            iUserNoticeService.insertUserNoticeMation(userMessageList);
        }
    }

}
