package com.skyeye.exam.examanradio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanradio.dao.ExamAnRadioDao;
import com.skyeye.exam.examanradio.entity.ExamAnRadio;
import com.skyeye.exam.examanradio.service.ExamAnRadioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "单选题保存表管理", groupName = "单选题保存表管理")
public class ExamAnRadioServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnRadioDao, ExamAnRadio> implements ExamAnRadioService {

    @Override
    public void queryExamAnRadioListById(InputObject inputObject, OutputObject outputObject) {
        String examAnRadioId = inputObject.getParams().get("id").toString();
        QueryWrapper<ExamAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID,examAnRadioId);
        List<ExamAnRadio> examAnRadioList = list(queryWrapper);
        outputObject.setBean(examAnRadioList);
        outputObject.settotal(examAnRadioList.size());
    }

    @Override
    public List<ExamAnRadio> selectRadioBySurveyId(String surveyId) {
        QueryWrapper<ExamAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamAnRadio> selectByQuid(String id) {
        QueryWrapper<ExamAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnRadio::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnRadio::getBelongId), surveyId)
                .eq(MybatisPlusUtil.toColumns(ExamAnRadio::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public List<ExamAnRadio> selectByQuIdAndStuId(String id, String studentId) {
        QueryWrapper<ExamAnRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnRadio::getQuId), id)
                .eq(MybatisPlusUtil.toColumns(ExamAnRadio::getCreateId), studentId);
        return list(queryWrapper);
    }
}
