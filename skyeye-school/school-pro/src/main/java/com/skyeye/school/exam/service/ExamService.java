package com.skyeye.school.exam.service;


import java.util.List;
import java.util.Map;

public interface ExamService  {
    Long queryClassExamSurveyDirectoryNum(String id);

    List<Map<String, Object>> queryListBySubjectId(String subjectId);
}
