package com.skyeye.eve.multifllblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;

import java.util.List;

public interface DwAnDfillblankService extends SkyeyeBusinessService<DwAnDfillblank> {
    void queryDwAnDfillblankById(InputObject inputObject, OutputObject outputObject);

    List<DwAnDfillblank> selectBySurveyId(String surveyId);
}
