package com.skyeye.exam.examandfillblank.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;
import com.skyeye.exam.examandfillblank.dao.ExamAnDfilllankDao;
import com.skyeye.exam.examandfillblank.entity.ExamAnDfillblank;
import com.skyeye.exam.examandfillblank.service.ExamAnDfilllankService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnDfilllankServiceImpl
 * @Description: 答卷 多行填空题保存表服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 多行填空题保存表", groupName = "答卷 多行填空题保存表")
public class ExamAnDfilllankServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnDfilllankDao, ExamAnDfillblank> implements ExamAnDfilllankService {


    @Override
    protected void createPostpose(ExamAnDfillblank entity, String userId) {
        List<ExamAnDfillblank> dFillblankAn = entity.getDFillblankAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(ExamAnDfillblank entity, String userId) {
        List<ExamAnDfillblank> chenCheckboxAn = entity.getDFillblankAn();
        if (CollectionUtil.isNotEmpty(chenCheckboxAn)) {
            super.updateEntity(chenCheckboxAn, userId);
        }
    }


    @Override
    public void queryExamAnDfilllankById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnDfillblank> examAnDfillblankList = list(queryWrapper);
        outputObject.setBean(examAnDfillblankList);
        outputObject.settotal(examAnDfillblankList.size());
    }

    @Override
    public List<ExamAnDfillblank> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamAnDfillblank> selectAnMultiFillblankQuId(String id) {
        QueryWrapper<ExamAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getBelongId), surveyId)
            .eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public ExamAnDfillblank selectById(String id) {
        ExamAnDfillblank examAnDfillblank = super.selectById(id);
        String belongAnswerId = examAnDfillblank.getBelongAnswerId();
        String belongId = examAnDfillblank.getBelongId();
        String quId = examAnDfillblank.getQuId();
        QueryWrapper<ExamAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getBelongAnswerId), belongAnswerId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getBelongId), belongId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getQuId), quId);
        queryWrapper.ne(CommonConstants.ID,id);
        examAnDfillblank.setDFillblankAn(list(queryWrapper));
        return examAnDfillblank;
    }

    @Override
    public Map<String, List<ExamAnDfillblank>> selectByQuIdAndStuId(List<String> id, String studentId) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnDfillblank::getQuId), id)
            .eq(MybatisPlusUtil.toColumns(ExamAnDfillblank::getCreateId), studentId);
        Map<String, List<ExamAnDfillblank>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnDfillblank::getQuId));
        return stringListMap;
    }
}
