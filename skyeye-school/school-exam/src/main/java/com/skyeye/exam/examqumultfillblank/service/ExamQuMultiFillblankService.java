package com.skyeye.exam.examqumultfillblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examqumultfillblank.entity.ExamQuMultiFillblank;

import java.util.List;
import java.util.Map;

public interface ExamQuMultiFillblankService extends SkyeyeBusinessService<ExamQuMultiFillblank> {

    void saveList(List<ExamQuMultiFillblank> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<ExamQuMultiFillblank> selectQuMultiFillblank(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);

    Map<String, List<ExamQuMultiFillblank>> selectByQuestionIds(List<String> questionIdList);
}
