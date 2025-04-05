package com.skyeye.exam.examanradio.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanradio.entity.ExamAnRadio;

import java.util.List;
import java.util.Map;

public interface ExamAnRadioService extends SkyeyeBusinessService<ExamAnRadio> {

    void queryExamAnRadioListById(InputObject inputObject, OutputObject outputObject);

     List<ExamAnRadio> selectRadioBySurveyId(String surveyId);

    List<ExamAnRadio> selectByQuid(String id);

    void deleteBySurAndCreateId(String surveyId, String createId);

    Map<String, List<ExamAnRadio>> selectByQuIdAndStuId(List<String> id, String studentId);
}
