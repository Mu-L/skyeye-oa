package com.skyeye.school.exam.service;


import java.util.List;
import java.util.Map;

public interface ExamService  {
    Long queryClassExamSurveyDirectoryNum(String id, String subjectId);

    List<Map<String, Object>> queryListBySubjectIdAndState(String subjectId, Integer state);
}
