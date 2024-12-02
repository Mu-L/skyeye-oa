package com.skyeye.exam.examqumultfillblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examqumultfillblank.entity.ExamQuMultiFillblank;

import java.util.List;

public interface ExamQuMultiFillblankService extends SkyeyeBusinessService<ExamQuMultiFillblank> {

    void saveList(List<ExamQuMultiFillblank> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);
}
