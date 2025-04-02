package com.skyeye.exam.examquorderby.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.exam.examquorderby.entity.ExamQuOrderby;

import java.util.List;
import java.util.Map;

public interface ExamQuOrderbyService extends SkyeyeBusinessService<ExamQuOrderby> {

    void saveList(List<ExamQuOrderby> score, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<ExamQuOrderby> selectQuOrderby(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);

    Map<String, List<ExamQuOrderby>> selectByQuestionIds(List<String> questionIdList);

    void createOrderbys(List<Question> questionList, String userId);

    void removeByQuIds(List<String> questionIds);

    void updateOrderbys(List<Question> questionList, String userId);
}
