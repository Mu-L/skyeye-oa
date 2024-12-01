package com.skyeye.exam.examSurveyDirectory.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examSurveyDirectory.entity.ExamSurveyDirectory;

/**
 * @ClassName: ExamSurveyDirectoryService
 * @Description: 试卷管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamSurveyDirectoryService extends SkyeyeBusinessService<ExamSurveyDirectory> {

    void queryAllOrMyExamList(InputObject inputObject, OutputObject outputObject);

    void deleteDirectoryById(InputObject inputObject, OutputObject outputObject);
}
