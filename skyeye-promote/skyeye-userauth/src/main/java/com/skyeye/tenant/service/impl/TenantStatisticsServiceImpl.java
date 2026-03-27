/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.tenant.IgnoreTenant;
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
 * 租户统计分析服务实现。
 * <p>
 * 提供运营侧/管理端用的租户维度统计；方法均标注 {@link IgnoreTenant}，
 * 查询时不受当前登录租户的数据隔离限制，统计全平台数据。
 * </p>
 * <p>
 * 入参统一可为 {@link TableSelectInfo}（起止时间等），其中无时间条件的接口会忽略时间参数。
 * 出参通常将统计结果放入 {@code OutputObject.bean}：
 * </p>
 * <ul>
 *   <li>仅合计类接口：{@code total}</li>
 *   <li>图表类接口：{@code total}、{@code xAxisData}（横轴文案）、{@code seriesData}（与横轴一一对应的数量）</li>
 * </ul>
 *
 * @author skyeye云系列--卫志强
 */
@Service
public class TenantStatisticsServiceImpl implements TenantStatisticsService {

    /**
     * 分组时用于表示空值、无法归类项的横轴标签（如缺失租户 ID、缺失枚举状态等）
     */
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

    /**
     * 租户总数量（全表计数，不区分时间）。
     */
    @Override
    @IgnoreTenant
    public void queryTenantTotal(InputObject inputObject, OutputObject outputObject) {
        long total = tenantDao.selectCount(null);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 租户用户总数量，按 {@code tenant_user.create_time} 与入参起止时间过滤（闭区间）。
     */
    @Override
    @IgnoreTenant
    public void queryTenantUserTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantUser> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        long total = tenantUserDao.selectCount(qw);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 租户应用购买订单总数量，按订单 {@code operTime} 与入参起止时间过滤。
     * 注：时间字段与 {@link #buildTimeRangeWrapper} 使用的 {@code create_time} 不同，此处单独拼条件。
     */
    @Override
    @IgnoreTenant
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

    /**
     * 租户用户邀请记录总数量，按邀请表 {@code create_time} 与入参起止时间过滤。
     */
    @Override
    @IgnoreTenant
    public void queryTenantInviteTotal(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<TenantUserInvite> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        long total = tenantUserInviteDao.selectCount(qw);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 平台侧租户应用（{@code tenant_app}）总数量，全表计数。
     */
    @Override
    @IgnoreTenant
    public void queryTenantAppTotal(InputObject inputObject, OutputObject outputObject) {
        long total = tenantAppDao.selectCount(null);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 新增租户按创建日期分布：在时间范围内查出租户列表后，按 {@code createTime} 的日历日（YYYY-MM-DD）分组计数。
     * 横轴为日期字符串，纵轴为当日新建租户数；{@code total} 为列表条数（与分组求和一致）。
     */
    @Override
    @IgnoreTenant
    public void queryTenantStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        QueryWrapper<Tenant> qw = buildTimeRangeWrapper(tableSelectInfo.getStartTime(), tableSelectInfo.getEndTime());
        List<Tenant> list = tenantDao.selectList(qw);
        long total = list.size();
        // 按创建时间年月日分组（createTime 格式如 2024-01-01 12:00:00，取前10位为 YYYY-MM-DD）
        Map<String, Long> dayStats = list.stream()
            .filter(o -> StrUtil.isNotEmpty(o.getCreateTime()))
            .collect(Collectors.groupingBy(
                o -> o.getCreateTime().length() >= 10 ? o.getCreateTime().substring(0, 10) : o.getCreateTime(),
                Collectors.counting()));
        List<String> xAxisData = new ArrayList<>(dayStats.keySet());
        xAxisData.sort(Comparator.naturalOrder());
        List<Long> seriesData = new ArrayList<>();
        for (String day : xAxisData) {
            seriesData.add(dayStats.get(day));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 租户用户按在职/用户状态分布：先按时间筛选 {@link TenantUser}，再按 {@code state} 分组计数。
     * 横轴顺序与 {@link UserStaffState} 枚举定义一致，最后若存在 state 为空的记录则追加「其他」。
     */
    @Override
    @IgnoreTenant
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

    /**
     * 购买订单按「购买租户」维度统计，供柱状图/折线图等展示。
     * <p>
     * <b>统计口径：</b>在可选的时间范围内（按订单 {@link TenantAppBuyOrder#getOperTime() operTime} 过滤），
     * 每一条 {@link TenantAppBuyOrder} 计入其 {@code buyTenantId} 对应租户；{@code buyTenantId} 为空则计入 {@link #OTHER_LABEL}。
     * </p>
     * <p>
     * <b>横轴（xAxisData）：</b>展示租户「名称」。分组键仍为租户 ID，随后批量查询 {@link Tenant} 得到 {@code id → name}；
     * 若名称为空、或库里已无该租户记录，则横轴退回显示该租户 ID，避免丢数。
     * </p>
     * <p>
     * <b>排序：</b>按横轴展示文案（名称或 ID、「其他」）字典序，便于图表阅读；与业务重要性无关。
     * </p>
     * <p>
     * <b>返回结构：</b>{@code total} 为订单条数；{@code seriesData} 与 {@code xAxisData} 下标一一对应，为各租户（及其「其他」）的订单数。
     * </p>
     *
     * @param inputObject  入参包装，内嵌 {@link TableSelectInfo}（{@code startTime}/{@code endTime} 可选，过滤 operTime）
     * @param outputObject 出参：{@code bean} 含 {@code total}、{@code xAxisData}、{@code seriesData}
     */
    @Override
    @IgnoreTenant
    public void queryTenantOrderStatsByTenant(InputObject inputObject, OutputObject outputObject) {
        // 解析通用表格/统计查询条件（起止时间由前端传入，均可为空表示不限制）
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);

        // 订单表用 operTime 作为业务时间，与 Tenant/TenantUser 等使用的 create_time 不同，此处单独拼 Wrapper
        QueryWrapper<TenantAppBuyOrder> qw = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime())) {
            qw.ge(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOperTime), tableSelectInfo.getStartTime());
        }
        if (StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            qw.le(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getOperTime), tableSelectInfo.getEndTime());
        }

        // 拉取范围内全部订单，在内存中按租户分组（量级过大时可改为 SQL group by + 再查名称）
        List<TenantAppBuyOrder> list = tenantAppBuyOrderDao.selectList(qw);
        long total = list.size();

        // Map 的 key：真实租户 ID；特殊桶 OTHER_LABEL：buyTenantId 为空的订单
        Map<String, Long> tenantStats = list.stream()
            .collect(Collectors.groupingBy(
                o -> StrUtil.isNotEmpty(o.getBuyTenantId()) ? o.getBuyTenantId() : OTHER_LABEL,
                Collectors.counting()));

        // 仅真实租户 ID 需要反查名称；「其他」桶不参与 tenant 表查询
        List<String> tenantIds = tenantStats.keySet().stream()
            .filter(id -> !OTHER_LABEL.equals(id))
            .collect(Collectors.toList());

        Map<String, String> idToName = new HashMap<>();
        if (!tenantIds.isEmpty()) {
            List<Tenant> tenants = tenantDao.selectBatchIds(tenantIds);
            if (tenants != null) {
                for (Tenant t : tenants) {
                    // 租户名缺失时用 ID 占位，保证图表始终有可读标签
                    String name = StrUtil.isNotEmpty(t.getName()) ? t.getName() : t.getId();
                    idToName.put(t.getId(), name);
                }
            }
            // 若 selectBatchIds 未返回某 ID（数据孤立），后续 getOrDefault(tenantId, tenantId) 仍会用 ID 作为横轴
        }

        // 将分组结果转为有序列表：按「展示名」排序，避免 HashMap 遍历顺序不稳定导致图表抖动
        List<Map.Entry<String, Long>> entries = new ArrayList<>(tenantStats.entrySet());
        entries.sort(Comparator.comparing(e -> {
            String key = e.getKey();
            if (OTHER_LABEL.equals(key)) {
                return OTHER_LABEL;
            }
            return idToName.getOrDefault(key, key);
        }));

        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : entries) {
            String tenantId = entry.getKey();
            if (OTHER_LABEL.equals(tenantId)) {
                xAxisData.add(OTHER_LABEL);
            } else {
                // idToName 无记录时退回 tenantId（例如租户已删、脏数据）
                xAxisData.add(idToName.getOrDefault(tenantId, tenantId));
            }
            seriesData.add(entry.getValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("xAxisData", xAxisData);
        result.put("seriesData", seriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 租户邀请按是否已使用（{@link IsUsedEnum}）分布：横轴为枚举展示名，仅包含有数据的取值；
     * 若存在 {@code isUsed} 为空的记录则在末尾追加「其他」。
     */
    @Override
    @IgnoreTenant
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

    /**
     * 按 {@code create_time} 构建起止时间条件（闭区间）；起止均为空时不加任何条件。
     * <p>
     * 用于 {@link Tenant}、{@link TenantUser}、{@link TenantUserInvite} 等带标准创建时间字段的实体统计。
     * 订单统计请使用 {@link TenantAppBuyOrder#getOperTime()}，勿与此处混用。
     * </p>
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param <T>       实体类型（仅用于 QueryWrapper 泛型）
     */
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
