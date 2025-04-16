package com.skyeye.school.exam.service;

import java.util.List;
import java.util.Map;

public interface ExamDirectoryAnService {
    Long queryClassExamSurveyAnswerNum(String id, String stuId, String subjectId);

    Map<String, Long> queryClassExamSurveyAnswerNumByStuIds(String classesId, List<String> stuIds, String subjectId);

    Double queryClassExamSurveyAvgScore(String classesId, String stuId, String subjectId);

    List<Map<String, Object>> queryExamAnserByExamIds(List<String> examIdList);
}
