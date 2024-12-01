package com.skyeye.exam.exammailinviteinbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.exammailinviteinbox.dao.ExamMailInviteInboxDao;
import com.skyeye.exam.exammailinviteinbox.entity.ExamMailInviteInbox;
import com.skyeye.exam.exammailinviteinbox.service.ExamMailInviteInboxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "是非题结果保存表管理", groupName = "是非题结果保存表管理")
public class ExamMailInviteInboxServiceImpl extends SkyeyeBusinessServiceImpl<ExamMailInviteInboxDao, ExamMailInviteInbox> implements ExamMailInviteInboxService {

    @Override
    public void queryExamMailInviteInboxListById(InputObject inputObject, OutputObject outputObject) {
        String examMailInviteInboxId = inputObject.getParams().get("id").toString();
        QueryWrapper<ExamMailInviteInbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examMailInviteInboxId);
        List<ExamMailInviteInbox> examMailInviteInboxList = list(queryWrapper);
        outputObject.setBean(examMailInviteInboxList);
        outputObject.settotal(examMailInviteInboxList.size());
    }
}
