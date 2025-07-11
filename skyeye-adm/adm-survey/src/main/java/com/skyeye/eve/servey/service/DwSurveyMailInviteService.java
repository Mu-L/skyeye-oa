package com.skyeye.eve.servey.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.servey.entity.DwSurveyMailInvite;

public interface DwSurveyMailInviteService extends SkyeyeBusinessService<DwSurveyMailInvite> {
    void queryDwSurveyMailInviteList(InputObject inputObject, OutputObject outputObject);

    void queryMyDwSurveyMailInviteList(InputObject inputObject, OutputObject outputObject);
}
