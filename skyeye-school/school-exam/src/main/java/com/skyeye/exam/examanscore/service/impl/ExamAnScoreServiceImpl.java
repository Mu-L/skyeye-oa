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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "评分题保存表管理", groupName = "评分题保存表管理")
public class ExamAnScoreServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnScoreDao, ExamAnScore> implements ExamAnScoreService {

    @Override
    protected void createPostpose(ExamAnScore examAnOrder, String userId) {
        List<ExamAnScore> dFillblankAn = examAnOrder.getScoreAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(ExamAnScore entity, String userId) {
        List<ExamAnScore> chenCheckboxAn = entity.getScoreAn();
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID,  entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongAnswerId), entity.getBelongAnswerId());
        List<ExamAnScore> examAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<ExamAnScore> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<ExamAnScore> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(ExamAnScore::getId).collect(Collectors.toSet());
        List<ExamAnScore> result = examAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(ExamAnScore::getId).collect(Collectors.toList());
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
    public ExamAnScore selectById(String id) {
        ExamAnScore examAnDfillblank = super.selectById(id);
        String belongAnswerId = examAnDfillblank.getBelongAnswerId();
        String belongId = examAnDfillblank.getBelongId();
        String quId = examAnDfillblank.getQuId();
        QueryWrapper<ExamAnScore> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnScore::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        examAnDfillblank.setScoreAn(list(queryWrapper1));
        return examAnDfillblank;
    }

    @Override
    public void queryExamAnScoreListById(InputObject inputObject, OutputObject outputObject) {
        String examAnScoreId = inputObject.getParams().get("id").toString();
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examAnScoreId);
        List<ExamAnScore> examAnScoreList = list(queryWrapper);
        outputObject.setBean(examAnScoreList);
        outputObject.settotal(examAnScoreList.size());
    }

    @Override
    public long selectBySurveyId(String surveyId, String id) {
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongAnswerId), id)
            .select(CommonConstants.ID);
        return count(queryWrapper);
    }

    @Override
    public List<ExamAnScore> selectAnScoreByQuId(String id) {
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnScore::getBelongId), surveyId)
            .eq(MybatisPlusUtil.toColumns(ExamAnScore::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnScore>> selectByQuIdAndStuId(List<String> id, String studentId) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnScore::getQuId), id)
            .eq(MybatisPlusUtil.toColumns(ExamAnScore::getCreateId), studentId);
        Map<String, List<ExamAnScore>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnScore::getQuId));
        return stringListMap;
    }
}
