package com.skyeye.exam.examanfillblank.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examanfillblank.dao.ExamAnFillblankDao;
import com.skyeye.exam.examanfillblank.entity.ExamAnFillblank;
import com.skyeye.exam.examanfillblank.service.ExamAnFillblankService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnFillblankController
 * @Description: 答卷 填空题保存表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "填空题保存表", groupName = "填空题保存表")
public class ExamAnFillblankServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnFillblankDao, ExamAnFillblank> implements ExamAnFillblankService {

    @Override
    public void queryExamAnFillblankListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnFillblank> examAnFillblankList = list(queryWrapper);
        outputObject.setBean(examAnFillblankList);
        outputObject.settotal(examAnFillblankList.size());
    }

    @Override
    public List<ExamAnFillblank> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnFillblank::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnFillblank::getBelongId), surveyId)
                .eq(MybatisPlusUtil.toColumns(ExamAnFillblank::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnFillblank>> selectByQuIdAndStuId(List<String> multifillblankIds, String studentId) {
        if (CollectionUtil.isEmpty(multifillblankIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnFillblank::getQuId), multifillblankIds)
            .eq(MybatisPlusUtil.toColumns(ExamAnFillblank::getCreateId), studentId);
        Map<String, List<ExamAnFillblank>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnFillblank::getQuId));
        return stringListMap;
    }
}
