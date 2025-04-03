package com.skyeye.exam.examsurveymarkexam.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examsurveymarkexam.dao.ExamSurveyMarkExamDao;
import com.skyeye.exam.examsurveymarkexam.entity.ExamSurveyMarkExam;
import com.skyeye.exam.examsurveymarkexam.service.ExamSurveyMarkExamService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public void createExamSurveyMarkExam(String id, List<String> readerIds, String userId) {
        List<ExamSurveyMarkExam> examSurveyMarkExamList = new ArrayList<>();
        for (String readerId : readerIds) {
            ExamSurveyMarkExam examSurveyMarkExam = new ExamSurveyMarkExam();
            examSurveyMarkExam.setSurveyId(id);
            examSurveyMarkExam.setUserId(readerId);
            examSurveyMarkExamList.add(examSurveyMarkExam);
        }
        createEntity(examSurveyMarkExamList,userId);
    }

    @Override
    public List<ExamSurveyMarkExam> selectBySurveyId(String surveyId) {
        return list(new QueryWrapper<ExamSurveyMarkExam>().eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), surveyId));
    }

    @Override
    public void deleteSurveyMarkExamBySurveyId(String id) {
        remove(new QueryWrapper<ExamSurveyMarkExam>().eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), id));
    }

    @Override
    public List<ExamSurveyMarkExam> getExamSurveyMarkExamList(String examDirectoryId){
        QueryWrapper<ExamSurveyMarkExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), examDirectoryId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamSurveyMarkExam> selectByUserId(String userId) {
        QueryWrapper<ExamSurveyMarkExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getUserId), userId);
        return list(queryWrapper);
    }

    @Override
    public Map<String, Integer> queryMarkedExamNum(List<String> directoryIds) {
        QueryWrapper<ExamSurveyMarkExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), directoryIds);
        List<ExamSurveyMarkExam> list = list(queryWrapper);
        Map<String, List<ExamSurveyMarkExam>> collect = list.stream().collect(Collectors.groupingBy(ExamSurveyMarkExam::getSurveyId));
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, List<ExamSurveyMarkExam>> entry : collect.entrySet()) {
            map.put(entry.getKey(), entry.getValue().size());
        }
        if(CollectionUtil.isEmpty(map)){
            return new HashMap<>();
        }
        return map;
    }


}
