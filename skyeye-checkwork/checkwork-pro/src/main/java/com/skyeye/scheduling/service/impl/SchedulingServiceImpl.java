package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.rest.promote.service.ISysEveUserStaffService;
import com.skyeye.scheduling.classenum.SchedulePeopleType;
import com.skyeye.scheduling.classenum.ScheduleType;
import com.skyeye.scheduling.dao.SchedulingDao;
import com.skyeye.scheduling.entity.*;
import com.skyeye.scheduling.service.*;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班管理", groupName = "排班管理")
public class SchedulingServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingDao, Scheduling> implements SchedulingService {

    @Autowired
    private BusinessTripService businessTripService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private ISysEveUserStaffService iSysEveUserStaffService;

    @Autowired
    private SchedulingShiftsTimeService schedulingShiftsTimeService;

    @Autowired
    private SchedulingShiftsTimeWorkService schedulingShiftsTimeWorkService;

    @Autowired
    private SchedulingLeaveService schedulingLeaveService;

    @Autowired
    private SchedulingShiftsService schedulingShiftsService;

    @Override
    protected void createPrepose(Scheduling entity) {
        String employeeId = entity.getEmployeeId();
        String shiftId = entity.getShiftId();
        String scheduleDate = entity.getScheduleDate();
        String farmId = entity.getFarmId();
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getEmployeeId), employeeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getShiftId), shiftId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), scheduleDate);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getFarmId), farmId);
        List<Scheduling> schedulingList = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(schedulingList)) {
            throw new CustomException("该员工已存在该班次");
        }
    }

    @Override
    protected void createPrepose(List<Scheduling> entities) {
        if (CollectionUtil.isEmpty(entities)) {
            return;
        }
        List<String> employeeIds = entities.stream().map(Scheduling::getEmployeeId).collect(Collectors.toList());
        List<String> shiftIds = entities.stream().map(Scheduling::getShiftId).collect(Collectors.toList());
        List<String> scheduleDates = entities.stream().map(Scheduling::getScheduleDate).collect(Collectors.toList());
        List<String> farmIds = entities.stream().map(Scheduling::getFarmId).collect(Collectors.toList());
        List<String> shiftTimeIds = entities.stream().map(Scheduling::getShiftTimeId).collect(Collectors.toList());
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getEmployeeId), employeeIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getShiftId), shiftIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), scheduleDates);
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getFarmId), farmIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getShiftTimeId), shiftTimeIds);
        List<Scheduling> existingSchedulings = list(queryWrapper);
        for (Scheduling entity : entities) {
            for (Scheduling existing : existingSchedulings) {
                if (entity.getEmployeeId().equals(existing.getEmployeeId())
                    && entity.getShiftId().equals(existing.getShiftId())
                    && entity.getScheduleDate().equals(existing.getScheduleDate())
                    && entity.getFarmId().equals(existing.getFarmId())) {
                    throw new CustomException("员工ID " + entity.getEmployeeId() + " 在车间 " + entity.getFarmId() + " 在日期 " + entity.getScheduleDate() + "的班次 " + entity.getShiftId() + " 已存在排班记录");
                }
            }
        }
    }


    @Override
    public void autoComputeScheduling(InputObject inputObject, OutputObject outputObject) {
        SchedulingAuto schedulingAuto = inputObject.getParams(SchedulingAuto.class);
        // 获取开始时间,格式是yyyy-MM-dd HH:mm
        String startTime = schedulingAuto.getStartTime();
        // 获取结束时间,格式是yyyy-MM-dd HH:mm
        String endTime = schedulingAuto.getEndTime();
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            // 去除多余的空格
            startTime = startTime.trim();
            endTime = endTime.trim();
            LocalDate startLocalTime = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDate endLocalTime = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            boolean isCrossDay = endLocalTime.isAfter(startLocalTime);
            if (!isCrossDay) {
                throw new CustomException("开始时间不能晚于结束时间");
            }
        }
        // 获取车间Id
        String farmId = schedulingAuto.getFarmId();
        // 获取班次Id
        String schedulingShiftsId = schedulingAuto.getSchedulingShiftsId();
        // 获取班次时间段Id
        String schedulingShiftsTimeIds = schedulingAuto.getSchedulingShiftsTimeIds();
        List<String> shiftsTimeIdList = Arrays.asList(schedulingShiftsTimeIds.split(CommonCharConstants.COMMA_MARK));
        // 获取员工Id列表
        String employeeIds = schedulingAuto.getEmployeeIds();

        // 解析员工ID和权重
        List<String> employeeIdList;
        Map<String, Integer> employeeIdWeightMap = new HashMap<>();

        if (employeeIds.startsWith("[")) {
            // 处理带权重的格式 [{"id":"xxx","weight":"100"}]
            List<Map<String, Object>> employeeIdWeightList = JSONUtil.parseArray(employeeIds).stream()
                .map(obj -> (Map<String, Object>) obj)
                .collect(Collectors.toList());
            employeeIdList = employeeIdWeightList.stream()
                .map(map -> map.get("id").toString())
                .collect(Collectors.toList());
            employeeIdWeightMap = employeeIdWeightList.stream()
                .collect(Collectors.toMap(
                    map -> map.get("id").toString(),
                    map -> map.get("weight") != null ? Integer.parseInt(map.get("weight").toString()) : 1
                ));
        } else {
            // 处理逗号分隔的纯ID格式 "id1,id2,id3"
            employeeIdList = Arrays.asList(employeeIds.split(CommonCharConstants.COMMA_MARK));
            // 为所有员工设置默认权重1
            for (String id : employeeIdList) {
                employeeIdWeightMap.put(id, 1);
            }
        }

        autoScheduling(farmId, schedulingShiftsId, shiftsTimeIdList, employeeIdList, employeeIdWeightMap, startTime, endTime);
    }

    private void autoScheduling(
        String farmId, // 车间Id
        String schedulingShiftsId, // 班次Id
        List<String> shiftsTimeIdList, // 时间段Id列表
        List<String> employeeIdList, // 员工Id列表
        Map<String, Integer> employeeIdWeightMap, // 员工ID到权重的映射
        String startTime, // 开始时间
        String endTime // 结束时间
    ) {
        // 获取所有员工信息
        List<Map<String, Object>> allStaffList = iSysEveUserStaffService.queryAllStaffList();

        // 区分正式员工和临时员工
        Map<Boolean, List<Map<String, Object>>> staffByType = allStaffList.stream()
            .filter(staff -> staff.get("id") != null && employeeIdList.contains(staff.get("id").toString()))
            .collect(Collectors.groupingBy(staff -> staff.get("userId") != null && StrUtil.isNotEmpty(staff.get("userId").toString())));
        // 获取正式员工
        List<Map<String, Object>> formalStaff = staffByType.getOrDefault(true, new ArrayList<>());
        // 获取临时员工
        List<Map<String, Object>> tempStaff = staffByType.getOrDefault(false, new ArrayList<>());

        // 获取正式员工的请假和出差信息
        Map<String, List<LeaveTimeSlot>> formalLeaveMap = leaveService.queryStateIsSuccessLeaveDayByUserId(
            startTime, endTime, formalStaff);
        Map<String, List<BusinessTripTimeSlot>> formalTripMap = businessTripService.queryStateIsSuccessBusinessTripDayByUserId(
            startTime, endTime, formalStaff);

        // 获取临时员工的请假信息
        Map<String, List<SchedulingLeave>> tempLeaveMap = schedulingLeaveService.queryLeaveByEmployeeIds(
            tempStaff.stream().map(staff -> staff.get("id").toString()).collect(Collectors.toList()),
            startTime,
            endTime
        );

        // 获取班次下的所有时间段
        List<SchedulingShiftsTime> shiftTimes = schedulingShiftsTimeService.queryShiftsTimeByIdList(shiftsTimeIdList);
        if (CollectionUtil.isEmpty(shiftTimes)) {
            throw new CustomException("未找到对应的班次时间段信息");
        }

        // 获取每个时间段下面工位需求
        List<SchedulingShiftsTimeWork> shiftsTimeWorkList = schedulingShiftsTimeWorkService.queryShiftsTimeWorkByShiftsTimeIds(shiftsTimeIdList);
        Map<String, List<SchedulingShiftsTimeWork>> shiftsTimeWorkMap = shiftsTimeWorkList.stream()
            .collect(Collectors.groupingBy(SchedulingShiftsTimeWork::getShiftsTimeId));

        // 按时间段长度排序，优先分配较长的时间段
        shiftTimes.sort((a, b) -> {
            int aDuration = calculateDuration(a.getStartTime(), a.getEndTime());
            int bDuration = calculateDuration(b.getStartTime(), b.getEndTime());
            return bDuration - aDuration;
        });

        // 时间范围内的所有日期
        List<LocalDate> dateRange = generateDateRange(startTime, endTime);

        // 存储最终的排班结果
        List<Scheduling> finalSchedules = new ArrayList<>();

        // 遍历每个日期
        for (LocalDate date : dateRange) {
            // 遍历每个时间段
            for (SchedulingShiftsTime shiftTime : shiftTimes) {
                // 获取当前可用的正式员工和临时员工
                List<Map<String, Object>> availableFormalStaff = getAvailableFormalStaffForTimeSlot(
                    formalStaff, date, shiftTime, formalLeaveMap, formalTripMap);
                List<Map<String, Object>> availableTempStaff = getAvailableTempStaffForTimeSlot(
                    tempStaff, date, shiftTime, tempLeaveMap);

                // 获取该时间段的所有工位需求
                List<SchedulingShiftsTimeWork> workPositions = shiftsTimeWorkMap.get(shiftTime.getId());

                // 如果没有工位需求，使用时间段的最小/最大需求人数
                if (CollectionUtil.isEmpty(workPositions)) {
                    int minRequired = shiftTime.getMinStaff() != null ? shiftTime.getMinStaff() : 0;
                    int maxAllowed = shiftTime.getMaxStaff() != null ? shiftTime.getMaxStaff() : Integer.MAX_VALUE;

                    // 优先分配正式员工
                    int toAssign = Math.min(maxAllowed, availableFormalStaff.size());
                    if (toAssign < minRequired) {
                        // 如果正式员工不足，使用临时员工补充
                        int remainingRequired = minRequired - toAssign;
                        int tempToAssign = Math.min(remainingRequired, availableTempStaff.size());
                        toAssign += tempToAssign;
                    }

                    // 分配员工
                    for (int i = 0; i < toAssign; i++) {
                        Map<String, Object> staff = i < availableFormalStaff.size() ?
                            availableFormalStaff.get(i) :
                            availableTempStaff.get(i - availableFormalStaff.size());
                        String employeeId = staff.get("id").toString();

                        Scheduling scheduling = new Scheduling();
                        scheduling.setEmployeeId(employeeId);
                        scheduling.setShiftId(schedulingShiftsId);
                        scheduling.setShiftTimeId(shiftTime.getId());
                        scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
                        scheduling.setFarmId(farmId);
                        scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());
                        scheduling.setSchedulePeopleType(SchedulePeopleType.ONDUTY.getKey());

                        // 设置权重
                        Integer weight = employeeIdWeightMap.get(employeeId);
                        scheduling.setSchedulingWeight(weight);

                        finalSchedules.add(scheduling);
                    }
                    continue;
                }

                // 为每个工位分配员工
                for (SchedulingShiftsTimeWork workPosition : workPositions) {
                    // 获取该工位的已分配人数
                    int currentAssigned = (int) finalSchedules.stream()
                        .filter(s -> s.getShiftTimeId().equals(shiftTime.getId()) &&
                            s.getWorkId().equals(workPosition.getWorkId()) &&
                            s.getScheduleDate().equals(date.format(DateTimeFormatter.ISO_DATE)))
                        .count();

                    // 计算该工位还需要分配的人数
                    int minRequired = workPosition.getMinStaff() != null ? workPosition.getMinStaff() : 0;
                    int maxAllowed = workPosition.getMaxStaff() != null ? workPosition.getMaxStaff() : Integer.MAX_VALUE;

                    // 如果当前已分配人数超过最大限制，跳过该工位
                    if (currentAssigned >= maxAllowed) {
                        continue;
                    }

                    // 优先分配正式员工
                    int remainingSlots = maxAllowed - currentAssigned;
                    int toAssign = Math.min(remainingSlots, availableFormalStaff.size());

                    // 如果当前已分配人数小于最小需求，但可用人数不足，则使用所有可用人数
                    if (currentAssigned < minRequired) {
                        toAssign = Math.min(availableFormalStaff.size(), minRequired - currentAssigned);
                    }

                    // 分配正式员工
                    for (int i = 0; i < toAssign; i++) {
                        Map<String, Object> staff = availableFormalStaff.get(i);
                        String employeeId = staff.get("id").toString();

                        Scheduling scheduling = new Scheduling();
                        scheduling.setEmployeeId(employeeId);
                        scheduling.setShiftId(schedulingShiftsId);
                        scheduling.setShiftTimeId(shiftTime.getId());
                        scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
                        scheduling.setFarmId(farmId);
                        scheduling.setWorkId(workPosition.getWorkId());
                        scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());
                        scheduling.setSchedulePeopleType(SchedulePeopleType.ONDUTY.getKey());

                        // 设置权重
                        Integer weight = employeeIdWeightMap.get(employeeId);
                        scheduling.setSchedulingWeight(weight);

                        finalSchedules.add(scheduling);
                    }

                    // 从可用员工列表中移除已分配的员工
                    availableFormalStaff = availableFormalStaff.subList(toAssign, availableFormalStaff.size());

                    // 如果正式员工不足，使用临时员工补充
                    if (currentAssigned + toAssign < minRequired) {
                        int remainingRequired = minRequired - (currentAssigned + toAssign);
                        int tempToAssign = Math.min(remainingRequired, availableTempStaff.size());

                        // 分配临时员工
                        for (int i = 0; i < tempToAssign; i++) {
                            Map<String, Object> staff = availableTempStaff.get(i);
                            String employeeId = staff.get("id").toString();

                            Scheduling scheduling = new Scheduling();
                            scheduling.setEmployeeId(employeeId);
                            scheduling.setShiftId(schedulingShiftsId);
                            scheduling.setShiftTimeId(shiftTime.getId());
                            scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
                            scheduling.setFarmId(farmId);
                            scheduling.setWorkId(workPosition.getWorkId());
                            scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());
                            scheduling.setSchedulePeopleType(SchedulePeopleType.ONDUTY.getKey());

                            // 设置权重
                            Integer weight = employeeIdWeightMap.get(employeeId);
                            scheduling.setSchedulingWeight(weight);

                            finalSchedules.add(scheduling);
                        }

                        // 从可用员工列表中移除已分配的员工
                        availableTempStaff = availableTempStaff.subList(tempToAssign, availableTempStaff.size());
                    }
                }
            }
        }

        // 保存排班结果
        if (CollectionUtil.isNotEmpty(finalSchedules)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            super.createEntity(finalSchedules, userId);
        }
    }

    /**
     * 计算员工权重
     * 权重规则：
     * 1. 员工类型：正式员工(有userId) +30分，临时员工 +10分
     * 2. 工龄：根据workTime计算，每满一年加10分
     * 3. 在职状态：
     * - 在职(1) +20分
     * - 试用期(4) +10分
     * - 见习(3) +5分
     * - 离职(2)和退休(5) 不参与排班
     * 4. 已分配班次：每多分配一次，权重减少5分，防止过度分配
     */
    private Map<String, Integer> calculateEmployeeWeights(List<Map<String, Object>> staffList, // 员工列表
                                                          Map<String, Map<String, Object>> employeeJobMap) // 员工职位信息
    {
        Map<String, Integer> weights = new HashMap<>();
        // 记录每个职位的已分配次数
        Map<String, Integer> jobAssignmentCount = new HashMap<>();

        for (Map<String, Object> staff : staffList) {
            int weight = 0;
            String staffId = staff.get("id").toString();
            String jobId = staff.get("jobId") != null ? staff.get("jobId").toString() : null;

            // 1. 员工类型权重
            if (staff.get("userId") != null && StrUtil.isNotEmpty(staff.get("userId").toString())) {
                weight += 30; // 正式员工
            } else {
                weight += 10; // 临时员工
            }

            // 2. 工龄权重
            if (staff.get("workTime") != null) {
                String workTime = staff.get("workTime").toString();
                try {
                    LocalDate workDate = LocalDate.parse(workTime);
                    int workYears = Period.between(workDate, LocalDate.now()).getYears();
                    weight += workYears * 10;
                } catch (Exception e) {
                    // 如果日期解析失败，不计算工龄权重
                }
            }

            // 3. 在职状态权重
            Integer state = staff.get("state") != null ? Integer.parseInt(staff.get("state").toString()) : null;
            if (state != null) {
                switch (state) {
                    case 1: // 在职(转正的员工)
                        weight += 20;
                        break;
                    case 4: // 试用期(未转正的员工)
                        weight += 10;
                        break;
                    case 3: // 见习(实习生)
                        weight += 5;
                        break;
                    case 2: // 离职
                    case 5: // 退休
                        // 离职和退休的员工不参与排班，权重设为0
                        weight = 0;
                        break;
                }
            }

            // 4. 考虑已分配班次数量，防止过度分配
            if (jobId != null) {
                int currentAssignments = jobAssignmentCount.getOrDefault(jobId, 0);
                weight -= currentAssignments * 5; // 每多分配一次，权重减少5分
            }

            weights.put(staffId, weight);
        }

        return weights;
    }

    /**
     * 计算时间段长度（分钟）
     */
    private int calculateDuration(String startTime, String endTime) {
        // 处理时间格式，确保小时是两位数
        startTime = startTime.length() == 7 ? "0" + startTime : startTime;
        endTime = endTime.length() == 7 ? "0" + endTime : endTime;

        // 如果时间包含秒，则使用 HH:mm:ss 格式，否则使用 HH:mm 格式
        DateTimeFormatter formatter = startTime.length() > 5 ?
            DateTimeFormatter.ofPattern("HH:mm:ss") :
            DateTimeFormatter.ofPattern("HH:mm");
        LocalTime start = LocalTime.parse(startTime, formatter);
        LocalTime end = LocalTime.parse(endTime, formatter);
        return (int) java.time.Duration.between(start, end).toMinutes();
    }

    /**
     * 获取指定时间段可用的正式员工
     */
    private List<Map<String, Object>> getAvailableFormalStaffForTimeSlot(
        List<Map<String, Object>> staffList,
        LocalDate date,
        SchedulingShiftsTime shiftTime,
        Map<String, List<LeaveTimeSlot>> leaveMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap) {

        return staffList.stream()
            .filter(staff -> {
                String staffId = staff.get("id").toString();
                // 检查是否请假
                if (leaveMap.containsKey(staffId)) {
                    for (LeaveTimeSlot leave : leaveMap.get(staffId)) {
                        if (date.equals(LocalDate.parse(leave.getLeaveDay()))) {
                            return false;
                        }
                    }
                }
                // 检查是否出差
                if (tripMap.containsKey(staffId)) {
                    for (BusinessTripTimeSlot trip : tripMap.get(staffId)) {
                        if (date.equals(LocalDate.parse(trip.getTravelDay()))) {
                            return false;
                        }
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取指定时间段可用的临时员工
     */
    private List<Map<String, Object>> getAvailableTempStaffForTimeSlot(
        List<Map<String, Object>> staffList,
        LocalDate date,
        SchedulingShiftsTime shiftTime,
        Map<String, List<SchedulingLeave>> leaveMap) {

        return staffList.stream()
            .filter(staff -> {
                String staffId = staff.get("id").toString();
                // 检查是否请假
                if (leaveMap.containsKey(staffId)) {
                    for (SchedulingLeave leave : leaveMap.get(staffId)) {
                        LocalDate leaveStartDate = LocalDate.parse(leave.getStartTime().split(" ")[0]);
                        LocalDate leaveEndDate = LocalDate.parse(leave.getEndTime().split(" ")[0]);
                        if (!date.isBefore(leaveStartDate) && !date.isAfter(leaveEndDate)) {
                            return false;
                        }
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    private List<LocalDate> generateDateRange(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDate start = LocalDate.parse(startTime, formatter);
        LocalDate end = LocalDate.parse(endTime, formatter);

        List<LocalDate> dates = new ArrayList<>();
        while (!start.isAfter(end)) {
            dates.add(start);
            start = start.plusDays(1);
        }
        return dates;
    }

    @Override
    public void querySchedulingListByTimeSlot(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 车间id
        String holderId = commonPageInfo.getHolderId();
        // 班次名称
        String keyword = commonPageInfo.getKeyword();
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(holderId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getFarmId), holderId);
        }
        List<Scheduling> schedulingList = list(queryWrapper);
        // 如果 keyword 不为空，过滤 schedulingList
        if (StrUtil.isNotEmpty(keyword)) {
            List<String> shiftIds = schedulingList.stream().map(Scheduling::getShiftId).collect(Collectors.toList());
            List<SchedulingShifts> schedulingShifts = schedulingShiftsService.querySchedulingShiftsByIds(shiftIds);
            Map<String, String> idToNameMap = schedulingShifts.stream()
                .collect(Collectors.toMap(SchedulingShifts::getId, SchedulingShifts::getName));
            schedulingList = schedulingList.stream()
                .filter(scheduling -> {
                    String shiftName = idToNameMap.get(scheduling.getShiftId());
                    return StrUtil.isNotEmpty(shiftName) && shiftName.contains(keyword);
                })
                .collect(Collectors.toList());
        }
        // 获取所有员工信息
        List<Map<String, Object>> allStaffList = iSysEveUserStaffService.queryAllStaffList();
        Map<String, List<Map<String, Object>>> userIdMap = allStaffList.stream()
            .filter(staffInfo -> staffInfo != null && staffInfo.get("userId") != null)
            .collect(Collectors.groupingBy(staffInfo -> staffInfo.get("userId").toString()));
        Map<String, List<Map<String, Object>>> idStaffMap = allStaffList.stream()
            .filter(staffInfo -> staffInfo != null && staffInfo.get("id") != null)
            .collect(Collectors.groupingBy(staffInfo -> staffInfo.get("id").toString()));
        // 设置员工信息
        for (Scheduling scheduling : schedulingList) {
            String employeeId = scheduling.getEmployeeId();
            List<Map<String, Object>> staffInfoList = userIdMap.get(employeeId);
            if (CollectionUtil.isNotEmpty(staffInfoList)) {
                scheduling.setStaffMation(staffInfoList);
            } else {
                staffInfoList = idStaffMap.get(employeeId);
                if (CollectionUtil.isNotEmpty(staffInfoList)) {
                    scheduling.setStaffMation(staffInfoList);
                }
            }
        }
        // 获取班次时间和班次信息
        List<String> shiftTimeIds = schedulingList.stream().map(Scheduling::getShiftTimeId).collect(Collectors.toList());
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryShiftsTimeByIdList(shiftTimeIds);
        Map<String, List<SchedulingShiftsTime>> stringListMap = schedulingShiftsTimes.stream().collect(Collectors.groupingBy(SchedulingShiftsTime::getId));

        List<String> shiftIds = schedulingList.stream().map(Scheduling::getShiftId).collect(Collectors.toList());
        List<SchedulingShifts> schedulingShifts = schedulingShiftsService.querySchedulingShiftsByIds(shiftIds);
        Map<String, List<SchedulingShifts>> schedulingShiftsMap = schedulingShifts.stream().collect(Collectors.groupingBy(SchedulingShifts::getId));

        // 设置班次时间和班次信息
        for (Scheduling scheduling : schedulingList) {
            List<SchedulingShifts> shifts = schedulingShiftsMap.get(scheduling.getShiftId());
            if (CollectionUtil.isNotEmpty(shifts)) {
                scheduling.setSchedulingShiftMation(shifts.get(CommonNumConstants.NUM_ZERO));
            }
            List<SchedulingShiftsTime> shiftsTimes = stringListMap.get(scheduling.getShiftTimeId());
            if (CollectionUtil.isNotEmpty(shiftsTimes)) {
                scheduling.setSchedulinghiftTimeMation(shiftsTimes.get(CommonNumConstants.NUM_ZERO));
            }
        }
        iAuthUserService.setName(schedulingList, "createId", "createName");
        iAuthUserService.setName(schedulingList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(schedulingList);
        outputObject.settotal(page.getTotal());
    }


    public void querySchedulingListByTimeSlot1(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 车间id
        String holderId = commonPageInfo.getHolderId();
        // 班次名称
        String keyword = commonPageInfo.getKeyword();
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(holderId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getFarmId), holderId);
        }
        List<Scheduling> schedulingList = list(queryWrapper);
        List<String> shiftIds = schedulingList.stream().map(Scheduling::getShiftId).collect(Collectors.toList());
        List<SchedulingShifts> schedulingShifts = schedulingShiftsService.querySchedulingShiftsByIds(shiftIds);
        if (StrUtil.isNotEmpty(keyword)) {
            schedulingList = new ArrayList<>();
            // 班次id和班次名称的map
            Map<String, String> idToNameMap = schedulingShifts.stream()
                .collect(Collectors.toMap(SchedulingShifts::getId, SchedulingShifts::getName));
            for (Scheduling scheduling : schedulingList) {
                String shiftId = scheduling.getShiftId();
                String shiftName = idToNameMap.get(shiftId);
                if (StrUtil.isNotEmpty(shiftName) && shiftName.contains(keyword)) {
                    schedulingList.add(scheduling);
                }
            }
        }
        List<Map<String, Object>> allStaffList = iSysEveUserStaffService.queryAllStaffList();
        Map<String, List<Map<String, Object>>> userIdMap = allStaffList.stream()
            .filter(staffInfo -> staffInfo != null && staffInfo.get("userId") != null)
            .collect(Collectors.groupingBy(staffInfo -> staffInfo.get("userId").toString()));
        Map<String, List<Map<String, Object>>> idStaffMap = allStaffList.stream()
            .filter(staffInfo -> staffInfo != null && staffInfo.get("id") != null)
            .collect(Collectors.groupingBy(staffInfo -> staffInfo.get("id").toString()));
        for (Scheduling scheduling : schedulingList) {
            String employeeId = scheduling.getEmployeeId();
            List<Map<String, Object>> staffInfoList = userIdMap.get(employeeId);
            if (CollectionUtil.isNotEmpty(staffInfoList)) {
                scheduling.setStaffMation(staffInfoList);
            } else {
                // 如果没有根据userId查找到员工信息，则尝试根据id查找（临时工）
                staffInfoList = idStaffMap.get(employeeId);
                if (CollectionUtil.isNotEmpty(staffInfoList)) {
                    scheduling.setStaffMation(staffInfoList);
                }
            }
        }
        List<String> shiftTimeIds = schedulingList.stream().map(Scheduling::getShiftTimeId).collect(Collectors.toList());
        Map<String, List<SchedulingShifts>> schedulingShiftsMap = schedulingShifts.stream().collect(Collectors.groupingBy(SchedulingShifts::getId));
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryShiftsTimeByIdList(shiftTimeIds);
        Map<String, List<SchedulingShiftsTime>> stringListMap = schedulingShiftsTimes.stream().collect(Collectors.groupingBy(SchedulingShiftsTime::getId));
        for (Scheduling scheduling : schedulingList) {
            List<SchedulingShifts> shifts = schedulingShiftsMap.get(scheduling.getShiftId());
            if (CollectionUtil.isNotEmpty(shifts)) {
                scheduling.setSchedulingShiftMation(shifts.get(CommonNumConstants.NUM_ZERO));
            }
            List<SchedulingShiftsTime> shiftsTimes = stringListMap.get(scheduling.getShiftTimeId());
            if (CollectionUtil.isNotEmpty(shiftsTimes)) {
                scheduling.setSchedulinghiftTimeMation(shiftsTimes.get(CommonNumConstants.NUM_ZERO));
            }
        }
        iAuthUserService.setName(schedulingList, "createId", "createName");
        iAuthUserService.setName(schedulingList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(schedulingList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void deleteSchedulingByIds(InputObject inputObject, OutputObject outputObject) {
        String ids = inputObject.getParams().get("ids").toString();
        String[] split = ids.split(CommonCharConstants.COMMA_MARK);
        List<String> idList = Arrays.asList(split);
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, idList);
        remove(queryWrapper);
    }

    @Override
    public void updateEmployStateByLeave(String employeeId, String startTime, String endTime) {
        // 1. 获取该员工的所有排班记录
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getEmployeeId), employeeId);
        List<Scheduling> schedulingList = list(queryWrapper);

        // 2. 获取所有相关的时间段信息
        List<String> shiftTimeIds = schedulingList.stream().map(Scheduling::getShiftTimeId).collect(Collectors.toList());
        List<SchedulingShiftsTime> schedulingShiftsTimes = schedulingShiftsTimeService.queryShiftsTimeByIdList(shiftTimeIds);
        Map<String, List<SchedulingShiftsTime>> stringListMap = schedulingShiftsTimes.stream().collect(Collectors.groupingBy(SchedulingShiftsTime::getId));

        // 3. 将传入的请假时间转换为 LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime leaveStartDateTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime leaveEndDateTime = LocalDateTime.parse(endTime, formatter);

        // 4. 创建一个列表用于存放需要更新的排班记录
        List<Scheduling> resultSchedulingList = new ArrayList<>();

        // 5. 遍历所有排班记录，检查时间是否冲突
        for (Scheduling scheduling : schedulingList) {
            List<SchedulingShiftsTime> shiftsTimes = stringListMap.get(scheduling.getShiftTimeId());
            if (CollectionUtil.isNotEmpty(shiftsTimes)) {
                SchedulingShiftsTime schedulingShiftsTime = shiftsTimes.get(CommonNumConstants.NUM_ZERO);

                // 组合排班日期和时间段
                String scheduleDate = scheduling.getScheduleDate();
                LocalDateTime shiftStartDateTime = LocalDateTime.parse(scheduleDate + " " + schedulingShiftsTime.getStartTime(), formatter);

                // 处理跨天的情况
                LocalDateTime shiftEndDateTime;
                if (schedulingShiftsTime.getIsNextDay() != null && schedulingShiftsTime.getIsNextDay() == 1) {
                    // 如果是跨天，结束时间要加一天
                    shiftEndDateTime = LocalDateTime.parse(scheduleDate + " " + schedulingShiftsTime.getEndTime(), formatter)
                        .plusDays(1);
                } else {
                    shiftEndDateTime = LocalDateTime.parse(scheduleDate + " " + schedulingShiftsTime.getEndTime(), formatter);
                }

                // 检查时间是否重叠
                boolean isOverlapping = !shiftEndDateTime.isBefore(leaveStartDateTime) && !shiftStartDateTime.isAfter(leaveEndDateTime);

                if (isOverlapping) {
                    resultSchedulingList.add(scheduling);
                }
            }
        }

        // 6. 批量更新状态
        if (CollectionUtil.isNotEmpty(resultSchedulingList)) {
            for (Scheduling scheduling : resultSchedulingList) {
                scheduling.setSchedulePeopleType(SchedulePeopleType.ONLEAVE.getKey());
            }
            // 使用批量更新
            super.updateBatchById(resultSchedulingList);
        }
    }

}
