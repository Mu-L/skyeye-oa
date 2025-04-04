package com.skyeye.exam.examanchenscore.service;


import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanchenscore.entity.ExamAnChenScore;

import java.util.List;

/**
 * @ClassName: ExamAnChenScoreService
 * @Description: 答卷 矩阵多选题服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamAnChenScoreService extends SkyeyeBusinessService<ExamAnChenScore> {
    void queryExamAnChenScoreListById(InputObject inputObject, OutputObject outputObject);

    List<ExamAnChenScore> selectBySurveyId(String surveyId);

    List<ExamAnChenScore> selectByQuId(String id);

    void deleteBySurAndCreateId(String surveyId, String createId);

    List<ExamAnChenScore> selectByQuIdAndStuId(String questionId, String studentId);
}
