/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.aps.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.aps.entity.AllocResult;
import com.skyeye.aps.entity.ApsScheduleParam;
import com.skyeye.aps.entity.ApsScheduleSaveParam;
import com.skyeye.aps.entity.SchedulableTask;
import com.skyeye.aps.service.ApsScheduleService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.entity.MachinChild;
import com.skyeye.machin.service.MachinService;
import com.skyeye.machinprocedure.classenum.MachinProcedureFarmState;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import com.skyeye.procedure.entity.WayProcedureChild;
import com.skyeye.procedure.service.WayProcedureChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * APS排程服务实现
 * <p>
 * <b>一、什么是APS（高级计划与排程）？</b><br>
 * APS（Advanced Planning and Scheduling，高级计划与排程）是制造执行系统(MES)和ERP中的核心模块，
 * 用于在有限产能约束下，自动为生产任务分配具体的计划开始时间、计划结束时间，实现生产计划的精细化排产。
 * 区别于传统的粗放式计划，APS会考虑：车间产能、工序顺序、交货期、设备/人员可用性等约束，
 * 输出可执行的、时间精确到分钟级的生产排程方案。
 * </p>
 * <p>
 * <b>二、本系统APS的核心流程：</b><br>
 * 1. 加载待排程任务：筛选状态为「待接收」「待执行」的车间任务(MachinProcedureFarm)<br>
 * 2. 加载关联数据：加工单(Machin)、工序(MachinProcedure)、工艺路线(WayProcedureChild)、车间产能日历(FarmCalendar)<br>
 * 3. 计算工序时长：目标数量 × 标准工时(分钟/件) = 所需分钟数<br>
 * 4. 按交货期、工序顺序排序任务<br>
 * 5. 逐个分配产能：在车间每日可用工时内，从前到后占用时间槽，支持跨天、跨多天<br>
 * 6. 计算接口仅返回结果供界面展示，不保存；用户微调后调用 saveSchedule 保存
 * </p>
 * <p>
 * <b>三、支持的能力：</b><br>
 * - 多加工单、多工序并行排程<br>
 * - 工序依赖：前置工序完成后，后序工序才能开始(respectProcedureOrder)<br>
 * - 车间产能日历：按日期/星期/日期区间配置每日可用工时，支持节假日、加班等<br>
 * - 标准工时：工艺路线工序的标准工时(分钟/件)，用于计算任务时长<br>
 * - 排程范围限制：scheduleEndDate 限制排程不超出指定日期
 * </p>
 * <p>
 * <b>四、核心数据模型：</b><br>
 * - Machin：加工单（生产计划单）<br>
 * - MachinChild：加工单子单据（具体产品、数量、交货期）<br>
 * - MachinProcedure：加工单工序（工序顺序、工艺路线、计划时间）<br>
 * - MachinProcedureFarm：车间任务（分配到某车间的工序任务、目标数量）<br>
 * - WayProcedureChild：工艺路线工序（标准工时 standardTimeMinutes）<br>
 * - FarmCalendar：车间产能日历（某日可用工时）
 * </p>
 *
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 */
@Service
public class ApsScheduleServiceImpl implements ApsScheduleService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);
    /**
     * 默认每日工作开始时间(分钟，从0:00算)，如8:00=480
     */
    private static final int DEFAULT_WORK_START_MIN = 8 * 60;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Autowired
    private MachinService machinService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private WayProcedureChildService wayProcedureChildService;

    @Override
    public void schedule(InputObject inputObject, OutputObject outputObject) {
        ApsScheduleParam param = inputObject.getParams(ApsScheduleParam.class);
        if (StrUtil.isEmpty(param.getScheduleStartDate())) {
            throw new CustomException("排程开始日期不能为空");
        }

        // 1) 加载待排程车间任务
        List<MachinProcedureFarm> farmTasks = loadPendingFarmTasks(param);
        if (CollectionUtil.isEmpty(farmTasks)) {
            return;
        }

        // 2) 加载加工单、工序、工艺
        List<String> machinIds = farmTasks.stream().map(MachinProcedureFarm::getMachinId).distinct().collect(Collectors.toList());
        Map<String, Machin> machinMap = machinService.selectByIds(machinIds.toArray(new String[0])).stream()
            .collect(Collectors.toMap(Machin::getId, m -> m));
        Map<String, Map<String, List<MachinProcedureFarm>>> farmMapByMachin = machinProcedureFarmService.queryMachinProcedureFarmMapByMachinIds(machinIds);
        Map<String, Map<String, MachinProcedure>> procedureMapByMachin = machinProcedureService.queryMachinProcedureMapByMachinIds(machinIds);

        // 3) 获取标准工时
        Set<String> wayIds = new HashSet<>();
        for (Map<String, MachinProcedure> pm : procedureMapByMachin.values()) {
            for (MachinProcedure mp : pm.values()) {
                if (StrUtil.isNotEmpty(mp.getWayProcedureId())) {
                    wayIds.add(mp.getWayProcedureId());
                }
            }
        }
        Map<String, List<WayProcedureChild>> wayProcedureMap = wayProcedureChildService.queryWayProcedureByWayId(new ArrayList<>(wayIds));

        // 4) 构建可排程任务列表(按交货期、工序顺序)
        List<SchedulableTask> tasks = buildSchedulableTasks(farmTasks, machinMap, farmMapByMachin, procedureMapByMachin, wayProcedureMap, param);
        if (CollectionUtil.isEmpty(tasks)) {
            throw new CustomException("无有效排程任务(缺少标准工时等)");
        }

        // 5) 车间产能占用：farmId -> dateStr -> 已占用分钟数
        Map<String, Map<String, Integer>> capacityRemain = new HashMap<>();
        Set<String> farmIdSet = tasks.stream().map(SchedulableTask::getFarmId).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(param.getFarmIds())) {
            farmIdSet.retainAll(param.getFarmIds());
        }
        for (String fid : farmIdSet) {
            capacityRemain.put(fid, new HashMap<>());
        }
        // 5.1) 历史已排单优先：预占产能，新任务避开已排单时段
        preOccupyHistoricalCapacity(param, capacityRemain);

        // 6) 排程：逐个分配（历史已排单的保留原时间，新任务分配产能）
        Map<String, String> procedurePlanStart = new HashMap<>();
        Map<String, String> procedurePlanEnd = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        List<String> failedItems = new ArrayList<>();

        for (SchedulableTask task : tasks) {
            String procId = task.getMachinProcedureId();
            if (Boolean.TRUE.equals(param.getRespectProcedureOrder()) && procedurePlanEnd.containsKey(procId)) {
                continue; // 同一工序只排一次(多个farm task共享)
            }
            String farmId = task.getFarmId();
            // 历史已排单：保留原计划时间，不重新分配
            if (StrUtil.isNotEmpty(task.getExistingPlanStartTime()) && StrUtil.isNotEmpty(task.getExistingPlanEndTime())) {
                procedurePlanStart.put(procId, task.getExistingPlanStartTime());
                procedurePlanEnd.put(procId, task.getExistingPlanEndTime());
                Map<String, Object> item = new HashMap<>();
                item.put("machinId", task.getMachinId());
                item.put("machinProcedureId", procId);
                item.put("farmId", farmId);
                item.put("planStartTime", task.getExistingPlanStartTime());
                item.put("planEndTime", task.getExistingPlanEndTime());
                item.put("durationMinutes", task.getDurationMinutes());
                item.put("deliveryTime", task.getDeliveryTime());
                items.add(item);
                continue;
            }
            int duration = task.getDurationMinutes();
            if (duration <= 0) {
                failedItems.add("工序" + procId + "时长为0");
                continue;
            }
            String allocStart;
            if (Boolean.TRUE.equals(param.getRespectProcedureOrder()) && task.getPrevProcedureId() != null) {
                String prevEnd = procedurePlanEnd.get(task.getPrevProcedureId());
                allocStart = prevEnd != null ? prevEnd : param.getScheduleStartDate() + " 08:00:00";
            } else {
                allocStart = param.getScheduleStartDate() + " 08:00:00";
            }
            AllocResult alloc = allocateCapacity(farmId, allocStart, duration, capacityRemain, param.getScheduleEndDate());
            if (alloc == null) {
                failedItems.add("工序" + procId + "产能不足");
                continue;
            }
            procedurePlanStart.put(procId, alloc.getPlanStart());
            procedurePlanEnd.put(procId, alloc.getPlanEnd());
            Map<String, Object> item = new HashMap<>();
            item.put("machinId", task.getMachinId());
            item.put("machinProcedureId", procId);
            item.put("farmId", farmId);
            item.put("planStartTime", alloc.getPlanStart());
            item.put("planEndTime", alloc.getPlanEnd());
            item.put("durationMinutes", duration);
            item.put("deliveryTime", task.getDeliveryTime());
            items.add(item);
        }

        // 7) 仅返回结果供界面展示，不保存；用户微调后调用 saveSchedule 保存
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("failedItems", failedItems);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 预占历史已排单的产能，新任务分配时避开已排单时段（历史排单优先执行）
     */
    private void preOccupyHistoricalCapacity(ApsScheduleParam param, Map<String, Map<String, Integer>> capacityRemain) {
        QueryWrapper<MachinProcedure> qw = new QueryWrapper<>();
        qw.isNotNull(MybatisPlusUtil.toColumns(MachinProcedure::getPlanStartTime));
        qw.isNotNull(MybatisPlusUtil.toColumns(MachinProcedure::getPlanEndTime));
        qw.ne(MybatisPlusUtil.toColumns(MachinProcedure::getPlanStartTime), StrUtil.EMPTY);
        qw.ne(MybatisPlusUtil.toColumns(MachinProcedure::getPlanEndTime), StrUtil.EMPTY);
        List<MachinProcedure> procs = machinProcedureService.list(qw);
        if (CollectionUtil.isEmpty(procs)) {
            return;
        }
        List<String> procIds = procs.stream().map(MachinProcedure::getId).collect(Collectors.toList());
        QueryWrapper<MachinProcedureFarm> farmQw = new QueryWrapper<>();
        farmQw.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinProcedureId), procIds);
        if (CollectionUtil.isNotEmpty(param.getFarmIds())) {
            farmQw.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getFarmId), param.getFarmIds());
        }
        List<MachinProcedureFarm> farmList = machinProcedureFarmService.list(farmQw);
        Map<String, String> procIdToFarmId = farmList.stream()
            .collect(Collectors.toMap(MachinProcedureFarm::getMachinProcedureId, MachinProcedureFarm::getFarmId, (a, b) -> a));
        LocalDate startDate = LocalDate.parse(param.getScheduleStartDate(), DATE_FMT);
        LocalDate endDate = param.getScheduleEndDate() != null
            ? LocalDate.parse(param.getScheduleEndDate(), DATE_FMT) : startDate.plusDays(365);
        for (MachinProcedure proc : procs) {
            String farmId = procIdToFarmId.get(proc.getId());
            if (StrUtil.isEmpty(farmId) || !capacityRemain.containsKey(farmId)) {
                continue;
            }
            addProcUsageToCapacity(proc.getPlanStartTime(), proc.getPlanEndTime(), farmId, capacityRemain, startDate, endDate);
        }
    }

    private void addProcUsageToCapacity(String planStart, String planEnd, String farmId,
                                        Map<String, Map<String, Integer>> capacityRemain,
                                        LocalDate rangeStart, LocalDate rangeEnd) {
        try {
            LocalDateTime start = LocalDateTime.parse(planStart, DATETIME_FMT);
            LocalDateTime end = LocalDateTime.parse(planEnd, DATETIME_FMT);
            LocalDate d = start.toLocalDate();
            LocalDate endD = end.toLocalDate();
            Map<String, Integer> remain = capacityRemain.get(farmId);
            if (remain == null) return;
            while (!d.isAfter(endD)) {
                if (!d.isBefore(rangeStart) && !d.isAfter(rangeEnd)) {
                    int dayStartMin = (d.equals(start.toLocalDate()) ? start.getHour() * 60 + start.getMinute() : 0);
                    int dayEndMin = (d.equals(endD) ? end.getHour() * 60 + end.getMinute() : 24 * 60);
                    int minutes = dayEndMin - dayStartMin;
                    if (minutes > 0) {
                        String ds = d.format(DATE_FMT);
                        remain.merge(ds, minutes, Integer::sum);
                    }
                }
                d = d.plusDays(1);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void saveSchedule(InputObject inputObject, OutputObject outputObject) {
        ApsScheduleSaveParam param = inputObject.getParams(ApsScheduleSaveParam.class);
        if (CollectionUtil.isEmpty(param.getItems())) {
            throw new CustomException("排程明细不能为空");
        }
        for (ApsScheduleSaveParam.ApsScheduleItem item : param.getItems()) {
            if (StrUtil.isEmpty(item.getMachinProcedureId()) || StrUtil.isEmpty(item.getPlanStartTime()) || StrUtil.isEmpty(item.getPlanEndTime())) {
                continue;
            }
            UpdateWrapper<MachinProcedure> uw = new UpdateWrapper<>();
            uw.eq(CommonConstants.ID, item.getMachinProcedureId());
            uw.set(MybatisPlusUtil.toColumns(MachinProcedure::getPlanStartTime), item.getPlanStartTime());
            uw.set(MybatisPlusUtil.toColumns(MachinProcedure::getPlanEndTime), item.getPlanEndTime());
            machinProcedureService.update(uw);
        }
        outputObject.setreturnMessage("保存成功");
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private List<MachinProcedureFarm> loadPendingFarmTasks(ApsScheduleParam param) {
        List<String> states = Arrays.asList(MachinProcedureFarmState.WAIT_RECEIVE.getKey(), MachinProcedureFarmState.WAIT_EXECUTED.getKey());
        QueryWrapper<MachinProcedureFarm> qw = new QueryWrapper<>();
        qw.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getState), states);
        if (CollectionUtil.isNotEmpty(param.getMachinIds())) {
            qw.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinId), param.getMachinIds());
        }
        if (CollectionUtil.isNotEmpty(param.getFarmIds())) {
            qw.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getFarmId), param.getFarmIds());
        }
        return machinProcedureFarmService.list(qw);
    }

    /**
     * 构建可排程任务列表
     * <p>
     * 将车间任务(MachinProcedureFarm)转换为可排程任务(SchedulableTask)，计算工序时长、前置工序依赖，
     * 并按交货期、工序顺序排序。过滤掉无标准工时、无工艺路线等无效任务。
     * </p>
     *
     * @param farmTasks            待排程的车间任务列表
     * @param machinMap            加工单Map
     * @param farmMapByMachin      按加工单分组的车间任务Map
     * @param procedureMapByMachin 按加工单分组的工序Map
     * @param wayProcedureMap      工艺路线工序Map(wayId -> 工序列表)
     * @param param                排程参数
     * @return 可排程任务列表，已按交货期、工序顺序排序
     */
    private List<SchedulableTask> buildSchedulableTasks(List<MachinProcedureFarm> farmTasks,
                                                        Map<String, Machin> machinMap,
                                                        Map<String, Map<String, List<MachinProcedureFarm>>> farmMapByMachin,
                                                        Map<String, Map<String, MachinProcedure>> procedureMapByMachin,
                                                        Map<String, List<WayProcedureChild>> wayProcedureMap,
                                                        ApsScheduleParam param) {
        List<SchedulableTask> tasks = new ArrayList<>();
        for (MachinProcedureFarm pf : farmTasks) {
            // 1. 校验加工单及子单据
            Machin machin = machinMap.get(pf.getMachinId());
            if (machin == null || CollectionUtil.isEmpty(machin.getMachinChildList())) continue;
            Map<String, MachinProcedure> procMap = procedureMapByMachin.get(pf.getMachinId());
            if (procMap == null) continue;
            // 2. 获取当前车间任务对应的工序信息
            MachinProcedure proc = procMap.values().stream()
                .filter(p -> pf.getMachinProcedureId().equals(p.getId()))
                .findFirst().orElse(null);
            if (proc == null || StrUtil.isEmpty(proc.getWayProcedureId()) || StrUtil.isEmpty(proc.getProcedureId()))
                continue;
            // 3. 获取工艺路线工序，用于计算标准工时
            List<WayProcedureChild> wayList = wayProcedureMap.get(proc.getWayProcedureId());
            if (CollectionUtil.isEmpty(wayList)) continue;
            WayProcedureChild wc = wayList.stream().filter(w -> proc.getProcedureId().equals(w.getProcedureId())).findFirst().orElse(null);
            if (wc == null || StrUtil.isEmpty(wc.getStandardTimeMinutes())) continue;
            // 4. 计算工序时长：目标数量 × 标准工时(分钟/件)，向上取整
            String targetNum = StrUtil.isEmpty(pf.getTargetNum()) ? "0" : pf.getTargetNum();
            String durationStr = CalculationUtil.multiply(targetNum, wc.getStandardTimeMinutes(), 0, RoundingMode.UP);
            int duration = Integer.parseInt(durationStr);
            // 5. 获取交货期（用于排序）
            MachinChild child = machin.getMachinChildList().stream().filter(c -> proc.getChildId().equals(c.getId())).findFirst().orElse(null);
            String deliveryTime = child != null ? child.getDeliveryTime() : "";
            // 6. 若考虑工序依赖，查找前置工序ID（用于排程时确定开始时间）
            String prevProcedureId = null;
            if (Boolean.TRUE.equals(param.getRespectProcedureOrder())) {
                Integer orderBy = proc.getOrderBy();
                if (orderBy != null && orderBy > 1) {
                    MachinProcedure prevProc = procMap.values().stream()
                        .filter(p -> proc.getChildId().equals(p.getChildId()) && p.getOrderBy() != null && p.getOrderBy() == orderBy - 1)
                        .findFirst().orElse(null);
                    if (prevProc != null) {
                        prevProcedureId = prevProc.getId();
                    }
                }
            }
            // 7. 组装可排程任务
            SchedulableTask t = new SchedulableTask();
            t.setMachinId(pf.getMachinId());
            t.setMachinProcedureId(pf.getMachinProcedureId());
            t.setFarmId(pf.getFarmId());
            t.setDurationMinutes(duration);
            t.setDeliveryTime(deliveryTime);
            t.setOrderBy(proc.getOrderBy() != null ? proc.getOrderBy() : 0);
            t.setPrevProcedureId(prevProcedureId);
            // 若工序已有计划时间，保留用于历史排单优先
            if (StrUtil.isNotEmpty(proc.getPlanStartTime()) && StrUtil.isNotEmpty(proc.getPlanEndTime())) {
                t.setExistingPlanStartTime(proc.getPlanStartTime());
                t.setExistingPlanEndTime(proc.getPlanEndTime());
            }
            tasks.add(t);
        }
        // 8. 排序：历史已排单优先，再按交货期、工序顺序
        tasks.sort(Comparator
            .comparing((SchedulableTask t) -> StrUtil.isEmpty(t.getExistingPlanStartTime()) ? 1 : 0)
            .thenComparing(SchedulableTask::getDeliveryTime, Comparator.nullsLast(String::compareTo))
            .thenComparing(SchedulableTask::getOrderBy));
        return tasks;
    }

    /**
     * 在车间产能内分配时间槽
     * <p>
     * 从 allocStart 开始，在指定车间的每日可用工时内，按天从前到后占用产能，支持跨天、跨多天。
     * 考虑产能日历(节假日、加班等)，每日已占用分钟数从 capacityRemain 读取并更新。
     * 若 scheduleEndDate 不为空，超出该日期则分配失败返回 null。
     * </p>
     *
     * @param farmId          车间ID
     * @param allocStart      分配起始时间，格式 yyyy-MM-dd HH:mm:ss
     * @param durationMinutes 需要分配的分钟数
     * @param capacityRemain  车间产能占用情况，farmId -> (dateStr -> 已占用分钟数)，会原地更新
     * @param scheduleEndDate 排程结束日期，格式 yyyy-MM-dd，为空则不限制
     * @return 分配结果(planStart / planEnd)，产能不足或超出结束日期时返回 null
     */
    private AllocResult allocateCapacity(String farmId, String allocStart, int durationMinutes,
                                         Map<String, Map<String, Integer>> capacityRemain, String scheduleEndDate) {
        // 获取该车间每日已占用产能（dateStr -> 已占用分钟数），不存在则创建
        Map<String, Integer> remain = capacityRemain.computeIfAbsent(farmId, k -> new HashMap<>());
        // 解析起始时间：日期 + 当日起始分钟（0:00起算）
        String[] parts = allocStart.split(" ");
        String dateStr = parts[0];
        int startMin = DEFAULT_WORK_START_MIN;
        if (parts.length > 1) {
            String[] hm = parts[1].split(":");
            if (hm.length >= 2) {
                startMin = Integer.parseInt(hm[0]) * 60 + Integer.parseInt(hm[1]);
            }
        }
        int remaining = durationMinutes;
        String planStart = null;
        String planEnd = null;
        LocalDate d = LocalDate.parse(dateStr, DATE_FMT);
        LocalDate endDateLimit = scheduleEndDate != null ? LocalDate.parse(scheduleEndDate, DATE_FMT) : d.plusDays(365);
        int dayStartMin = startMin;
        // 按天遍历，在每日可用产能内分配，支持跨天
        for (int i = 0; i < 365 && remaining > 0; i++) {
            if (d.isAfter(endDateLimit)) {
                return null;
            }
            String ds = d.format(DATE_FMT);
            // 获取该车间当日可用工时（考虑产能日历、节假日等）
            int dailyCap = farmService.getDailyWorkMinutes(farmId, ds);
            if (dailyCap <= 0) {
                d = d.plusDays(1);
                dayStartMin = DEFAULT_WORK_START_MIN;
                continue;
            }
            // 当日已占用分钟数，可用 = 总产能 - 已占用
            int used = remain.getOrDefault(ds, 0);
            int avail = Math.max(0, dailyCap - used);
            if (avail <= 0) {
                d = d.plusDays(1);
                dayStartMin = DEFAULT_WORK_START_MIN;
                continue;
            }
            // 本日可分配量 = min(剩余需求, 当日可用)
            int alloc = Math.min(remaining, avail);
            if (planStart == null) {
                planStart = ds + " " + String.format("%02d:%02d:00", dayStartMin / 60, dayStartMin % 60);
            }
            remaining -= alloc;
            remain.put(ds, used + alloc);
            int endMin = dayStartMin + alloc;
            if (remaining <= 0) {
                planEnd = ds + " " + String.format("%02d:%02d:00", endMin / 60, endMin % 60);
                break;
            }
            d = d.plusDays(1);
            dayStartMin = DEFAULT_WORK_START_MIN;
        }
        if (planStart != null && planEnd != null) {
            AllocResult r = new AllocResult();
            r.setPlanStart(planStart);
            r.setPlanEnd(planEnd);
            return r;
        }
        return null;
    }

}
