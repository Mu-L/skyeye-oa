/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.afterseal.classenum.AfterSealState;
import com.skyeye.afterseal.dao.AfterSealDao;
import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.service.AfterSealStatisticsService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工单统计服务实现：按状态、完成率、区域、紧急程度、项目等维度统计售后工单
 */
@Service
public class AfterSealStatisticsServiceImpl implements AfterSealStatisticsService {

    @Autowired
    private AfterSealDao afterSealDao;

    @Override
    public void queryOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        long total = list.size();
        Map<String, Long> stateCountMap = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getState()))
            .collect(Collectors.groupingBy(AfterSeal::getState, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("beDispatched", stateCountMap.getOrDefault(AfterSealState.BE_DISPATCHED.getKey(), 0L));
        result.put("pendingOrders", stateCountMap.getOrDefault(AfterSealState.PENDING_ORDERS.getKey(), 0L));
        result.put("beSigned", stateCountMap.getOrDefault(AfterSealState.BE_SIGNED.getKey(), 0L));
        result.put("beCompleted", stateCountMap.getOrDefault(AfterSealState.BE_COMPLETED.getKey(), 0L));
        result.put("beEvaluated", stateCountMap.getOrDefault(AfterSealState.BE_EVALUATED.getKey(), 0L));
        result.put("audit", stateCountMap.getOrDefault(AfterSealState.AUDIT.getKey(), 0L));
        result.put("complate", stateCountMap.getOrDefault(AfterSealState.COMPLATE.getKey(), 0L));

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        long total = list.size();
        long completed = list.stream()
            .filter(o -> AfterSealState.COMPLATE.getKey().equals(o.getState()))
            .count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        if (total > 0) {
            result.put("completionRate", CalculationUtil.divide(String.valueOf(completed), String.valueOf(total), CommonNumConstants.NUM_TWO));
        } else {
            result.put("completionRate", CommonNumConstants.NUM_ZERO);
        }

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryOrderStatsByRegion(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        // 按省统计
        Map<String, Long> provinceStats = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getProvinceId()))
            .collect(Collectors.groupingBy(AfterSeal::getProvinceId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : provinceStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("provinceId", entry.getKey());
            item.put("level", "province");
            item.put("orderCount", entry.getValue());
            result.add(item);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryOrderStatsByUrgency(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        Map<String, Long> urgencyStats = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getUrgencyId()))
            .collect(Collectors.groupingBy(AfterSeal::getUrgencyId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : urgencyStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("urgencyId", entry.getKey());
            item.put("orderCount", entry.getValue());
            result.add(item);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryOrderStatsByProject(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        Map<String, Long> projectStats = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getProjectId()))
            .collect(Collectors.groupingBy(AfterSeal::getProjectId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : projectStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("projectId", entry.getKey());
            item.put("orderCount", entry.getValue());
            result.add(item);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    private QueryWrapper<AfterSeal> buildTimeRangeWrapper(String startTime, String endTime) {
        QueryWrapper<AfterSeal> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(AfterSeal::getCreateTime), endTime);
        }
        return queryWrapper;
    }
}
