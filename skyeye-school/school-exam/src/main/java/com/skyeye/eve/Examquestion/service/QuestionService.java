/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.Examquestion.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.Examquestion.entity.Question;

import java.util.List;

/**
 * @ClassName: QuestionService
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/15 15:18
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface QuestionService extends SkyeyeBusinessService<Question> {
    List<Question> QueryQuestionByBelongId(String belongId);

//    String saveQuestion(Question question, String id, String userId);

}
