package com.skyeye.exam.examquchckbox.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;
import com.skyeye.exam.examquestion.entity.Question;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamQuCheckboxService
 * @Description: 多选题选项表服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/11/1 18:51
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface ExamQuCheckboxService extends SkyeyeBusinessService<ExamQuCheckbox> {
    void saveList(List<ExamQuCheckbox> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<ExamQuCheckbox> selectQuChenbox(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);

    Map<String, List<ExamQuCheckbox>> selectByQuestionIds(List<String> questionIdList);

    void createCheckboxs(List<Question> questionList, String userId);

    void removeByQuIds(List<String> questionIds);

    void updateCheckboxs(List<Question> questionList, String userId);

}
