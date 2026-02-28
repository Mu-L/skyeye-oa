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

    /**
     * 柱状图固定顺序：与 AfterSealState 枚举顺序一致
     */
    private static final AfterSealState[] STATE_ORDER = {
        AfterSealState.BE_DISPATCHED,
        AfterSealState.PENDING_ORDERS,
        AfterSealState.BE_SIGNED,
        AfterSealState.BE_COMPLETED,
        AfterSealState.BE_EVALUATED,
        AfterSealState.AUDIT,
        AfterSealState.COMPLATE
    };

    @Override
    public void queryOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<AfterSeal> queryWrapper = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());

        List<AfterSeal> list = afterSealDao.selectList(queryWrapper);
        long total = list.size();
        Map<String, Long> stateCountMap = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getState()))
            .collect(Collectors.groupingBy(AfterSeal::getState, Collectors.counting()));

        // 柱状图格式：横轴类目 + 纵轴数值，与 ECharts 等可直接使用
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (AfterSealState state : STATE_ORDER) {
            xAxisData.add(state.getValue());
            seriesData.add(stateCountMap.getOrDefault(state.getKey(), 0L));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

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
