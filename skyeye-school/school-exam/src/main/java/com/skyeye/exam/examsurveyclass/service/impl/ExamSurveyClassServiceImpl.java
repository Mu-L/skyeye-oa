package com.skyeye.exam.examsurveyclass.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examsurveyclass.dao.ExamSurveyClassDao;
import com.skyeye.exam.examsurveyclass.entity.ExamSurveyClass;
import com.skyeye.exam.examsurveyclass.service.ExamSurveyClassService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void createExamSurveyClass(String id, List<String> classIds, String userId) {
        List<ExamSurveyClass> examSurveyClassesList = new ArrayList<>();
        for (String classId : classIds) {
            ExamSurveyClass examSurveyClass = new ExamSurveyClass();
            examSurveyClass.setExamSurveyId(id);
            examSurveyClass.setClassId(classId);
            examSurveyClassesList.add(examSurveyClass);
        }
        createEntity(examSurveyClassesList, userId);
    }

    @Override
    public void deleteSurveyClassBySurveyId(String id) {
        QueryWrapper<ExamSurveyClass> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyClass::getExamSurveyId), id);
        remove(queryWrapper);
    }
}
