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
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;
import com.skyeye.exam.examanchenscore.dao.ExamAnChenScoreDao;
import com.skyeye.exam.examanchenscore.entity.ExamAnChenScore;
import com.skyeye.exam.examanchenscore.service.ExamAnChenScoreService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    protected void createPostpose(ExamAnChenScore entity, String userId) {
        List<ExamAnChenScore> dFillblankAn = entity.getChenScoreAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(ExamAnChenScore entity, String userId) {
        List<ExamAnChenScore> chenCheckboxAn = entity.getChenScoreAn();
        QueryWrapper<ExamAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID,  entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getBelongAnswerId), entity.getBelongAnswerId());
        List<ExamAnChenScore> examAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<ExamAnChenScore> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<ExamAnChenScore> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(ExamAnChenScore::getId).collect(Collectors.toSet());
        List<ExamAnChenScore> result = examAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(ExamAnChenScore::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(TodeleteIds)) {
            deleteById(TodeleteIds);
        }
        if (CollectionUtil.isNotEmpty(NoIdChenCheckbox)) {
            super.createEntity(NoIdChenCheckbox, userId);
        }
        if (CollectionUtil.isNotEmpty(YesIdChenCheckbox)) {
            super.updateEntity(YesIdChenCheckbox, userId);
        }
    }

    @Override
    public ExamAnChenScore selectById(String id) {
        ExamAnChenScore examAnChenCheckbox = super.selectById(id);
        String belongAnswerId = examAnChenCheckbox.getBelongAnswerId();
        String belongId = examAnChenCheckbox.getBelongId();
        String quId = examAnChenCheckbox.getQuId();
        QueryWrapper<ExamAnChenScore> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        examAnChenCheckbox.setChenScoreAn(list(queryWrapper1));
        return examAnChenCheckbox;
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
        if (CollectionUtil.isEmpty(questionId)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnChenScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnChenScore::getQuId), questionId)
            .eq(MybatisPlusUtil.toColumns(ExamAnChenScore::getCreateId), studentId);
        Map<String, List<ExamAnChenScore>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnChenScore::getQuId));
        return stringListMap;
    }

}
