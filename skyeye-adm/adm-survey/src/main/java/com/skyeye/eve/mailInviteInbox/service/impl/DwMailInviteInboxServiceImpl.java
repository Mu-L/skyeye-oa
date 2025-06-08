package com.skyeye.eve.mailInviteInbox.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.mailInviteInbox.dao.DwMailInviteInboxDao;
import com.skyeye.eve.mailInviteInbox.entity.DwMailInviteInbox;
import com.skyeye.eve.mailInviteInbox.service.DwMailInviteInboxService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "是非题结果保存表管理", groupName = "是非题结果保存表管理")
public class DwMailInviteInboxServiceImpl extends SkyeyeBusinessServiceImpl<DwMailInviteInboxDao, DwMailInviteInbox> implements DwMailInviteInboxService {
    @Override
    public void queryDwMailInviteInboxListById(InputObject inputObject, OutputObject outputObject) {
        String dwMailInviteInboxId = inputObject.getParams().get("id").toString();
        QueryWrapper<DwMailInviteInbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, dwMailInviteInboxId);
        List<DwMailInviteInbox> dwMailInviteInboxList = list(queryWrapper);
        outputObject.setBean(dwMailInviteInboxList);
        outputObject.settotal(dwMailInviteInboxList.size());
    }
}
