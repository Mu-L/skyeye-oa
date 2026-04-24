/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.personnel.dao.SysEveUserOperLogDao;
import com.skyeye.personnel.entity.SysEveUserOperLog;
import com.skyeye.personnel.service.SysEveUserOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 系统操作日志（租户强隔离）
 */
@Slf4j
@Service
@SkyeyeService(name = "系统操作日志", groupName = "用户管理", tenant = TenantEnum.NO_ISOLATION, allowDynamicAttrKey = false)
public class SysEveUserOperLogServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserOperLogDao, SysEveUserOperLog> implements SysEveUserOperLogService {

    private static final DateTimeFormatter OPER_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD);
    private static final int DEFAULT_STAT_DAYS = 7;
    private static final int DEFAULT_TOP_N = 10;
    private static final int MAX_TOP_N = 100;

    @Override
    @IgnoreTenant
    public int cleanExpiredOperationLogs(int batchSize, int retainMonths) {
        int actualBatchSize = Math.max(batchSize, 100);
        int actualRetainMonths = Math.max(retainMonths, 1);
        String cutoffTime = LocalDateTime.now().minusMonths(actualRetainMonths).format(OPER_TIME_FORMATTER);
        String idColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getId);
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(idColumn)
            .lt(operTimeColumn, cutoffTime)
            .orderByAsc(operTimeColumn, idColumn)
            .last("limit " + actualBatchSize);
        List<Object> idList = baseMapper.selectObjs(queryWrapper);
        if (idList == null || idList.isEmpty()) {
            return 0;
        }
        boolean removed = removeByIds(idList);
        if (!removed) {
            log.warn("清理过期操作日志未删除到数据，cutoffTime: {}", cutoffTime);
            return 0;
        }
        log.info("清理过期操作日志完成，保留最近{}个月，本批删除数量：{}", actualRetainMonths, idList.size());
        return idList.size();
    }

    /**
     * 概览统计：
     * - 输入：startTime/endTime（可选），为空时默认最近7天
     * - 输出：totalCount/successCount/failCount/successRate/avgCostMs/todayCount
     */
    @Override
    @IgnoreTenant
    public void queryOperLogOverviewStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 统计时间窗口：前端仅传 startTime/endTime；缺失时默认最近7天
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String todayStart = LocalDateTime.now().toLocalDate().atStartOfDay().format(OPER_TIME_FORMATTER);
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String returnCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getReturnCode);
        String costMsColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getCostMs);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "count(1) as totalCount",
                "sum(case when " + returnCodeColumn + " = 0 then 1 else 0 end) as successCount",
                "sum(case when " + returnCodeColumn + " <> 0 then 1 else 0 end) as failCount",
                "avg(" + costMsColumn + ") as avgCostMs",
                "sum(case when " + operTimeColumn + " >= '" + todayStart + "' then 1 else 0 end) as todayCount")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime);
        Map<String, Object> dbRow = baseMapper.selectMaps(queryWrapper).stream().findFirst().orElse(new LinkedHashMap<>());
        long totalCount = parseLong(dbRow.get("totalCount"));
        long successCount = parseLong(dbRow.get("successCount"));
        long failCount = parseLong(dbRow.get("failCount"));
        double avgCostMs = parseDouble(dbRow.get("avgCostMs"));
        double successRate = totalCount == 0 ? 0D : (successCount * 100D / totalCount);
        Map<String, Object> result = new LinkedHashMap<>();
        // startTime: 统计开始时间
        result.put("startTime", fromTime);
        // endTime: 统计结束时间
        result.put("endTime", endTime);
        // totalCount: 时间窗口内日志总数
        result.put("totalCount", totalCount);
        // successCount: 返回码为0的成功日志数量
        result.put("successCount", successCount);
        // failCount: 返回码非0的失败日志数量
        result.put("failCount", failCount);
        // todayCount: 今日00:00:00至当前时间的日志数量
        result.put("todayCount", parseLong(dbRow.get("todayCount")));
        // avgCostMs: 平均接口耗时（毫秒）
        result.put("avgCostMs", avgCostMs);
        // successRate: 成功率（百分比，保留两位小数）
        result.put("successRate", Math.round(successRate * 100D) / 100D);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 趋势统计（按天）：
     * - 输入：startTime/endTime（可选），为空时默认最近7天
     * - 输出：xAxisData(日期)、seriesData(总量)、successSeriesData、failSeriesData
     */
    @Override
    @IgnoreTenant
    public void queryOperLogTrendStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String fromDate = fromTime.substring(0, 10);
        String toDate = endTime.substring(0, 10);
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String returnCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getReturnCode);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "substr(" + operTimeColumn + ", 1, 10) as statDate",
                "count(1) as totalCount",
                "sum(case when " + returnCodeColumn + " = 0 then 1 else 0 end) as successCount",
                "sum(case when " + returnCodeColumn + " <> 0 then 1 else 0 end) as failCount")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime)
            .groupBy("substr(" + operTimeColumn + ", 1, 10)")
            .orderByAsc("statDate");
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        Map<String, Map<String, Object>> dayMap = new HashMap<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                dayMap.put(String.valueOf(row.get("statDate")), row);
            }
        }
        List<String> dayList = DateUtil.getDays(fromDate, toDate);
        List<Long> totalSeriesData = new ArrayList<>();
        List<Long> successSeriesData = new ArrayList<>();
        List<Long> failSeriesData = new ArrayList<>();
        long total = 0L;
        for (String day : dayList) {
            Map<String, Object> row = dayMap.get(day);
            long totalCount = row == null ? 0L : parseLong(row.get("totalCount"));
            long successCount = row == null ? 0L : parseLong(row.get("successCount"));
            long failCount = row == null ? 0L : parseLong(row.get("failCount"));
            total += totalCount;
            totalSeriesData.add(totalCount);
            successSeriesData.add(successCount);
            failSeriesData.add(failCount);
        }
        Map<String, Object> result = new HashMap<>();
        // total: 当前趋势窗口内日志总量
        result.put("total", total);
        // xAxisData: 横轴日期列表（yyyy-MM-dd）
        result.put("xAxisData", dayList);
        // seriesData: 每日日志总量序列（与xAxisData一一对应）
        result.put("seriesData", totalSeriesData);
        // successSeriesData: 每日成功数量序列
        result.put("successSeriesData", successSeriesData);
        // failSeriesData: 每日失败数量序列
        result.put("failSeriesData", failSeriesData);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * Top接口统计（按接口ID聚合）：
     * - 输入：startTime/endTime（可选）、topN（可选，默认10）
     * - 输出：xAxisData(接口名)、seriesData(调用量)、successSeriesData、failSeriesData、detailList
     */
    @Override
    @IgnoreTenant
    public void queryOperLogTopApiStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // topN: 返回前N个接口，默认10，范围[1, 100]
        int topN = parseInt(params.get("topN"), DEFAULT_TOP_N, 1, MAX_TOP_N);
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String returnCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getReturnCode);
        String apiIdColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getApiId);
        String apiNameColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getApiName);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                apiIdColumn + " as apiId",
                "max(" + apiNameColumn + ") as apiName",
                "count(1) as totalCount",
                "sum(case when " + returnCodeColumn + " = 0 then 1 else 0 end) as successCount",
                "sum(case when " + returnCodeColumn + " <> 0 then 1 else 0 end) as failCount")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime)
            .isNotNull(apiIdColumn)
            .groupBy(apiIdColumn)
            .orderByDesc("totalCount")
            .last("limit " + topN);
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        List<Long> successSeriesData = new ArrayList<>();
        List<Long> failSeriesData = new ArrayList<>();
        long total = 0L;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String apiName = String.valueOf(row.get("apiName"));
                if (StrUtil.isBlank(apiName) || "null".equalsIgnoreCase(apiName)) {
                    apiName = String.valueOf(row.get("apiId"));
                }
                long totalCount = parseLong(row.get("totalCount"));
                long successCount = parseLong(row.get("successCount"));
                long failCount = parseLong(row.get("failCount"));
                total += totalCount;
                xAxisData.add(apiName);
                seriesData.add(totalCount);
                successSeriesData.add(successCount);
                failSeriesData.add(failCount);
            }
        }
        Map<String, Object> result = new HashMap<>();
        // total: Top列表中的调用总量（非全量日志总量）
        result.put("total", total);
        // xAxisData: Top接口名称列表（接口名为空时回退apiId）
        result.put("xAxisData", xAxisData);
        // seriesData: Top接口总调用量序列
        result.put("seriesData", seriesData);
        // successSeriesData: Top接口成功调用量序列
        result.put("successSeriesData", successSeriesData);
        // failSeriesData: Top接口失败调用量序列
        result.put("failSeriesData", failSeriesData);
        // detailList: 明细聚合结果（apiId/apiName/totalCount/successCount/failCount）
        result.put("detailList", rows);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * Top请求路径统计（按requestPath聚合）：
     * - 输入：startTime/endTime（可选）、topN（可选，默认10）
     * - 输出：xAxisData(请求路径)、seriesData(调用量)、successSeriesData、failSeriesData、detailList
     */
    @Override
    @IgnoreTenant
    public void queryOperLogTopPathStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // startTime/endTime: 统计时间范围（不传则默认最近7天）
        // topN: 返回前N条路径，默认10，范围[1, 100]
        int topN = parseInt(params.get("topN"), DEFAULT_TOP_N, 1, MAX_TOP_N);
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String returnCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getReturnCode);
        String requestPathColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getRequestPath);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                requestPathColumn + " as requestPath",
                "count(1) as totalCount",
                "sum(case when " + returnCodeColumn + " = 0 then 1 else 0 end) as successCount",
                "sum(case when " + returnCodeColumn + " <> 0 then 1 else 0 end) as failCount")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime)
            .isNotNull(requestPathColumn)
            .groupBy(requestPathColumn)
            .orderByDesc("totalCount")
            .last("limit " + topN);
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        List<Long> successSeriesData = new ArrayList<>();
        List<Long> failSeriesData = new ArrayList<>();
        long total = 0L;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String requestPath = String.valueOf(row.get("requestPath"));
                if (StrUtil.isBlank(requestPath) || "null".equalsIgnoreCase(requestPath)) {
                    requestPath = "其他";
                }
                long totalCount = parseLong(row.get("totalCount"));
                long successCount = parseLong(row.get("successCount"));
                long failCount = parseLong(row.get("failCount"));
                total += totalCount;
                xAxisData.add(requestPath);
                seriesData.add(totalCount);
                successSeriesData.add(successCount);
                failSeriesData.add(failCount);
            }
        }
        Map<String, Object> result = new HashMap<>();
        // total: Top列表中的调用总量（非全量日志总量）
        result.put("total", total);
        // xAxisData: Top请求路径列表
        result.put("xAxisData", xAxisData);
        // seriesData: Top请求路径总调用量序列
        result.put("seriesData", seriesData);
        // successSeriesData: Top请求路径成功调用量序列
        result.put("successSeriesData", successSeriesData);
        // failSeriesData: Top请求路径失败调用量序列
        result.put("failSeriesData", failSeriesData);
        // detailList: 明细聚合结果（requestPath/totalCount/successCount/failCount）
        result.put("detailList", rows);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * Top访问人统计（按operUserId聚合）：
     * - 输入：startTime/endTime（可选）、topN（可选，默认10）
     * - 输出：xAxisData(操作人)、seriesData(调用量)、successSeriesData、failSeriesData、detailList
     */
    @Override
    @IgnoreTenant
    public void queryOperLogTopUserStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // startTime/endTime: 统计时间范围（不传则默认最近7天）
        // topN: 返回前N个访问人，默认10，范围[1, 100]
        int topN = parseInt(params.get("topN"), DEFAULT_TOP_N, 1, MAX_TOP_N);
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String returnCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getReturnCode);
        String userIdColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperUserId);
        String userNameColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperUserName);
        String userCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperUserCode);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                userIdColumn + " as operUserId",
                "max(" + userNameColumn + ") as operUserName",
                "max(" + userCodeColumn + ") as operUserCode",
                "count(1) as totalCount",
                "sum(case when " + returnCodeColumn + " = 0 then 1 else 0 end) as successCount",
                "sum(case when " + returnCodeColumn + " <> 0 then 1 else 0 end) as failCount")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime)
            .groupBy(userIdColumn)
            .orderByDesc("totalCount")
            .last("limit " + topN);
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        List<Long> successSeriesData = new ArrayList<>();
        List<Long> failSeriesData = new ArrayList<>();
        long total = 0L;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String label = String.valueOf(row.get("operUserName"));
                if (StrUtil.isBlank(label) || "null".equalsIgnoreCase(label)) {
                    label = String.valueOf(row.get("operUserCode"));
                }
                if (StrUtil.isBlank(label) || "null".equalsIgnoreCase(label)) {
                    label = "未识别用户";
                }
                long totalCount = parseLong(row.get("totalCount"));
                long successCount = parseLong(row.get("successCount"));
                long failCount = parseLong(row.get("failCount"));
                total += totalCount;
                xAxisData.add(label);
                seriesData.add(totalCount);
                successSeriesData.add(successCount);
                failSeriesData.add(failCount);
            }
        }
        Map<String, Object> result = new HashMap<>();
        // total: Top列表中的调用总量（非全量日志总量）
        result.put("total", total);
        // xAxisData: Top访问人名称列表（优先姓名，次选账号）
        result.put("xAxisData", xAxisData);
        // seriesData: Top访问人总调用量序列
        result.put("seriesData", seriesData);
        // successSeriesData: Top访问人成功调用量序列
        result.put("successSeriesData", successSeriesData);
        // failSeriesData: Top访问人失败调用量序列
        result.put("failSeriesData", failSeriesData);
        // detailList: 明细聚合结果（operUserId/operUserName/operUserCode/totalCount/successCount/failCount）
        result.put("detailList", rows);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * Top来源服务统计（按sourceService聚合）：
     * - 输入：startTime/endTime（可选）、topN（可选，默认10）
     * - 输出：xAxisData(来源服务)、seriesData(调用量)、successSeriesData、failSeriesData、detailList
     */
    @Override
    @IgnoreTenant
    public void queryOperLogTopSourceServiceStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // startTime/endTime: 统计时间范围（不传则默认最近7天）
        // topN: 返回前N个来源服务，默认10，范围[1, 100]
        int topN = parseInt(params.get("topN"), DEFAULT_TOP_N, 1, MAX_TOP_N);
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String returnCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getReturnCode);
        String sourceServiceColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getSourceService);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                sourceServiceColumn + " as sourceService",
                "count(1) as totalCount",
                "sum(case when " + returnCodeColumn + " = 0 then 1 else 0 end) as successCount",
                "sum(case when " + returnCodeColumn + " <> 0 then 1 else 0 end) as failCount")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime)
            .groupBy(sourceServiceColumn)
            .orderByDesc("totalCount")
            .last("limit " + topN);
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        List<Long> successSeriesData = new ArrayList<>();
        List<Long> failSeriesData = new ArrayList<>();
        long total = 0L;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String sourceService = String.valueOf(row.get("sourceService"));
                if (StrUtil.isBlank(sourceService) || "null".equalsIgnoreCase(sourceService)) {
                    sourceService = "其他";
                }
                long totalCount = parseLong(row.get("totalCount"));
                long successCount = parseLong(row.get("successCount"));
                long failCount = parseLong(row.get("failCount"));
                total += totalCount;
                xAxisData.add(sourceService);
                seriesData.add(totalCount);
                successSeriesData.add(successCount);
                failSeriesData.add(failCount);
            }
        }
        Map<String, Object> result = new HashMap<>();
        // total: Top列表中的调用总量（非全量日志总量）
        result.put("total", total);
        // xAxisData: Top来源服务名称列表
        result.put("xAxisData", xAxisData);
        // seriesData: Top来源服务总调用量序列
        result.put("seriesData", seriesData);
        // successSeriesData: Top来源服务成功调用量序列
        result.put("successSeriesData", successSeriesData);
        // failSeriesData: Top来源服务失败调用量序列
        result.put("failSeriesData", failSeriesData);
        // detailList: 明细聚合结果（sourceService/totalCount/successCount/failCount）
        result.put("detailList", rows);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 失败最多接口统计（按接口ID聚合，按失败数倒序）：
     * - 输入：startTime/endTime（可选）、topN（可选，默认10）
     * - 输出：xAxisData(接口名)、seriesData(失败量)、detailList
     */
    @Override
    @IgnoreTenant
    public void queryOperLogTopFailApiStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // startTime/endTime: 统计时间范围（不传则默认最近7天）
        int topN = parseInt(params.get("topN"), DEFAULT_TOP_N, 1, MAX_TOP_N);
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String returnCodeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getReturnCode);
        String apiIdColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getApiId);
        String apiNameColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getApiName);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                apiIdColumn + " as apiId",
                "max(" + apiNameColumn + ") as apiName",
                "count(1) as totalCount",
                "sum(case when " + returnCodeColumn + " <> 0 then 1 else 0 end) as failCount")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime)
            .isNotNull(apiIdColumn)
            .groupBy(apiIdColumn)
            .orderByDesc("failCount", "totalCount")
            .last("limit " + topN);
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        long total = 0L;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String apiName = String.valueOf(row.get("apiName"));
                if (StrUtil.isBlank(apiName) || "null".equalsIgnoreCase(apiName)) {
                    apiName = String.valueOf(row.get("apiId"));
                }
                long failCount = parseLong(row.get("failCount"));
                total += failCount;
                xAxisData.add(apiName);
                seriesData.add(failCount);
            }
        }
        Map<String, Object> result = new HashMap<>();
        // total: Top列表中的失败调用总量（非全量日志失败总量）
        result.put("total", total);
        // xAxisData: 失败最多接口名称列表（接口名为空时回退apiId）
        result.put("xAxisData", xAxisData);
        // seriesData: 失败调用量序列
        result.put("seriesData", seriesData);
        // detailList: 明细聚合结果（apiId/apiName/totalCount/failCount）
        result.put("detailList", rows);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 慢接口统计（按接口ID聚合，按平均耗时倒序）：
     * - 输入：startTime/endTime（可选）、topN（可选，默认10）
     * - 输出：xAxisData(接口名)、seriesData(平均耗时ms)、detailList
     */
    @Override
    @IgnoreTenant
    public void queryOperLogTopSlowApiStat(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // startTime/endTime: 统计时间范围（不传则默认最近7天）
        int topN = parseInt(params.get("topN"), DEFAULT_TOP_N, 1, MAX_TOP_N);
        String[] range = resolveStatTimeRange(params);
        String fromTime = range[0];
        String endTime = range[1];
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        String apiIdColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getApiId);
        String apiNameColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getApiName);
        String costMsColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getCostMs);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                apiIdColumn + " as apiId",
                "max(" + apiNameColumn + ") as apiName",
                "count(1) as totalCount",
                "avg(" + costMsColumn + ") as avgCostMs",
                "max(" + costMsColumn + ") as maxCostMs")
            .ge(operTimeColumn, fromTime)
            .le(operTimeColumn, endTime)
            .isNotNull(apiIdColumn)
            .groupBy(apiIdColumn)
            .orderByDesc("avgCostMs")
            .last("limit " + topN);
        List<Map<String, Object>> rows = baseMapper.selectMaps(queryWrapper);
        List<String> xAxisData = new ArrayList<>();
        List<Long> seriesData = new ArrayList<>();
        long total = 0L;
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String apiName = String.valueOf(row.get("apiName"));
                if (StrUtil.isBlank(apiName) || "null".equalsIgnoreCase(apiName)) {
                    apiName = String.valueOf(row.get("apiId"));
                }
                long avgCost = Math.round(parseDouble(row.get("avgCostMs")));
                total += avgCost;
                xAxisData.add(apiName);
                seriesData.add(avgCost);
            }
        }
        Map<String, Object> result = new HashMap<>();
        // total: Top列表中平均耗时之和（用于页面总览）
        result.put("total", total);
        // xAxisData: 慢接口名称列表（接口名为空时回退apiId）
        result.put("xAxisData", xAxisData);
        // seriesData: 平均耗时序列（单位ms，已四舍五入为long）
        result.put("seriesData", seriesData);
        // detailList: 明细聚合结果（apiId/apiName/totalCount/avgCostMs/maxCostMs）
        result.put("detailList", rows);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    protected QueryWrapper<SysEveUserOperLog> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SysEveUserOperLog> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime));
        return queryWrapper;
    }

    private static int parseInt(Object val, int defaultVal, int minVal, int maxVal) {
        if (val == null) {
            return defaultVal;
        }
        try {
            int v = Integer.parseInt(String.valueOf(val));
            if (v < minVal) {
                return minVal;
            }
            return Math.min(v, maxVal);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private static long parseLong(Object val) {
        if (val == null) {
            return 0L;
        }
        try {
            return Long.parseLong(String.valueOf(val));
        } catch (Exception e) {
            return 0L;
        }
    }

    private static double parseDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.parseDouble(String.valueOf(val));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 统计时间窗口：优先使用前端传入 startTime/endTime，缺失时默认最近7天。
     */
    private String[] resolveStatTimeRange(Map<String, Object> params) {
        String startTime = params == null ? null : str(params.get("startTime"));
        String endTime = params == null ? null : str(params.get("endTime"));
        if (StrUtil.isBlank(startTime) || StrUtil.isBlank(endTime)) {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(DEFAULT_STAT_DAYS - 1L).toLocalDate().atStartOfDay();
            return new String[]{start.format(OPER_TIME_FORMATTER), end.format(OPER_TIME_FORMATTER)};
        }
        if (startTime.length() == 10) {
            startTime = startTime + " 00:00:00";
        }
        if (endTime.length() == 10) {
            endTime = endTime + " 23:59:59";
        }
        return new String[]{startTime, endTime};
    }

    private static String str(Object val) {
        return val == null ? null : String.valueOf(val);
    }
}
