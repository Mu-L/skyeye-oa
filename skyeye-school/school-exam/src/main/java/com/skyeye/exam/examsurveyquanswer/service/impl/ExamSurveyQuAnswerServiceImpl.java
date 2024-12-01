package com.skyeye.exam.examSurveyQuAnswer.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examSurveyQuAnswer.dao.ExamSurveyQuAnswerDao;
import com.skyeye.exam.examSurveyQuAnswer.entity.ExamSurveyQuAnswer;
import com.skyeye.exam.examSurveyQuAnswer.service.ExamSurveyQuAnswerService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamSurveyQuAnswerServiceImpl
 * @Description: 答卷 题目和所得分数的关联表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 题目和所得分数的关联表管理", groupName = "答卷 题目和所得分数的关联表管理")
public class ExamSurveyQuAnswerServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyQuAnswerDao, ExamSurveyQuAnswer> implements ExamSurveyQuAnswerService {
}
