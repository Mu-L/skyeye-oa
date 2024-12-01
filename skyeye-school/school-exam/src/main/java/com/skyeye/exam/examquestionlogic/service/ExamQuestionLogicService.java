package com.skyeye.exam.examquestionlogic.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.exam.examquestionlogic.entity.ExamQuestionLogic;

import java.util.List;

/**
 * @ClassName: ExamQuestionLogicService
 * @Description: 题目逻辑设置管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamQuestionLogicService extends SkyeyeBusinessService<ExamQuestionLogic> {
    List<ExamQuestionLogic> setLogics(String quId, List<ExamQuestionLogic> logicStr, String userId);
}
