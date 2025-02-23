package com.skyeye.eve.question.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwSurveyAnswer;

import java.util.List;

public interface DwSurveyAnswerService extends SkyeyeBusinessService<DwSurveyAnswer> {
    void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject);

    DwSurveyAnswer queryWhetherExamIngByStuId(String userId, String id);

    List<DwSurveyAnswer> querySurveyAnswer(String surveyId, String answerId, String userId);

    List<DwSurveyAnswer> queryNoOrYesSurveyAnswerList(InputObject inputObject, OutputObject outputObject);

    void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject);

    void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject);

    void queryFilterToBeReviewedSurveys(InputObject inputObject, OutputObject outputObject);
}
