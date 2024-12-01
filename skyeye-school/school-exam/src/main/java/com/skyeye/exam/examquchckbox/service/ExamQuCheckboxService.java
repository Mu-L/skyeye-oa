package com.skyeye.exam.examquchckbox.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;

import java.util.List;

public interface ExamQuCheckboxService extends SkyeyeBusinessService<ExamQuCheckbox> {
    void saveList(List<ExamQuCheckbox> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

//    void queryExamQuCheckboxListById(InputObject inputObject, OutputObject outputObject);
}
