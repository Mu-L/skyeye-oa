package com.skyeye.exam.examanyesno.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanyesno.dao.ExamAnYesnoDao;
import com.skyeye.exam.examanyesno.entity.ExamAnYesno;
import com.skyeye.exam.examanyesno.service.ExamAnYesnoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "判断题保存表管理", groupName = "判断题保存表管理")
public class ExamAnYesnoServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnYesnoDao, ExamAnYesno> implements ExamAnYesnoService {

    @Override
    public void queryExamAnYesnoListById(InputObject inputObject, OutputObject outputObject) {
        String examAnYesnoId = inputObject.getParams().get("id").toString();
        QueryWrapper<ExamAnYesno> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examAnYesnoId);
        List<ExamAnYesno> examAnYesnoList = list(queryWrapper);
        outputObject.setBean(examAnYesnoList);
        outputObject.settotal(examAnYesnoList.size());
    }

    @Override
    public List<ExamAnYesno> selectBySurveyId(String surveyId, String id) {
        QueryWrapper<ExamAnYesno> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnYesno::getBelongId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnYesno::getBelongAnswerId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnYesno> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnYesno::getBelongId), surveyId)
                .eq(MybatisPlusUtil.toColumns(ExamAnYesno::getCreateId), createId);
        remove(queryWrapper);
    }
}
