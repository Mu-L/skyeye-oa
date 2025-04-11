package com.skyeye.school.exam.service;

import java.util.List;
import java.util.Map;

public interface ExamDirectoryAnService {
    Long queryClassExamSurveyAnswerNum(String id);

    Map<String, Long> queryClassExamSurveyAnswerNumByStuIds(String classesId, List<String> stuIds);
}
