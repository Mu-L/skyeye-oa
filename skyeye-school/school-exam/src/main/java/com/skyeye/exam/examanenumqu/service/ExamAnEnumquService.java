package com.skyeye.exam.examanenumqu.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanenumqu.entity.ExamAnEnumqu;

import java.util.List;

/**
 * @ClassName: ExamAnEnumquService
 * @Description: 答卷 枚举题答案实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamAnEnumquService extends SkyeyeBusinessService<ExamAnEnumqu> {
    void queryExamAnEnumquListById(InputObject inputObject, OutputObject outputObject);

    List<ExamAnEnumqu> selectBySurveyId(String surveyId);

    void deleteBySurAndCreateId(String surveyId, String createId);
}
