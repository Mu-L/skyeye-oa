package com.skyeye.exam.examancompchenradio.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examancompchenradio.dao.ExamAnCompChenRadioDao;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;
import com.skyeye.exam.examancompchenradio.service.ExamAnCompChenRadioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnCompChenRadioServiceImpl
 * @Description: 答卷 复合矩阵单选题服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Service
@SkyeyeService(name = "答卷 复合矩阵单选题", groupName = "答卷 复合矩阵单选题")
public class ExamAnCompChenRadioServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnCompChenRadioDao, ExamAnCompChenRadio> implements ExamAnCompChenRadioService {

    @Override
    protected void createPrepose(ExamAnCompChenRadio entity) {
        List<ExamAnCompChenRadio> compChenRadioAn = entity.getCompChenRadioAn();
        if (CollectionUtil.isNotEmpty(compChenRadioAn)) {
            super.createEntity(compChenRadioAn, StrUtil.EMPTY);
        }
    }

    @Override
    public void queryExamAnCompChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnCompChenRadio> examAnCompChenRadioList = list(queryWrapper);
        outputObject.setBean(examAnCompChenRadioList);
        outputObject.settotal(examAnCompChenRadioList.size());
    }

    @Override
    public List<ExamAnCompChenRadio> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnCompChenRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamAnCompChenRadio> selectByQuId(String id) {
        QueryWrapper<ExamAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnCompChenRadio::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnCompChenRadio::getBelongId), surveyId)
            .eq(MybatisPlusUtil.toColumns(ExamAnCompChenRadio::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnCompChenRadio>> selectByQuIdAndStuId(List<String> questionId, String studentId) {
        QueryWrapper<ExamAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnCompChenRadio::getQuId), questionId)
            .eq(MybatisPlusUtil.toColumns(ExamAnCompChenRadio::getCreateId), studentId);
        Map<String, List<ExamAnCompChenRadio>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnCompChenRadio::getQuId));
        return stringListMap;
    }
}
