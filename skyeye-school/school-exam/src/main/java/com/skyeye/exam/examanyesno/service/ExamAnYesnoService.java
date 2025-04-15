package com.skyeye.exam.examanyesno.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanyesno.entity.ExamAnYesno;

import java.util.List;

public interface ExamAnYesnoService extends SkyeyeBusinessService<ExamAnYesno> {

    void queryExamAnYesnoListById(InputObject inputObject, OutputObject outputObject);

    List<ExamAnYesno> selectBySurveyId(String surveyId, String id);

    void deleteBySurAndCreateId(String surveyId, String createId);
}
