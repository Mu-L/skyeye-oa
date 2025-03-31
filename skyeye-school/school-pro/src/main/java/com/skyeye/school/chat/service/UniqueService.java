package com.skyeye.school.chat.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.Unique;

public interface UniqueService extends SkyeyeBusinessService<Unique> {
    void queryMyChatMessageList(InputObject inputObject, OutputObject outputObject);

    Unique quesyUniqueIsExist(String uniqueId);

    void deleteMyChatUniqueList(InputObject inputObject, OutputObject outputObject);
}
