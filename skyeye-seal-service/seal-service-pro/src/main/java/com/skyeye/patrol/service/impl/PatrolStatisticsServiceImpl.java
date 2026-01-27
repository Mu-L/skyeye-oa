/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.patrol.classenum.PatrolItemSummaryType;
import com.skyeye.patrol.classenum.PatrolTaskState;
import com.skyeye.patrol.dao.PatrolRecordDao;
import com.skyeye.patrol.dao.PatrolTaskDao;
import com.skyeye.patrol.entity.PatrolRecord;
import com.skyeye.patrol.entity.PatrolTask;
import com.skyeye.patrol.service.PatrolPlanService;
import com.skyeye.patrol.service.PatrolStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolStatisticsServiceImpl
 * @Description: 巡检统计服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class PatrolStatisticsServiceImpl implements PatrolStatisticsService {

    @Autowired
    private PatrolTaskDao patrolTaskDao;

    @Autowired
    private PatrolRecordDao patrolRecordDao;

    @Autowired
    private PatrolPlanService patrolPlanService;

    @Override
    public void queryTaskCompletionStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        // 总数
        long total = patrolTaskDao.selectCount(queryWrapper);

        // 按状态统计
        Map<Integer, Long> stateCountMap = new HashMap<>();
        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        for (PatrolTask task : taskList) {
            Integer state = task.getState();
            stateCountMap.put(state, stateCountMap.getOrDefault(state, 0L) + 1);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pending", stateCountMap.getOrDefault(PatrolTaskState.PENDING.getKey(), 0L));
        result.put("inProgress", stateCountMap.getOrDefault(PatrolTaskState.IN_PROGRESS.getKey(), 0L));
        result.put("completed", stateCountMap.getOrDefault(PatrolTaskState.COMPLETED.getKey(), 0L));
        result.put("cancelled", stateCountMap.getOrDefault(PatrolTaskState.CANCELLED.getKey(), 0L));
        result.put("timeout", stateCountMap.getOrDefault(PatrolTaskState.TIMEOUT.getKey(), 0L));

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryAbnormalStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolRecord> queryWrapper = new QueryWrapper<>();
        // 只统计已审批通过的记录
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolRecord::getState), FlowableStateEnum.PASS.getKey());
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), endTime);
        }

        List<PatrolRecord> recordList = patrolRecordDao.selectList(queryWrapper);
        long total = recordList.size();
        long normalCount = 0;
        long abnormalCount = 0;

        for (PatrolRecord record : recordList) {
            if (PatrolItemSummaryType.NORMAL.getKey().equals(record.getCheckResult())) {
                normalCount++;
            } else if (PatrolItemSummaryType.ABNORMAL.getKey().equals(record.getCheckResult())) {
                abnormalCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("normalCount", normalCount);
        result.put("abnormalCount", abnormalCount);
        if (total > 0) {
            result.put("normalRate", CalculationUtil.divide(String.valueOf(normalCount), String.valueOf(total), CommonNumConstants.NUM_TWO));
            result.put("abnormalRate", CalculationUtil.divide(String.valueOf(abnormalCount), String.valueOf(total), CommonNumConstants.NUM_TWO));
        } else {
            result.put("normalRate", CommonNumConstants.NUM_ZERO);
            result.put("abnormalRate", CommonNumConstants.NUM_ZERO);
        }

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStatsByTeam(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        // 获取所有唯一的 planId
        Set<String> planIds = taskList.stream()
            .filter(task -> StrUtil.isNotEmpty(task.getPlanId()))
            .map(PatrolTask::getPlanId)
            .collect(Collectors.toSet());

        if (CollectionUtil.isEmpty(planIds)) {
            outputObject.setBeans(new ArrayList<>());
            outputObject.settotal(CommonNumConstants.NUM_ZERO);
            return;
        }

        // 批量查询计划信息获取 teamId
        Map<String, String> planTeamMap = new HashMap<>();
        List<com.skyeye.patrol.entity.PatrolPlan> planList = patrolPlanService.selectByIds(planIds.toArray(new String[]{}));
        for (com.skyeye.patrol.entity.PatrolPlan plan : planList) {
            if (plan != null && StrUtil.isNotEmpty(plan.getTeamId())) {
                planTeamMap.put(plan.getId(), plan.getTeamId());
            }
        }

        // 按 teamId 分组统计
        Map<String, Long> teamStats = new HashMap<>();
        for (PatrolTask task : taskList) {
            String teamId = planTeamMap.get(task.getPlanId());
            if (StrUtil.isNotEmpty(teamId)) {
                teamStats.put(teamId, teamStats.getOrDefault(teamId, 0L) + 1);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : teamStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("teamId", entry.getKey());
            item.put("taskCount", entry.getValue());
            result.add(item);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryStatsByPoint(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        Map<String, Long> pointStats = taskList.stream()
            .filter(task -> StrUtil.isNotEmpty(task.getPointId()))
            .collect(Collectors.groupingBy(PatrolTask::getPointId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : pointStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("pointId", entry.getKey());
            item.put("taskCount", entry.getValue());
            result.add(item);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryStatsByItem(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolRecord::getState), FlowableStateEnum.PASS.getKey());
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolRecord::getCreateTime), endTime);
        }

        List<PatrolRecord> recordList = patrolRecordDao.selectList(queryWrapper);
        Map<String, Long> itemStats = recordList.stream()
            .filter(record -> StrUtil.isNotEmpty(record.getItemId()))
            .collect(Collectors.groupingBy(PatrolRecord::getItemId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : itemStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("itemId", entry.getKey());
            item.put("recordCount", entry.getValue());
            result.add(item);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryStatsByExecutor(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        Map<String, Long> executorStats = taskList.stream()
            .filter(task -> StrUtil.isNotEmpty(task.getExecutorId()))
            .collect(Collectors.groupingBy(PatrolTask::getExecutorId, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : executorStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("executorId", entry.getKey());
            item.put("taskCount", entry.getValue());
            // 统计已完成任务数
            long completedCount = taskList.stream()
                .filter(task -> entry.getKey().equals(task.getExecutorId())
                    && PatrolTaskState.COMPLETED.getKey().equals(task.getState()))
                .count();
            item.put("completedCount", completedCount);
            result.add(item);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryTimeTrendStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime, endTime;
        if (StrUtil.isNotEmpty(tableSelectInfo.getStartTime()) && StrUtil.isNotEmpty(tableSelectInfo.getEndTime())) {
            startTime = tableSelectInfo.getStartTime();
            endTime = tableSelectInfo.getEndTime();
        } else {
            startTime = DateUtil.formatDate2Str(DateUtil.getAfDate(DateUtil.getPointTime(DateUtil.getYmdTimeAndToString(), DateUtil.YYYY_MM_DD), -30, "d"),
                DateUtil.YYYY_MM_DD);
            endTime = DateUtil.getYmdTimeAndToString();
        }

        List<String> dayList = DateUtil.getDays(startTime, endTime);

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime)
            .le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        Map<String, Long> taskCountMap = taskList.stream().collect(Collectors.groupingBy(task -> {
            Date pointTime = DateUtil.getPointTime(task.getCreateTime(), DateUtil.YYYY_MM_DD);
            return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
        }, Collectors.counting()));

        // 已完成任务统计
        Map<String, Long> completedCountMap = taskList.stream()
            .filter(task -> PatrolTaskState.COMPLETED.getKey().equals(task.getState()))
            .collect(Collectors.groupingBy(task -> {
                Date pointTime = DateUtil.getPointTime(task.getCreateTime(), DateUtil.YYYY_MM_DD);
                return DateUtil.formatDate2Str(pointTime, DateUtil.YYYY_MM_DD);
            }, Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        List<Long> allTasks = new ArrayList<>();
        List<Long> completedTasks = new ArrayList<>();
        Long defaultValue = Long.valueOf(CommonNumConstants.NUM_ZERO);

        for (String day : dayList) {
            allTasks.add(taskCountMap.getOrDefault(day, defaultValue));
            completedTasks.add(completedCountMap.getOrDefault(day, defaultValue));
        }

        result.put("allTasks", allTasks);
        result.put("completedTasks", completedTasks);
        result.put("dayList", dayList);

        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        TableSelectInfo tableSelectInfo = inputObject.getParams(TableSelectInfo.class);
        String startTime = tableSelectInfo.getStartTime();
        String endTime = tableSelectInfo.getEndTime();

        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(startTime)) {
            queryWrapper.ge(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(PatrolTask::getCreateTime), endTime);
        }

        List<PatrolTask> taskList = patrolTaskDao.selectList(queryWrapper);
        long total = taskList.size();
        long completed = taskList.stream()
            .filter(task -> PatrolTaskState.COMPLETED.getKey().equals(task.getState()))
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

}

