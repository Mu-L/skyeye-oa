package com.skyeye.eve.multifllblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;

import java.util.List;
import java.util.Map;

public interface DwAnDfillblankService extends SkyeyeBusinessService<DwAnDfillblank> {
    void queryDwAnDfillblankById(InputObject inputObject, OutputObject outputObject);

    List<DwAnDfillblank> selectBySurveyId(String surveyId);

    List<DwAnDfillblank> selectAnDfillblankQuId(String id);

    Map<String, List<DwAnDfillblank>> selectByQuIdAndStuId(List<String> multifillblankIds, String studentId, String id);
}
