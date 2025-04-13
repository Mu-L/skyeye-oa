package com.skyeye.school.exam.service;

import java.util.List;
import java.util.Map;

public interface ExamDirectoryAnService {
    Long queryClassExamSurveyAnswerNum(String id, String stuId);

    Map<String, Long> queryClassExamSurveyAnswerNumByStuIds(String classesId, List<String> stuIds);

    Double queryClassExamSurveyAvgScore(String classesId, String stuId);

    List<Map<String, Object>> queryExamAnserByExamIds(List<String> examIdList);
}
