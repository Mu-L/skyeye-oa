package com.skyeye.exam.examsurveyanswer.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyAnswerService
 * @Description: 试卷回答信息表管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamSurveyAnswerService extends SkyeyeBusinessService<ExamSurveyAnswer> {
    void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject);

    ExamSurveyAnswer queryWhetherExamIngByStuId(String userId, String id);

    List<ExamSurveyAnswer> querySurveyAnswer(String surveyId, String answerId, String userId);

    void queryAllSurveyList(InputObject inputObject, OutputObject outputObject);

    void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject);

    void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject);

    Map<String, Integer> queryAnswerNum(List<String> directoryIds);

    void querySurveyBySurveyIdAndUserId(InputObject inputObject, OutputObject outputObject);

    Map<String, Integer> queryAlreadyAnswerNum(List<String> directoryIds);

    void IsTakeSurveyAnswer(InputObject inputObject, OutputObject outputObject);
}
