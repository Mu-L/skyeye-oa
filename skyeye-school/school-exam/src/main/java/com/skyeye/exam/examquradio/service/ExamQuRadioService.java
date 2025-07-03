package com.skyeye.exam.examquradio.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examquestion.entity.Question;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamQuRadioService
 * @Description: 单选题选项表管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamQuRadioService extends SkyeyeBusinessService<ExamQuRadio> {
    void saveList(List<ExamQuRadio> list,String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<ExamQuRadio> selectQuRadio(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);

    void deleteByQuestionId(String entityId);

    Map<String, List<ExamQuRadio>> selectByQuestionIds(List<String> questionIdList);

    void createRadios(List<Question> questionList, String userId);

    void removeByQuIds(List<String> questionIds);

    void updateRadios(List<Question> questionList, String userId);

}
