package com.skyeye.exam.examanchenscore.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanchenscore.dao.ExamAnChenScoreDao;
import com.skyeye.exam.examanchenscore.entity.ExamAnChenScore;
import com.skyeye.exam.examanchenscore.service.ExamAnChenScoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnChenScoreServiceImpl
 * @Description: 答卷 矩阵多选题服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 矩阵多选题", groupName = "答卷 矩阵多选题")
public class ExamAnChenScoreServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnChenScoreDao, ExamAnChenScore> implements ExamAnChenScoreService {

    @Override
    protected void createPrepose(ExamAnChenScore entity) {
        List<ExamAnChenScore> chenScoreAn = entity.getChenScoreAn();
        if (CollectionUtil.isNotEmpty(chenScoreAn)) {
            super.createEntity(chenScoreAn, StrUtil.EMPTY);
        }
    }

    @Override
    public void queryExamAnChenScoreListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnChenScore> examAnChenScoreList = list(queryWrapper);
        outputObject.setBean(examAnChenScoreList);
        outputObject.settotal(examAnChenScoreList.size());
    }

    @Override
    public List<ExamAnChenScore> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamAnChenScore> selectByQuId(String id) {
        QueryWrapper<ExamAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getBelongId), surveyId)
            .eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnChenScore>> selectByQuIdAndStuId(List<String> questionId, String studentId) {
        QueryWrapper<ExamAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnChenScore::getQuId), questionId)
            .eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getCreateId), studentId);
        Map<String, List<ExamAnChenScore>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnChenScore::getQuId));
        return stringListMap;
    }

}
