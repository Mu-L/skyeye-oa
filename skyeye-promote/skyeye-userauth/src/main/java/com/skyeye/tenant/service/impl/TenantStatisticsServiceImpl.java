/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.IsUsedEnum;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.*;
import com.skyeye.tenant.entity.Tenant;
import com.skyeye.tenant.entity.TenantAppBuyOrder;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.entity.TenantUserInvite;
import com.skyeye.tenant.service.TenantStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 租户统计分析服务实现：9 个统计接口，便于报表 3x3 等布局
 */
@Service
public class TenantStatisticsServiceImpl implements TenantStatisticsService {

    private static final String OTHER_LABEL = "其他";

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private TenantUserDao tenantUserDao;

    @Autowired
    private TenantAppBuyOrderDao tenantAppBuyOrderDao;

    @Autowired
    private TenantUserInviteDao tenantUserInviteDao;

    @Autowired
    private TenantAppDao tenantAppDao;

    @Override
    public void queryTenantTotal(InputObject inputObject, OutputObject outputObject) {
        long total = tenantDao.selectCount(null);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTenantUserTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantUser> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        long total = tenantUserDao.selectCount(qw);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTenantOrderTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantAppBuyOrder> qw = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime())) {
            qw.ge(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOperTime), tableSelectInfo.getStartTime());
        }
        if (StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            qw.le(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOperTime), tableSelectInfo.getEndTime());
        }
        long total = tenantAppBuyOrderDao.selectCount(qw);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTenantInviteTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantUserInvite> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        long total = tenantUserInviteDao.selectCount(qw);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTenantAppTotal(InputObject inputObject, OutputObject outputObject) {
        long total = tenantAppDao.selectCount(null);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTenantStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<Tenant> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        List<Tenant> list = tenantDao.selectList(qw);
        long total = list.size();
        // 按创建时间年月分组（createTime 格式如 2024-01-01 12:00:00，取前7位为 YYYY-MM）
        Map<String, Long> monthStats = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getCreateTime()))
            .collect(Collectors.groupingBy(
                o -> o.getCreateTime().length() >= 7 ? o.getCreateTime().substring(0, 7) : o.getCreateTime(),
                Collectors.counting()));
        List<String> xAxisData = new ArrayList<>(monthStats.keySet());
        xAxisData.sort(Comparator.naturalOrder());
        List<Long> seriesData = new ArrayList<>();
        for (String month : xAxisData) {
            seriesData.add(monthStats.get(month));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTenantUserStatsByState(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantUser> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        List<TenantUser> list = tenantUserDao.selectList(qw);
        long total = list.size();
        // state 为空归其他
        Map<String, Long> stateStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> o.getState() != null ? o.getState().toString() : OTHER_LABEL,
                Collectors.counting()));
        UserStaffState[] stateOrder = UserStaffState.values();
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (UserStaffState state : stateOrder) {
            xAxisData.add(state.getValue());
            seriesData.add(stateStats.getOrDefault(state.getKey().toString(), 0L));
        }
        if (stateStats.containsKey(OTHER_LABEL)) {
            xAxisData.add(OTHER_LABEL);
            seriesData.add(stateStats.get(OTHER_LABEL));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryTenantOrderStatsByTenant(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantAppBuyOrder> qw = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime())) {
            qw.ge(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOperTime), tableSelectInfo.getStartTime());
        }
        if (StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            qw.le(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOperTime), tableSelectInfo.getEndTime());
        }
        List<TenantAppBuyOrder> list = tenantAppBuyOrderDao.selectList(qw);
        long total = list.size();
        Map<String, Long> tenantStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getBuyTenantId()) ? o.getBuyTenantId() : OTHER_LABEL,
                Collectors.counting()));
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : tenantStats.entrySet()) {
            xAxisData.add(OTHER_LABEL.equals(entry.getKey()) ? OTHER_LABEL : entry.getKey());
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
    public void queryTenantInviteStatsByUsed(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantUserInvite> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        List<TenantUserInvite> list = tenantUserInviteDao.selectList(qw);
        long total = list.size();
        Map<String, Long> usedStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> o.getIsUsed() != null ? o.getIsUsed().toString() : OTHER_LABEL,
                Collectors.counting()));
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (IsUsedEnum e : IsUsedEnum.values()) {
            String k = e.getKey().toString();
            if (usedStats.containsKey(k)) {
                xAxisData.add(e.getValue());
                seriesData.add(usedStats.get(k));
            }
        }
        if (usedStats.containsKey(OTHER_LABEL)) {
            xAxisData.add(OTHER_LABEL);
            seriesData.add(usedStats.get(OTHER_LABEL));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private <T> QueryWrapper<T> buildTimeRangeWrapper(String startTime, String endTime) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        if (StrUtil.isEmpty(startTime) && StrUtil.isEmpty(endTime)) {
            return qw;
        }
        String column = "create_time";
        if (StrUtil.isNotEmpty(startTime)) {
            qw.ge(column, startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            qw.le(column, endTime);
        }
        return qw;
    }
}
