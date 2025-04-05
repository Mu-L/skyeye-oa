package com.skyeye.exam.examanscore.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanscore.dao.ExamAnScoreDao;
import com.skyeye.exam.examanscore.entity.ExamAnScore;
import com.skyeye.exam.examanscore.service.ExamAnScoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "评分题保存表管理", groupName = "评分题保存表管理")
public class ExamAnScoreServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnScoreDao, ExamAnScore> implements ExamAnScoreService {


    @Override
    protected void createPrepose(ExamAnScore entity) {
        List<ExamAnScore> scoreAn = entity.getScoreAn();
        if (CollectionUtil.isNotEmpty(scoreAn)){
            super.createEntity(scoreAn, StrUtil.EMPTY);
        }
    }

    @Override
    public void queryExamAnScoreListById(InputObject inputObject, OutputObject outputObject) {
        String examAnScoreId = inputObject.getParams().get("id").toString();
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID,examAnScoreId);
        List<ExamAnScore> examAnScoreList = list(queryWrapper);
        outputObject.setBean(examAnScoreList);
        outputObject.settotal(examAnScoreList.size());
    }

    @Override
    public List<ExamAnScore> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongId),surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamAnScore> selectAnScoreByQuId(String id) {
       QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getQuId),id);
       return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongId),surveyId)
                .eq(MybatisPlusUtil.toColumns(ExamAnScore::getCreateId),createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnScore>> selectByQuIdAndStuId(List<String> id, String studentId) {
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnScore::getQuId),id)
                .eq(MybatisPlusUtil.toColumns(ExamAnScore::getCreateId),studentId);
        Map<String, List<ExamAnScore>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnScore::getQuId));
        return stringListMap;
    }
}
