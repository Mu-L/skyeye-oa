package com.skyeye.exam.examancheckbox.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examancheckbox.dao.ExamAnCheckboxDao;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examancheckbox.service.ExamAnCheckboxService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnCheckboxServiceImpl
 * @Description: 答卷 多选题保存表服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 多选题保存表", groupName = "答卷 多选题保存表")
public class ExamAnCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnCheckboxDao, ExamAnCheckbox> implements ExamAnCheckboxService {


    @Override
    protected void createPrepose(ExamAnCheckbox entity) {
        String quItemId = entity.getQuItemId();
        String[] splitArray = quItemId.split(",");
        List<String> resultList = Arrays.asList(splitArray);
        List<ExamAnCheckbox> examAnCheckboxList = new ArrayList<>();
        for (String quAnswerId : resultList) {
            ExamAnCheckbox examAnCheckbox = new ExamAnCheckbox();
            examAnCheckbox.setQuItemId(quAnswerId);
            examAnCheckboxList.add(examAnCheckbox);
        }
        super.createEntity(examAnCheckboxList, StrUtil.EMPTY);
    }

    @Override
    public void queryExamAnCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnCheckbox> examAnCheckboxList = list(queryWrapper);
        outputObject.setBean(examAnCheckboxList);
        outputObject.settotal(examAnCheckboxList.size());
    }

    @Override
    public long slectBySurveyId(String surveyId, String id) {
        QueryWrapper<ExamAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnCheckbox::getBelongId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnCheckbox::getBelongAnswerId), id)
            .select(CommonConstants.ID);
        return count(queryWrapper);
    }

    @Override
    public List<ExamAnCheckbox> selectAnCheckBoxByQuId(String id) {
        QueryWrapper<ExamAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnCheckbox::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnCheckbox::getBelongId), surveyId)
                .eq(MybatisPlusUtil.toColumns(ExamAnCheckbox::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnCheckbox>> selectByQuIdAndStuId(List<String> id, String studentId) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnCheckbox::getQuId), id)
                .eq(MybatisPlusUtil.toColumns(ExamAnCheckbox::getCreateId), studentId);
        Map<String, List<ExamAnCheckbox>> cheneckBoxMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnCheckbox::getQuId));
        return cheneckBoxMap;
    }
}
