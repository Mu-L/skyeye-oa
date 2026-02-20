/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.aps.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.aps.entity.AllocResult;
import com.skyeye.aps.entity.ApsScheduleParam;
import com.skyeye.aps.entity.ApsScheduleSaveParam;
import com.skyeye.aps.entity.SchedulableTask;
import com.skyeye.aps.service.ApsScheduleService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ApsConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.entity.Farm;
import com.skyeye.farm.entity.FarmCalendar;
import com.skyeye.farm.service.FarmCalendarService;
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
import com.skyeye.procedure.service.WorkProcedureService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@SkyeyeService(name = "APS排程", groupName = "APS排程", tenant = TenantEnum.STRONG_ISOLATION)
public class ApsScheduleServiceImpl implements ApsScheduleService {

    /**
     * 单次排程最大任务数，防止任务过多导致卡死
     */
    private static final int MAX_SCHEDULE_TASKS = 2000;

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

    @Autowired
    private FarmCalendarService farmCalendarService;

    @Autowired
    private WorkProcedureService workProcedureService;

    @Override
    public void schedule(InputObject inputObject, OutputObject outputObject) {
        ApsScheduleParam param = inputObject.getParams(ApsScheduleParam.class);
        if (StrUtil.isEmpty(param.getScheduleStartDate())) {
            throw new CustomException("排程开始日期不能为空");
        }

        // 1) 加载待排程车间任务
        List<MachinProcedureFarm> farmTasks = loadPendingFarmTasks(param);
        if (CollectionUtil.isEmpty(farmTasks)) {
            Map<String, Object> result = new HashMap<>();
            result.put("items", Collections.emptyList());
            result.put("failedItems", Collections.emptyList());
            outputObject.setBean(result);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }

        // 2) 加载加工单、工序、工艺
        List<String> machinIds = farmTasks.stream().map(MachinProcedureFarm::getMachinId).distinct().collect(Collectors.toList());
        Map<String, Machin> machinMap = machinService.selectByIds(machinIds.toArray(new String[0])).stream()
            .collect(Collectors.toMap(Machin::getId, m -> m));
        // 获取加工单所有得车间任务信息，按加工单分组
        Map<String, Map<String, List<MachinProcedureFarm>>> farmMapByMachin = machinProcedureFarmService.queryMachinProcedureFarmMapByMachinIds(machinIds);
        // 获取加工单所有得工序信息，按加工单分组
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
        // 填充工序名称，供排程结果展示
        wayProcedureMap.values().forEach(list -> workProcedureService.setDataMation(list, WayProcedureChild::getProcedureId));

        // 4) 构建可排程任务列表(按交货期、工序顺序)
        List<SchedulableTask> tasks = buildSchedulableTasks(farmTasks, machinMap, farmMapByMachin, procedureMapByMachin, wayProcedureMap, param);
        if (CollectionUtil.isEmpty(tasks)) {
            throw new CustomException("无有效排程任务(缺少标准工时等)");
        }
        if (tasks.size() > MAX_SCHEDULE_TASKS) {
            throw new CustomException("待排程任务过多(" + tasks.size() + ")，请缩小范围(指定加工单或车间)，单次最多" + MAX_SCHEDULE_TASKS + "个");
        }
        log.info("APS排程开始: 任务数={}, 车间数={}", tasks.size(), tasks.stream().map(SchedulableTask::getFarmId).distinct().count());

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

        // 5.2) 预加载各车间日期区间内每日产能，批量查询产能日历与车间默认工时，避免循环内频繁查库
        String scheduleStart = param.getScheduleStartDate();
        String scheduleEnd = StrUtil.isNotEmpty(param.getScheduleEndDate()) ? param.getScheduleEndDate() : null;
        Map<String, Map<String, Integer>> dailyCapCache = new HashMap<>();
        List<String> farmIdList = new ArrayList<>(farmIdSet);
        Map<String, List<FarmCalendar>> calendarMapByFarm = farmCalendarService.listByFarmIds(farmIdList);
        Map<String, Integer> defaultMinutesByFarm = new HashMap<>();
        Map<String, String> farmIdToName = new HashMap<>();
        for (Farm f : farmService.queryFarmListByIds(farmIdList)) {
            int def = (f.getDailyWorkMinutes() != null && f.getDailyWorkMinutes() > 0)
                ? f.getDailyWorkMinutes() : ApsConstants.DEFAULT_DAILY_WORK_MINUTES;
            defaultMinutesByFarm.put(f.getId(), def);
            farmIdToName.put(f.getId(), f.getName() != null ? f.getName() : "");
        }
        for (String fid : farmIdSet) {
            List<FarmCalendar> calList = calendarMapByFarm.getOrDefault(fid, Collections.emptyList());
            int defMin = defaultMinutesByFarm.getOrDefault(fid, ApsConstants.DEFAULT_DAILY_WORK_MINUTES);
            dailyCapCache.put(fid, farmService.getDailyWorkMinutesByDateRange(fid, scheduleStart, scheduleEnd, calList, defMin));
        }

        // 6) 排程：逐个分配（历史已排单的保留原时间，新任务分配产能）
        Map<String, String> procedurePlanStart = new HashMap<>();
        Map<String, String> procedurePlanEnd = new HashMap<>();
        // 同一加工单、同一工艺工序(procedureId)下，多子单（子件/成品）续接：记录该 key 下已排的结束时间
        Map<String, String> lastEndByMachinAndProcedure = new HashMap<>();
        // 不同加工单按交货期续接：记录每个加工单当前最晚结束时间，下一加工单的第一个任务从上一加工单的最晚结束时间开始
        Map<String, String> lastEndByMachinId = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        List<String> failedItems = new ArrayList<>();

        int taskIdx = 0;
        for (SchedulableTask task : tasks) {
            taskIdx++;
            if (taskIdx % 200 == 0) {
                log.info("APS排程进度: {}/{}", taskIdx, tasks.size());
            }
            String procId = task.getMachinProcedureId();
            String farmId = task.getFarmId();
            String machinProcKey = StrUtil.isNotEmpty(task.getProcedureId())
                ? task.getMachinId() + "_" + task.getProcedureId() : null;
            // 历史已排单：保留原计划时间，不重新分配
            if (StrUtil.isNotEmpty(task.getExistingPlanStartTime()) && StrUtil.isNotEmpty(task.getExistingPlanEndTime())) {
                procedurePlanStart.put(procId, task.getExistingPlanStartTime());
                procedurePlanEnd.put(procId, task.getExistingPlanEndTime());
                if (machinProcKey != null) {
                    lastEndByMachinAndProcedure.put(machinProcKey, task.getExistingPlanEndTime());
                }
                String machinId = task.getMachinId();
                if (StrUtil.isNotEmpty(machinId)) {
                    lastEndByMachinId.put(machinId, later(lastEndByMachinId.getOrDefault(machinId, ""), task.getExistingPlanEndTime()));
                }
                Map<String, Object> item = new HashMap<>();
                item.put("machinId", task.getMachinId());
                item.put("childId", task.getChildId());
                item.put("machinProcedureId", procId);
                item.put("farmId", farmId);
                item.put("procedureName", task.getProcedureName());
                item.put("farmName", farmIdToName.getOrDefault(farmId, ""));
                item.put("materialName", task.getMaterialName());
                item.put("normsName", task.getNormsName());
                item.put("targetNum", task.getTargetNum());
                item.put("planStartTime", task.getExistingPlanStartTime());
                item.put("planEndTime", task.getExistingPlanEndTime());
                item.put("durationMinutes", task.getDurationMinutes());
                item.put("deliveryTime", task.getDeliveryTime());
                items.add(item);
                continue;
            }
            int duration = parseDurationToMinutes(task.getDurationMinutes());
            if (duration <= 0) {
                failedItems.add("工序" + procId + "时长为0");
                continue;
            }
            String allocStart = param.getScheduleStartDate() + " 08:00:00";
            if (Boolean.TRUE.equals(param.getRespectProcedureOrder())) {
                // 不同加工单按交货期续接：当前加工单的第一个任务从已排过的其他加工单的最晚结束时间开始
                String machinId = task.getMachinId();
                if (StrUtil.isNotEmpty(machinId) && !lastEndByMachinId.containsKey(machinId)) {
                    for (String end : lastEndByMachinId.values()) {
                        if (StrUtil.isNotEmpty(end)) {
                            allocStart = later(allocStart, end);
                        }
                    }
                }
                // 同一工序多车间：从上一车间的结束时间开始排（续接）
                if (procedurePlanEnd.containsKey(procId)) {
                    allocStart = later(allocStart, procedurePlanEnd.get(procId));
                }
                // 同一加工单、同一工艺工序多子单（子件→成品）：从上一子单该工序的结束时间开始排（续接）
                if (machinProcKey != null && lastEndByMachinAndProcedure.containsKey(machinProcKey)) {
                    allocStart = later(allocStart, lastEndByMachinAndProcedure.get(machinProcKey));
                }
                // 同一子单内前序工序：从前序工序结束时间开始
                if (task.getPrevProcedureId() != null && procedurePlanEnd.get(task.getPrevProcedureId()) != null) {
                    allocStart = later(allocStart, procedurePlanEnd.get(task.getPrevProcedureId()));
                }
            }
            Map<String, Integer> capByDate = dailyCapCache.getOrDefault(farmId, Collections.emptyMap());
            AllocResult alloc = allocateCapacity(farmId, allocStart, duration, capacityRemain, param.getScheduleEndDate(), capByDate);
            if (alloc == null) {
                failedItems.add("工序" + procId + "产能不足");
                continue;
            }
            procedurePlanStart.put(procId, alloc.getPlanStart());
            procedurePlanEnd.put(procId, alloc.getPlanEnd());
            if (machinProcKey != null) {
                lastEndByMachinAndProcedure.put(machinProcKey, alloc.getPlanEnd());
            }
            String machinId = task.getMachinId();
            if (StrUtil.isNotEmpty(machinId)) {
                lastEndByMachinId.put(machinId, later(lastEndByMachinId.getOrDefault(machinId, ""), alloc.getPlanEnd()));
            }
            Map<String, Object> item = new HashMap<>();
            item.put("machinId", machinId);
            item.put("childId", task.getChildId());
            item.put("machinProcedureId", procId);
            item.put("farmId", farmId);
            item.put("procedureName", task.getProcedureName());
            item.put("farmName", farmIdToName.getOrDefault(farmId, ""));
            item.put("materialName", task.getMaterialName());
            item.put("normsName", task.getNormsName());
            item.put("targetNum", task.getTargetNum());
            item.put("planStartTime", alloc.getPlanStart());
            item.put("planEndTime", alloc.getPlanEnd());
            item.put("durationMinutes", task.getDurationMinutes());
            item.put("deliveryTime", task.getDeliveryTime());
            items.add(item);
        }

        // 7) 仅返回结果供界面展示，不保存；用户微调后调用 saveSchedule 保存
        log.info("APS排程完成: 成功={}, 失败={}", items.size(), failedItems.size());
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
        Map<String, List<String>> procIdToFarmIds = farmList.stream()
            .collect(Collectors.groupingBy(MachinProcedureFarm::getMachinProcedureId,
                Collectors.mapping(MachinProcedureFarm::getFarmId, Collectors.toList())));
        LocalDate startDate = LocalDate.parse(param.getScheduleStartDate(), DATE_FMT);
        LocalDate endDate = StrUtil.isNotEmpty(param.getScheduleEndDate()) ? LocalDate.parse(param.getScheduleEndDate(), DATE_FMT) : startDate.plusDays(365);
        for (MachinProcedure proc : procs) {
            List<String> farmIds = procIdToFarmIds.get(proc.getId());
            if (CollectionUtil.isEmpty(farmIds)) {
                continue;
            }
            for (String farmId : farmIds) {
                if (StrUtil.isEmpty(farmId) || !capacityRemain.containsKey(farmId)) {
                    continue;
                }
                addProcUsageToCapacity(proc.getPlanStartTime(), proc.getPlanEndTime(), farmId, capacityRemain, startDate, endDate);
            }
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
                        // 合并产能占用情况
                        remain.merge(ds, minutes, Integer::sum);
                    }
                }
                d = d.plusDays(1);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 保存排产：更新加工单工序(MachinProcedure)的计划时间。
     * 同一工序可能对应多个车间任务，但库中仅存一条工序计划时间，故按 machinProcedureId 合并：
     * 取该工序下所有条目的最早开始、最晚结束作为工序的计划时间，每个工序只更新一次。
     */
    @Override
    public void saveSchedule(InputObject inputObject, OutputObject outputObject) {
        ApsScheduleSaveParam param = inputObject.getParams(ApsScheduleSaveParam.class);
        if (CollectionUtil.isEmpty(param.getItems())) {
            throw new CustomException("排程明细不能为空");
        }
        Map<String, ApsScheduleSaveParam.ApsScheduleItem> merged = new LinkedHashMap<>();
        for (ApsScheduleSaveParam.ApsScheduleItem item : param.getItems()) {
            if (StrUtil.isEmpty(item.getMachinProcedureId()) || StrUtil.isEmpty(item.getPlanStartTime()) || StrUtil.isEmpty(item.getPlanEndTime())) {
                continue;
            }
            String id = item.getMachinProcedureId();
            ApsScheduleSaveParam.ApsScheduleItem existing = merged.get(id);
            if (existing == null) {
                ApsScheduleSaveParam.ApsScheduleItem one = new ApsScheduleSaveParam.ApsScheduleItem();
                one.setMachinProcedureId(item.getMachinProcedureId());
                one.setPlanStartTime(item.getPlanStartTime());
                one.setPlanEndTime(item.getPlanEndTime());
                merged.put(id, one);
            } else {
                if (item.getPlanStartTime().compareTo(existing.getPlanStartTime()) < 0) {
                    existing.setPlanStartTime(item.getPlanStartTime());
                }
                if (item.getPlanEndTime().compareTo(existing.getPlanEndTime()) > 0) {
                    existing.setPlanEndTime(item.getPlanEndTime());
                }
            }
        }
        // 批量更新加工单工序(MachinProcedure)的计划时间
        if (CollectionUtil.isNotEmpty(merged)) {
            List<String> procedureIds = new ArrayList<>(merged.keySet());
            QueryWrapper<MachinProcedure> procQw = new QueryWrapper<>();
            procQw.in(CommonConstants.ID, procedureIds);
            List<MachinProcedure> procList = machinProcedureService.list(procQw);
            for (MachinProcedure proc : procList) {
                ApsScheduleSaveParam.ApsScheduleItem item = merged.get(proc.getId());
                if (item != null) {
                    proc.setPlanStartTime(item.getPlanStartTime());
                    proc.setPlanEndTime(item.getPlanEndTime());
                }
            }
            if (CollectionUtil.isNotEmpty(procList)) {
                machinProcedureService.updateBatchById(procList);
            }
        }
        // 顺带批量更新车间任务(MachinProcedureFarm)的计划时间：按 machinProcedureId + farmId 匹配
        List<ApsScheduleSaveParam.ApsScheduleItem> farmItems = param.getItems().stream()
            .filter(item -> StrUtil.isNotEmpty(item.getMachinProcedureId()) && StrUtil.isNotEmpty(item.getFarmId())
                && StrUtil.isNotEmpty(item.getPlanStartTime()) && StrUtil.isNotEmpty(item.getPlanEndTime()))
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(farmItems)) {
            List<String> procedureIds = farmItems.stream()
                .map(ApsScheduleSaveParam.ApsScheduleItem::getMachinProcedureId).distinct().collect(Collectors.toList());
            QueryWrapper<MachinProcedureFarm> farmQw = new QueryWrapper<>();
            farmQw.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinProcedureId), procedureIds);
            List<MachinProcedureFarm> farmList = machinProcedureFarmService.list(farmQw);
            Map<String, ApsScheduleSaveParam.ApsScheduleItem> itemMap = farmItems.stream()
                .collect(Collectors.toMap(i -> i.getMachinProcedureId() + "_" + i.getFarmId(), i -> i, (a, b) -> a));
            List<MachinProcedureFarm> toUpdate = new ArrayList<>();
            for (MachinProcedureFarm farm : farmList) {
                ApsScheduleSaveParam.ApsScheduleItem item = itemMap.get(farm.getMachinProcedureId() + "_" + farm.getFarmId());
                if (item != null) {
                    farm.setPlanStartTime(item.getPlanStartTime());
                    farm.setPlanEndTime(item.getPlanEndTime());
                    toUpdate.add(farm);
                }
            }
            if (CollectionUtil.isNotEmpty(toUpdate)) {
                machinProcedureFarmService.updateBatchById(toUpdate);
            }
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
            String procedureName = null;
            if (wc.getProcedureMation() != null && StrUtil.isNotEmpty(wc.getProcedureMation().getName())) {
                procedureName = wc.getProcedureMation().getName();
            }
            // 4. 计算工序时长：目标数量 × 标准工时(分钟/件)，保留2位小数
            String targetNumForDuration = StrUtil.isEmpty(pf.getTargetNum()) ? "0" : pf.getTargetNum();
            String durationStr = CalculationUtil.multiply(targetNumForDuration, wc.getStandardTimeMinutes(), 2, RoundingMode.UP);
            // 5. 获取交货期、商品、规格、数量（用于排序及结果展示）
            MachinChild child = machin.getMachinChildList().stream().filter(c -> proc.getChildId().equals(c.getId())).findFirst().orElse(null);
            String deliveryTime = child != null ? child.getDeliveryTime() : "";
            String materialName = null;
            String normsName = null;
            if (child != null) {
                if (child.getMaterialMation() != null && StrUtil.isNotEmpty(child.getMaterialMation().getName())) {
                    materialName = child.getMaterialMation().getName();
                }
                if (child.getNormsMation() != null && StrUtil.isNotEmpty(child.getNormsMation().getName())) {
                    normsName = child.getNormsMation().getName();
                }
            }
            String targetNum = StrUtil.isEmpty(pf.getTargetNum()) ? "" : pf.getTargetNum();
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
            t.setChildId(proc.getChildId());
            t.setProcedureId(proc.getProcedureId());
            t.setProcedureName(procedureName);
            t.setMaterialName(materialName);
            t.setNormsName(normsName);
            t.setTargetNum(targetNum);
            t.setDurationMinutes(durationStr);
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
        // 8. 排序：历史已排单优先，再按交货期、加工单、工序顺序；同一交货期下同一加工单连续，便于不同加工单按交货期续接
        tasks.sort(Comparator
            .comparing((SchedulableTask t) -> StrUtil.isEmpty(t.getExistingPlanStartTime()) ? 1 : 0)
            .thenComparing(SchedulableTask::getDeliveryTime, Comparator.nullsLast(String::compareTo))
            .thenComparing(SchedulableTask::getMachinId, Comparator.nullsLast(String::compareTo))
            .thenComparing(SchedulableTask::getOrderBy)
            .thenComparing(SchedulableTask::getProcedureId, Comparator.nullsLast(String::compareTo))
            .thenComparing(SchedulableTask::getChildId, Comparator.nullsLast(String::compareTo))
            .thenComparing(SchedulableTask::getMachinProcedureId, Comparator.nullsLast(String::compareTo))
            .thenComparing(SchedulableTask::getFarmId, Comparator.nullsLast(String::compareTo)));
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
                                         Map<String, Map<String, Integer>> capacityRemain, String scheduleEndDate, Map<String, Integer> capByDate) {
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
        LocalDate endDateLimit = StrUtil.isNotEmpty(scheduleEndDate) ? LocalDate.parse(scheduleEndDate, DATE_FMT) : d.plusDays(365);
        int dayStartMin = startMin;
        // 按天遍历，在每日可用产能内分配，支持跨天
        for (int i = 0; i < 365 && remaining > 0; i++) {
            if (d.isAfter(endDateLimit)) {
                return null;
            }
            String ds = d.format(DATE_FMT);
            // 从预加载缓存获取当日可用工时，未命中则跳过（视为无产能）
            int dailyCap = capByDate.getOrDefault(ds, 0);
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

    /**
     * 返回两个日期时间字符串中较晚的一个（格式 yyyy-MM-dd HH:mm:ss），用于续接取最晚开始
     */
    private String later(String a, String b) {
        if (StrUtil.isEmpty(a)) return b;
        if (StrUtil.isEmpty(b)) return a;
        return a.compareTo(b) >= 0 ? a : b;
    }

    /**
     * 将时长字符串解析为整数分钟（向上取整），用于产能分配
     */
    private int parseDurationToMinutes(String durationStr) {
        if (StrUtil.isEmpty(durationStr)) {
            return 0;
        }
        try {
            return new java.math.BigDecimal(durationStr).setScale(0, RoundingMode.UP).intValueExact();
        } catch (Exception e) {
            return 0;
        }
    }

}
