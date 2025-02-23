package com.skyeye.eve.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.score.entity.DwAnScore;

import java.util.List;

public interface DwAnScoreService extends SkyeyeBusinessService<DwAnScore> {
    List<DwAnScore> selectAnScoreByQuId(String id);

    List<DwAnScore> selectBySurveyId(String surveyId);

    void queryDwAnScoreListById(InputObject inputObject, OutputObject outputObject);
}
