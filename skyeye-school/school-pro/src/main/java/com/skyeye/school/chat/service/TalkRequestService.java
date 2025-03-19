package com.skyeye.school.chat.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.TalkRequest;

public interface TalkRequestService extends SkyeyeBusinessService<TalkRequest> {
    void queryTalkRequestByRecipient(InputObject inputObject, OutputObject outputObject);

    void changeFriendStatus(InputObject inputObject, OutputObject outputObject);

    void queryTalkRequestFriend(InputObject inputObject, OutputObject outputObject);

    void queryTalkRequestByApplicant(InputObject inputObject, OutputObject outputObject);
}
