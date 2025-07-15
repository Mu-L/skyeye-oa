package com.skyeye.eve.multifllblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;
import com.skyeye.eve.multifllblank.entity.DwAnFillblank;

import java.util.List;
import java.util.Map;

public interface DwAnFillblankService extends SkyeyeBusinessService<DwAnFillblank> {
    void queryDwAnFillblankListById(InputObject inputObject, OutputObject outputObject);

    List<DwAnFillblank> selectBySurveyId(String surveyId);

    List<DwAnFillblank> selectAnFillblankQuId(String id);

    Map<String, List<DwAnFillblank>> selectByQuIdAndStuId(List<String> multifillblankIds, String studentId);
}
