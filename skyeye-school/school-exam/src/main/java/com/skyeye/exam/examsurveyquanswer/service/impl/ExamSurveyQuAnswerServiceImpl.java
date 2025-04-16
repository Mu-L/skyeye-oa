package com.skyeye.exam.examsurveyquanswer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examsurveyquanswer.dao.ExamSurveyQuAnswerDao;
import com.skyeye.exam.examsurveyquanswer.entity.ExamSurveyQuAnswer;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public void queryExamSurveyQuAnswerListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamSurveyQuAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamSurveyQuAnswer> examSurveyQuAnswerList = list(queryWrapper);
        outputObject.setBean(examSurveyQuAnswerList);
        outputObject.settotal(examSurveyQuAnswerList.size());
    }

    @Override
    public float selectFractionBySurveyId(String surveyId, String id) {
        QueryWrapper<ExamSurveyQuAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyQuAnswer::getSurveyId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyQuAnswer::getBelongAnswerId), id);
        List<ExamSurveyQuAnswer> examSurveyQuAnswerList = list(queryWrapper);
        float totalFraction = (float) examSurveyQuAnswerList.stream()
            .mapToDouble(ExamSurveyQuAnswer::getFraction)
            .sum();
        return totalFraction;
    }

    @Override
    public Map<String, List<ExamSurveyQuAnswer>> selectFacByIdAndSurveyId(String id, String surveyId) {
        QueryWrapper<ExamSurveyQuAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyQuAnswer::getBelongAnswerId), id);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyQuAnswer::getSurveyId), surveyId);
        List<ExamSurveyQuAnswer> examSurveyQuAnswerList = list(queryWrapper);
        Map<String, List<ExamSurveyQuAnswer>> quIdToFractionMap = examSurveyQuAnswerList.stream()
            .collect(Collectors.groupingBy(ExamSurveyQuAnswer::getQuId));
        return quIdToFractionMap;
    }
}
