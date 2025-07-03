package com.skyeye.exam.examquchckbox.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examquestion.entity.Question;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;

import java.util.List;
import java.util.Map;

public interface ExamQuCheckboxService extends SkyeyeBusinessService<ExamQuCheckbox> {
    void saveList(List<ExamQuCheckbox> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<ExamQuCheckbox> selectQuChenbox(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);

    Map<String, List<ExamQuCheckbox>> selectByQuestionIds(List<String> questionIdList);

    void createCheckboxs(List<Question> questionList, String userId);

    void removeByQuIds(List<String> questionIds);

    void updateCheckboxs(List<Question> questionList, String userId);

//    void queryExamQuCheckboxListById(InputObject inputObject, OutputObject outputObject);
}
