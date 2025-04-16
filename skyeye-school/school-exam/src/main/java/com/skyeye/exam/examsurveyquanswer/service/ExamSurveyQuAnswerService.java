package com.skyeye.exam.examsurveyquanswer.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examsurveyquanswer.entity.ExamSurveyQuAnswer;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyQuAnswerService
 * @Description: 答卷 题目和所得分数的关联表管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamSurveyQuAnswerService extends SkyeyeBusinessService<ExamSurveyQuAnswer> {
    void queryExamSurveyQuAnswerListById(InputObject inputObject, OutputObject outputObject);

    float selectFractionBySurveyId(String surveyId, String id);

    Map<String, List<ExamSurveyQuAnswer>> selectFacByIdAndSurveyId(String id, String surveyId);
}
