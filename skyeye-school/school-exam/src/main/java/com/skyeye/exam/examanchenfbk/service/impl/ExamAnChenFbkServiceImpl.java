package com.skyeye.exam.examanchenfbk.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchenfbk.dao.ExamAnChenFbkDao;
import com.skyeye.exam.examanchenfbk.entity.ExamAnChenFbk;
import com.skyeye.exam.examanchenfbk.service.ExamAnChenFbkService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnChenFbkServiceImpl
 * @Description: 答卷 矩阵填空题管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 矩阵填空题", groupName = "答卷 矩阵填空题")
public class ExamAnChenFbkServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnChenFbkDao, ExamAnChenFbk> implements ExamAnChenFbkService {

    @Override
    protected void createPostpose(ExamAnChenFbk entity, String userId) {
        List<ExamAnChenFbk> dFillblankAn = entity.getChenFbkAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(ExamAnChenFbk entity, String userId) {
        List<ExamAnChenFbk> chenFbkList = entity.getChenFbkAn();
        QueryWrapper<ExamAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID,entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getBelongAnswerId), entity.getBelongAnswerId());
        List<ExamAnChenFbk> examAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<ExamAnChenFbk> NoIdChenCheckbox = chenFbkList.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<ExamAnChenFbk> YesIdChenCheckbox = chenFbkList.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(ExamAnChenFbk::getId).collect(Collectors.toSet());
        List<ExamAnChenFbk> result = examAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(ExamAnChenFbk::getId).collect(Collectors.toList());
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
    public void queryExamAnChenFbkListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnChenFbk> examAnChenFbkList = list(queryWrapper);
        outputObject.setBean(examAnChenFbkList);
        outputObject.settotal(examAnChenFbkList.size());
    }

    @Override
    public List<ExamAnChenFbk> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public ExamAnChenFbk selectById(String id) {
        ExamAnChenFbk examAnChenCheckbox = super.selectById(id);
        String belongAnswerId = examAnChenCheckbox.getBelongAnswerId();
        String belongId = examAnChenCheckbox.getBelongId();
        String quId = examAnChenCheckbox.getQuId();
        QueryWrapper<ExamAnChenFbk> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID,id);
        examAnChenCheckbox.setChenFbkAn(list(queryWrapper1));
        return examAnChenCheckbox;
    }

    @Override
    public List<ExamAnChenFbk> selectByQuId(String id) {
        QueryWrapper<ExamAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getBelongId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnChenFbk>> selectByQuIdAndStuId(List<String> questionId, String studentId) {
        if (CollectionUtil.isEmpty(questionId)){
            return new HashMap<>();
        }
        QueryWrapper<ExamAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnChenFbk::getQuId), questionId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenFbk::getCreateId), studentId);
        Map<String, List<ExamAnChenFbk>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnChenFbk::getQuId));
        return stringListMap;
    }
}
