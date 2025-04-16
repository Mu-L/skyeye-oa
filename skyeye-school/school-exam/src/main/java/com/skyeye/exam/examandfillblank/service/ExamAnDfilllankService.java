package com.skyeye.exam.examandfillblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examandfillblank.entity.ExamAnDfillblank;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamAnDfilllankService
 * @Description: 答卷 多行填空题保存表服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamAnDfilllankService extends SkyeyeBusinessService<ExamAnDfillblank> {
    void queryExamAnDfilllankById(InputObject inputObject, OutputObject outputObject);

    long selectBySurveyId(String surveyId, String id);

    List<ExamAnDfillblank> selectAnMultiFillblankQuId(String id);

    void deleteBySurAndCreateId(String surveyId, String createId);

    Map<String, List<ExamAnDfillblank>> selectByQuIdAndStuId(List<String> id, String studentId);
}
