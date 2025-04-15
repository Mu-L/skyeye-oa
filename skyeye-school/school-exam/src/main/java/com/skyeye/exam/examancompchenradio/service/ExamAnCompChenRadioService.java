package com.skyeye.exam.examancompchenradio.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamAnCompChenRadioService
 * @Description: 答卷 复合矩阵单选题服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

public interface ExamAnCompChenRadioService extends SkyeyeBusinessService<ExamAnCompChenRadio> {
    void queryExamAnCompChenRadioListById(InputObject inputObject, OutputObject outputObject);

    List<ExamAnCompChenRadio> selectBySurveyId(String surveyId, String id);

    List<ExamAnCompChenRadio> selectByQuId(String id);

    void deleteBySurAndCreateId(String surveyId, String createId);

    Map<String, List<ExamAnCompChenRadio>> selectByQuIdAndStuId(List<String> questionId, String studentId);
}
