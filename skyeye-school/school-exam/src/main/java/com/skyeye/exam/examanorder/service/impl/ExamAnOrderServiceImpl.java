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
import com.skyeye.exam.examanorder.dao.ExamAnOrderDao;
import com.skyeye.exam.examanorder.entity.ExamAnOrder;
import com.skyeye.exam.examanorder.service.ExamAnOrderService;
import com.skyeye.exam.examanscore.entity.ExamAnScore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    protected void createPrepose(ExamAnOrder entity) {
        List<ExamAnOrder> orderByAn = entity.getOrderByAn();
        if (CollectionUtil.isNotEmpty(orderByAn)) {
            super.createEntity(orderByAn, StrUtil.EMPTY);
        }
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
    public List<ExamAnOrder> selectByQuIdAndStuId(String id, String studentId) {
        QueryWrapper<ExamAnOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamAnOrder::getQuId), id)
                .eq(MybatisPlusUtil.toColumns(ExamAnOrder::getCreateId), studentId);
        return list(queryWrapper);
    }
}
