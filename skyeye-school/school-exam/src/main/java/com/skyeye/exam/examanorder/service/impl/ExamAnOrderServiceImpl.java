package com.skyeye.exam.examanorder.service.impl;

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
import com.skyeye.exam.examandfillblank.entity.ExamAnDfillblank;
import com.skyeye.exam.examanorder.dao.ExamAnOrderDao;
import com.skyeye.exam.examanorder.entity.ExamAnOrder;
import com.skyeye.exam.examanorder.service.ExamAnOrderService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamAnOrderServiceImpl
 * @Description: 答卷 评分题接口服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "答卷 排序题", groupName = "答卷 排序题")
public class ExamAnOrderServiceImpl extends SkyeyeBusinessServiceImpl<ExamAnOrderDao, ExamAnOrder> implements ExamAnOrderService {

    @Override
    protected void createPostpose(ExamAnOrder examAnOrder, String userId) {
        List<ExamAnOrder> dFillblankAn = examAnOrder.getOrderByAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(ExamAnOrder entity, String userId) {
        List<ExamAnOrder> chenCheckboxAn = entity.getOrderByAn();
        List<ExamAnOrder> NoIdChenFbk = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())
        ).collect(Collectors.toList());
        List<ExamAnOrder> YesIdChenFbk = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())
        ).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(NoIdChenFbk)) {
            super.createEntity(NoIdChenFbk, userId);
        }
        if (CollectionUtil.isNotEmpty(YesIdChenFbk)) {
            super.updateEntity(YesIdChenFbk, userId);
        }
    }

    @Override
    public ExamAnOrder selectById(String id) {
        ExamAnOrder examAnDfillblank = super.selectById(id);
        String belongAnswerId = examAnDfillblank.getBelongAnswerId();
        String belongId = examAnDfillblank.getBelongId();
        String quId = examAnDfillblank.getQuId();
        QueryWrapper<ExamAnOrder> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnOrder::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnOrder::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(ExamAnOrder::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID,id);
        examAnDfillblank.setOrderByAn(list(queryWrapper1));
        return examAnDfillblank;
    }

    @Override
    public void queryExamAnOrderById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<ExamAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<ExamAnOrder> examAnOrderList = list(queryWrapper);
        outputObject.setBean(examAnOrderList);
        outputObject.settotal(examAnOrderList.size());
    }
    @Override
    public List<ExamAnOrder> selectBySurveyId(String surveyId) {
        QueryWrapper<ExamAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnOrder::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<ExamAnOrder> selectAnOrderByQuId(String id) {
        QueryWrapper<ExamAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnOrder::getQuId),id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurAndCreateId(String surveyId, String createId) {
        QueryWrapper<ExamAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnOrder::getBelongId), surveyId)
                .eq(MybatisPlusUtil.toColumns(ExamAnOrder::getCreateId), createId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ExamAnOrder>> selectByQuIdAndStuId(List<String> id, String studentId) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamAnOrder::getQuId), id)
                .eq(MybatisPlusUtil.toColumns(ExamAnOrder::getCreateId), studentId);
        Map<String, List<ExamAnOrder>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(ExamAnOrder::getQuId));
        return stringListMap;
    }
}
