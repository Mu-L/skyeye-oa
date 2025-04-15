package com.skyeye.exam.examanscore.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanscore.entity.ExamAnScore;

import java.util.List;
import java.util.Map;

public interface ExamAnScoreService extends SkyeyeBusinessService<ExamAnScore> {

    void queryExamAnScoreListById(InputObject inputObject, OutputObject outputObject);

    List<ExamAnScore> selectBySurveyId(String surveyId, String id);

    List<ExamAnScore> selectAnScoreByQuId(String id);

    void deleteBySurAndCreateId(String surveyId, String createId);

    Map<String, List<ExamAnScore>> selectByQuIdAndStuId(List<String> id, String studentId);
}
