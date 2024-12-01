package com.skyeye.exam.examSurveyClass.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examSurveyAnswer.dao.ExamSurveyAnswerDao;
import com.skyeye.exam.examSurveyAnswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examSurveyAnswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examSurveyClass.dao.ExamSurveyClassDao;
import com.skyeye.exam.examSurveyClass.entity.ExamSurveyClass;
import com.skyeye.exam.examSurveyClass.service.ExamSurveyClassService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ExamSurveyClassServiceImpl
 * @Description: 试卷与班级关系表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷与班级关系表管理", groupName = "试卷与班级关系表管理")
public class ExamSurveyClassServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyClassDao, ExamSurveyClass> implements ExamSurveyClassService {


}
