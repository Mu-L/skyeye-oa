package com.skyeye.exam.examQuestionLogic.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examQuestionLogic.dao.ExamQuestionLogicDao;
import com.skyeye.exam.examQuestionLogic.entity.ExamQuestionLogic;
import com.skyeye.exam.examQuestionLogic.service.ExamQuestionLogicService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamQuestionLogicServiceImpl
 * @Description: 题目逻辑设置管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "题目逻辑设置管理", groupName = "题目逻辑设置管理")
public class ExamQuestionLogicServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuestionLogicDao, ExamQuestionLogic> implements ExamQuestionLogicService  {
}
