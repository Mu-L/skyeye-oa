package com.skyeye.exam.examanradio.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanradio.entity.ExamAnRadio;
import com.skyeye.exam.examanscore.entity.ExamAnScore;

import java.util.List;
import java.util.Map;

public interface ExamAnRadioService extends SkyeyeBusinessService<ExamAnRadio> {

    void queryExamAnRadioListById(InputObject inputObject, OutputObject outputObject);

     List<ExamAnRadio> selectRadioBySurveyId(String surveyId);
}
