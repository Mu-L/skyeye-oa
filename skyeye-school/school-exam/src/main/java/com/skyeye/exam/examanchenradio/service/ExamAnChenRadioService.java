package com.skyeye.exam.examanchenradio.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;

import java.util.List;

/**
 * @ClassName: ExamAnChenRadioService
 * @Description: 答卷 矩阵单选题服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

public interface ExamAnChenRadioService extends SkyeyeBusinessService<ExamAnChenRadio> {
    void queryExamAnChenRadioListById(InputObject inputObject, OutputObject outputObject);

    List<ExamAnChenRadio> selectBySurveyId(String surveyId);

    List<ExamAnChenRadio> selectAnChenRadioByQuId(String id);
}
