package com.skyeye.eve.answer.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.answer.entity.DwAnAnswer;

import java.util.List;

public interface DwAnAnswerService extends SkyeyeBusinessService<DwAnAnswer> {
    List<DwAnAnswer> selectBySurveyId(String surveyId);

    List<DwAnAnswer> selectAnAnswerByQuId(String id);
}
