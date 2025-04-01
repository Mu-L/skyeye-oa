package com.skyeye.exam.examsurveydirectory.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;

import java.util.List;

/**
 * @ClassName: ExamSurveyDirectoryService
 * @Description: 试卷管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamSurveyDirectoryService extends SkyeyeBusinessService<ExamSurveyDirectory> {

    void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject);

    void setUpExamDirectory(InputObject inputObject, OutputObject outputObject);

    ExamSurveyDirectory takeExam(InputObject inputObject, OutputObject outputObject);

    void copyExamDirectory(InputObject inputObject, OutputObject outputObject);

    void updateExamMationEndById(InputObject inputObject, OutputObject outputObject);

    void queryMyExamList(InputObject inputObject, OutputObject outputObject);

    void queryFilterExamLists(InputObject inputObject, OutputObject outputObject);

    void queryAllExamList(InputObject inputObject, OutputObject outputObject);

    void queryMySurvey(InputObject inputObject, OutputObject outputObject);

    void createExamDirectory(InputObject inputObject, OutputObject outputObject);

    void querySurveyListBySubjectLinkId(InputObject inputObject, OutputObject outputObject);

    void querySurveyListByNoOrYesState(Integer state);

    List<ExamSurveyDirectory> querySurveyListByIds(List<String> surveyIds);

//    void queryExamFxById(InputObject inputObject, OutputObject outputObject);
}
