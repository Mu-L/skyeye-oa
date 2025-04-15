package com.skyeye.exam.examanchencheckbox.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanchencheckbox.dao.ExamAnChenCheckboxDao;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnChenCheckboxServiceImpl
 * @Description: 答卷 矩阵多选题管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 矩阵多选题", groupName = "答卷 矩阵多选题")
public class ExamAnChenCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnChenCheckboxDao, ExamAnChenCheckbox> implements ExamAnChenCheckboxService {

    @Override
    protected void createPostpose(ExamAnChenCheckbox entity, String userId) {
        List<ExamAnChenCheckbox> dFillblankAn = entity.getChenCheckboxAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(ExamAnChenCheckbox entity, String userId) {
        List<ExamAnChenCheckbox> chenCheckboxAn = entity.getChenCheckboxAn();
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID,  entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongAnswerId), entity.getBelongAnswerId());
        List<ExamAnChenCheckbox> examAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<ExamAnChenCheckbox> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<ExamAnChenCheckbox> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(ExamAnChenCheckbox::getId).collect(Collectors.toSet());
        List<ExamAnChenCheckbox> result = examAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(ExamAnChenCheckbox::getId).collect(Collectors.toList());
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
    public void queryExamAnChenCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnChenCheckbox> examAnChenCheckboxList = list(queryWrapper);
        outputObject.setBean(examAnChenCheckboxList);
        outputObject.settotal(examAnChenCheckboxList.size());
    }

    @Override
    public List<ExamAnChenCheckbox> selectBySurveyId(String surveyId, String id) {
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongAnswerId), id);
        return list(queryWrapper);
    }

    @Override
    public ExamAnChenCheckbox selectById(String id) {
        ExamAnChenCheckbox examAnChenCheckbox = super.selectById(id);
        String belongAnswerId = examAnChenCheckbox.getBelongAnswerId();
        String belongId = examAnChenCheckbox.getBelongId();
        String quId = examAnChenCheckbox.getQuId();
        QueryWrapper<ExamAnChenCheckbox> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        examAnChenCheckbox.setChenCheckboxAn(list(queryWrapper1));
        return examAnChenCheckbox;
    }

    @Override
    public List<ExamAnChenCheckbox> selectAnChenCheckboxByQuId(String id) {
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getBelongId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnChenCheckbox>> selectByQuIdAndStuId(List<String> questionId, String studentId) {
        if (CollectionUtil.isEmpty(questionId)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnChenCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getQuId), questionId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenCheckbox::getCreateId), studentId);
        Map<String, List<ExamAnChenCheckbox>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnChenCheckbox::getQuId));
        return stringListMap;
    }
}
