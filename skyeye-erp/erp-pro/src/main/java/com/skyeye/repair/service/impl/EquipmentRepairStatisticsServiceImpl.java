/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentRepairStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报修维修统计服务层（看板本月区间等仍按报修时间 {@link DateUtil#YYYY_MM_DD_HH_MM} 计算）
 *
 * @author skyeye云系列--卫志强
 * @Copyright 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
@Service
public class EquipmentRepairStatisticsServiceImpl implements EquipmentRepairStatisticsService {

    /**
     * 空值或缺失数据归类显示名称
     */
    private static final String OTHER_LABEL = "其他";

    @Autowired
    private EquipmentRepairOrderDao equipmentRepairOrderDao;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Override
    public void queryRepairMonthlyTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getDispatchTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getDispatchTime), endTime);
        }

        long total = equipmentRepairOrderDao.selectCount(queryWrapper);

        Map<String, Long> monthCountMap = new HashMap<>();
        List<EquipmentRepairOrder> orderList = equipmentRepairOrderDao.selectList(queryWrapper);
        for (EquipmentRepairOrder order : orderList) {
            String ymKey = null;
            String dispatchTime = order.getDispatchTime();
            if (StrUtil.isNotEmpty(dispatchTime)) {
                try {
                    String normalized = DateUtil.formatDate(dispatchTime);
                    String pattern = normalized.length() >= 19 ? DateUtil.YYYY_MM_DD_HH_MM_SS
                        : (normalized.length() >= 16 ? DateUtil.YYYY_MM_DD_HH_MM : DateUtil.YYYY_MM_DD);
                    Date date = DateUtil.getPointTime(normalized, pattern);
                    ymKey = DateUtil.formatDate2Str(date, DateUtil.YYYY_MM);
                } catch (Exception ex) {
                    ymKey = null;
                }
            }
            monthCountMap.put(ymKey, monthCountMap.getOrDefault(ymKey, 0L) + 1);
        }

        List<String> monthOrder = monthCountMap.keySet().stream()
            .filter(ym -> ym != null)
            .sorted()
            .collect(Collectors.toList());
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (String ym : monthOrder) {
            String monthLabel;
            if (StrUtil.isEmpty(ym)) {
                monthLabel = ym;
            } else {
                try {
                    Date ymDate = DateUtil.getPointTime(ym, DateUtil.YYYY_MM);
                    monthLabel = new SimpleDateFormat("yyyy年MM月").format(ymDate);
                } catch (Exception ex) {
                    monthLabel = ym;
                }
            }
            xAxisData.add(monthLabel);
            seriesData.add(monthCountMap.getOrDefault(ym, 0L));
        }
        long otherCount = monthCountMap.entrySet().stream()
            .filter(e -> e.getKey() == null)
            .mapToLong(Map.Entry::getValue)
            .sum();
        if (otherCount > 0) {
            xAxisData.add(OTHER_LABEL);
            seriesData.add(otherCount);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryRepairStatsByEquipmentName(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();

        long total = equipmentRepairOrderDao.selectCount(queryWrapper);

        List<EquipmentRepairOrder> orderList = equipmentRepairOrderDao.selectList(queryWrapper);
        equipmentService.setDataMation(orderList, EquipmentRepairOrder::getEquipmentId);

        Map<String, Long> nameCountMap = new HashMap<>();
        for (EquipmentRepairOrder order : orderList) {
            String equipmentName = OTHER_LABEL;
            if (StrUtil.isNotEmpty(order.getEquipmentId())) {
                Map<String, Object> equipmentMation = order.getEquipmentMation();
                if (equipmentMation != null && StrUtil.isNotEmpty(MapUtil.getStr(equipmentMation, "name"))) {
                    equipmentName = MapUtil.getStr(equipmentMation, "name");
                }
            }
            nameCountMap.put(equipmentName, nameCountMap.getOrDefault(equipmentName, 0L) + 1);
        }

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : nameCountMap.entrySet()) {
            xAxisData.add(entry.getKey());
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        equipmentRepairOrderService.queryPageList(inputObject, outputObject);
    }
}
