package com.skyeye.eve.servey.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.servey.entity.DwSurveyAnswer;

import java.util.List;

public interface DwSurveyAnswerService extends SkyeyeBusinessService<DwSurveyAnswer> {
    void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject);

    List<DwSurveyAnswer> queryWhetherExamIngByStuId(String userId, String id);

    List<DwSurveyAnswer> querySurveyAnswer(String surveyId, String answerId, String userId);

    void queryNoOrYesSurveyAnswerList(InputObject inputObject, OutputObject outputObject);

    void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject);

    void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject);

    void queryFilterToBeReviewedSurveys(InputObject inputObject, OutputObject outputObject);

    List<DwSurveyAnswer> querySurveyAnswerByBelongId(String dwDirectoryId);

    Integer selectFractionBySurveyId(String surveyId);

    void querySurveyAnswerByDirectoryIdAndUserId(InputObject inputObject, OutputObject outputObject);

    DwSurveyAnswer querySurveyAnswerByRuleCode(String machineCode, String id);

    DwSurveyAnswer querySurveyAnswerByIp(String ip, String id);

    List<DwSurveyAnswer> querySurveyAnswerNumById(String id);

}
