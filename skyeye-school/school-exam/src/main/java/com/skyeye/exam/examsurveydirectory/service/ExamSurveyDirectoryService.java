/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.exam.examsurveydirectory.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyDirectoryService
 * @Description: 试卷管理服务接口层
 * @author: skyeye云系列--lyj
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

    void querySurveyListBySubjectLinkId(InputObject inputObject, OutputObject outputObject);

    Map<String, List<ExamSurveyDirectory>> querySurveyListByIds(List<String> surveyIds, String createId);

    ExamSurveyDirectory selectBySurAndStuId(String surveyId, String studentId);

    ExamSurveyDirectory selectBySurAndStuIds(String surveyId, String studentId, String id);

    Map<String, ExamSurveyDirectory> selectMapBysurveyIds(List<String> surveyIds);

    List<String> queryDirectoryIdsByClassId(String objectIds, String subjectId);

    void queryMyDoSurvey(InputObject inputObject, OutputObject outputObject);

    List<ExamSurveyDirectory> queryCreatedSurveyListByUserId(String userId);

    void queryFilterNoSurveys(InputObject inputObject, OutputObject outputObject);

    void createNotSubStudent(String id, String userId);

    void updateSurveyAnswerStatus(String id);

    /**
     * 自动组卷
     * @param inputObject 输入对象，包含试卷ID和组卷规则列表
     * @param outputObject 输出对象
     */
    void autoGeneratePaper(InputObject inputObject, OutputObject outputObject);
}
