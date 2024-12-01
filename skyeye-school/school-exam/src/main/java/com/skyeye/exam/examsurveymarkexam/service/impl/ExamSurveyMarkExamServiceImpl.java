package com.skyeye.exam.examSurveyMarkExam.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.exam.examSurveyMarkExam.dao.ExamSurveyMarkExamDao;
import com.skyeye.exam.examSurveyMarkExam.entity.ExamSurveyMarkExam;
import com.skyeye.exam.examSurveyMarkExam.service.ExamSurveyMarkExamService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamSurveyMarkExamServiceImpl
 * @Description: 试卷与阅卷人关系表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷与阅卷人关系表管理", groupName = "试卷与阅卷人关系表管理")
public class ExamSurveyMarkExamServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyMarkExamDao, ExamSurveyMarkExam> implements ExamSurveyMarkExamService {
}
