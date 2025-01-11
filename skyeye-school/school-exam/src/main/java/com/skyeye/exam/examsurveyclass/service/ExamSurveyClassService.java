package com.skyeye.exam.examsurveyclass.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.exam.examsurveyclass.entity.ExamSurveyClass;

/**
 * @ClassName: ExamSurveyClassService
 * @Description: 试卷与班级关系表管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamSurveyClassService extends SkyeyeBusinessService<ExamSurveyClass> {

    void createExamSurveyClass(String id,String classId, String userId);

    void deleteSurveyClassBySurveyId(String id);
}
