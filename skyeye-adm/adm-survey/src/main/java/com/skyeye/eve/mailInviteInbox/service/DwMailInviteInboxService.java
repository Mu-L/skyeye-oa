package com.skyeye.eve.mailInviteInbox.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.mailInviteInbox.entity.DwMailInviteInbox;

public interface DwMailInviteInboxService extends SkyeyeBusinessService<DwMailInviteInbox> {
    void queryDwMailInviteInboxListById(InputObject inputObject, OutputObject outputObject);
}
