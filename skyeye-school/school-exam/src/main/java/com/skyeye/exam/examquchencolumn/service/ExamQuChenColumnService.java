package com.skyeye.exam.examquchencolumn.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;

import java.util.List;
import java.util.Map;

public interface ExamQuChenColumnService extends SkyeyeBusinessService<ExamQuChenColumn> {

    void saveList(List<ExamQuChenColumn> column, List<ExamQuChenRow> row, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<ExamQuChenColumn> selectQuChenColumn(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);

    Map<String, List<ExamQuChenColumn>> selectByQuestionIds(List<String> questionIdList);

    void createChenColumns(List<Question> questionList, String userId);

    void removeByQuIds(List<String> questionIds);

    void updateChenColumn(List<Question> questionList, String userId);
}
