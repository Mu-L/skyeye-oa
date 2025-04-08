package com.skyeye.exam.examanchenradio.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanchenfbk.entity.ExamAnChenFbk;
import com.skyeye.exam.examanchenradio.dao.ExamAnChenRadioDao;
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnChenRadioServiceImpl
 * @Description: 答卷 矩阵单选题服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 矩阵单选题", groupName = "答卷 矩阵单选题")
public class ExamAnChenRadioServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnChenRadioDao, ExamAnChenRadio> implements ExamAnChenRadioService {

    @Override
    protected void createPostpose(ExamAnChenRadio entity, String userId) {
        List<ExamAnChenRadio> dFillblankAn = entity.getChenRadioAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    public void queryExamAnChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnChenRadio> examAnChenRadioList = list(queryWrapper);
        outputObject.setBean(examAnChenRadioList);
        outputObject.settotal(examAnChenRadioList.size());
    }

    @Override
    public List<ExamAnChenRadio> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public ExamAnChenRadio selectById(String id) {
        ExamAnChenRadio examAnChenCheckbox = super.selectById(id);
        String belongAnswerId = examAnChenCheckbox.getBelongAnswerId();
        String belongId = examAnChenCheckbox.getBelongId();
        String quId = examAnChenCheckbox.getQuId();
        QueryWrapper<ExamAnChenRadio> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID,id);
        examAnChenCheckbox.setChenRadioAn(list(queryWrapper1));
        return examAnChenCheckbox;
    }

    @Override
    public List<ExamAnChenRadio> selectAnChenRadioByQuId(String id) {
        QueryWrapper<ExamAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getBelongId), surveyId)
                .eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnChenRadio>> selectByQuIdAndStuId(List<String> questionId, String studentId) {
        if (CollectionUtil.isEmpty(questionId)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnChenRadio::getQuId), questionId)
            .eq(MybatisPlusUtil.toColumns(ExamAnChenRadio::getCreateId),studentId);
        Map<String, List<ExamAnChenRadio>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnChenRadio::getQuId));
        return stringListMap;
    }

}
