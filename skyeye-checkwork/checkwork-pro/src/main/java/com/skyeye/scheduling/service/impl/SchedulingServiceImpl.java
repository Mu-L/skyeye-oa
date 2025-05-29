package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.leave.entity.LeaveTimeSlot;
import com.skyeye.leave.service.LeaveService;
import com.skyeye.rest.erp.service.IFarmStationService;
import com.skyeye.rest.promote.service.IProCompanyJobService;
import com.skyeye.rest.promote.service.ISysEveUserStaffService;
import com.skyeye.scheduling.dao.SchedulingDao;
import com.skyeye.scheduling.entity.Scheduling;
import com.skyeye.scheduling.entity.SchedulingAuto;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;
import com.skyeye.scheduling.service.SchedulingService;
import com.skyeye.scheduling.service.SchedulingShiftsTimeService;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private IFarmStationService iFarmStationService;


    @Autowired
    private IProCompanyJobService iProCompanyJobService;

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
            LocalDate startLocalTime = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDate endLocalTime = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            boolean isCrossDay = endLocalTime.isAfter(startLocalTime);
            if (!isCrossDay) {
                throw new CustomException("开始时间不能晚于结束时间");
            }
        }
        // 获取车间Id
        String farmId = schedulingAuto.getFarmId();
        // 获取员工Id列表
        List<String> employeeIdList = Arrays.asList(schedulingAuto.getEmployeeIds().split(CommonCharConstants.COMMA_MARK));
        // 获取所有员工信息（包括正式和临时员工）
        List<Map<String, Object>> allStaffList = iSysEveUserStaffService.queryAllStaffList();

        // 获取所有职位信息
        // TODO: 临时工职位暂时注释,等临时工职位功能完善后再启用
        // List<Map<String, Object>> allFarmStationList = iFarmStationService.queryAllFarmStationList();
        // 正式工职位
        List<Map<String, Object>> allCompanyJobList = iProCompanyJobService.queryCompanyJobList();

        // 构建职位ID到职位信息的映射
        Map<String, Map<String, Object>> jobInfoMap = new HashMap<>();
        // TODO: 临时工职位暂时注释,等临时工职位功能完善后再启用
        // allFarmStationList.forEach(job -> jobInfoMap.put(job.get("id").toString(), job));
        allCompanyJobList.forEach(job -> jobInfoMap.put(job.get("id").toString(), job));

        // 构建员工ID到职位信息的映射
        Map<String, Map<String, Object>> employeeJobMap = new HashMap<>();
        allStaffList.forEach(staff -> {
            String staffId = staff.get("id").toString();
            String jobId = staff.get("jobId") != null ? staff.get("jobId").toString() : null;
            // 只处理正式工(有userId的员工)
            if (jobId != null && jobInfoMap.containsKey(jobId) && staff.get("userId") != null) {
                employeeJobMap.put(staffId, jobInfoMap.get(jobId));
            }
        });

        // 获取班次Id列表
        List<String> schedulingShiftsIdList = Arrays.asList(schedulingAuto.getSchedulingShiftsIds().split(CommonCharConstants.COMMA_MARK));
        // 获取排班时间段Id列表
        List<String> schedulingShiftsTimeIdList = Arrays.asList(schedulingAuto.getSchedulingShiftsTimeIds().split(CommonCharConstants.COMMA_MARK));

        autoScheduling(farmId, employeeIdList, schedulingShiftsIdList, schedulingShiftsTimeIdList, startTime, endTime, employeeJobMap);
    }

    private void autoScheduling(
        String farmId, // 车间Id
        List<String> employeeIdList, // 员工Id列表
        List<String> schedulingShiftsIdList, // 班次Id列表
        List<String> schedulingShiftsTimeIdList, // 排班时间段Id列表
        String startTime, // 开始时间
        String endTime, // 结束时间
        Map<String, Map<String, Object>> employeeJobMap) // 员工职位信息
    {

        // 获取班次Id对应的班次时间段
        Map<String, List<SchedulingShiftsTime>> schedlingIdTimeMap = schedulingShiftsTimeService.queryTimeByIdListMap(schedulingShiftsIdList);
        schedlingIdTimeMap = schedlingIdTimeMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .filter(time -> schedulingShiftsTimeIdList.contains(time.getId()))
                    .collect(Collectors.toList())
            ));

        // 时间范围内的所有日期
        List<LocalDate> dateRange = generateDateRange(startTime, endTime);
        String employeeIds = employeeIdList.stream().collect(Collectors.joining(CommonCharConstants.COMMA_MARK));
        // 获取员工信息
        List<Map<String, Object>> staffList = iSysEveUserStaffService.queryEmployeeListByIds(employeeIds);

        // 计算每个员工的权重
        Map<String, Integer> employeeWeights = calculateEmployeeWeights(staffList, employeeJobMap);

        // 按权重对员工进行排序
        List<Map<String, Object>> sortedStaffList = staffList.stream()
            .sorted((a, b) -> {
                String aId = a.get("id").toString();
                String bId = b.get("id").toString();
                return employeeWeights.get(bId).compareTo(employeeWeights.get(aId));
            })
            .collect(Collectors.toList());

        // 获取请假和出差信息
        Map<String, List<LeaveTimeSlot>> leaveMap = leaveService.queryStateIsSuccessLeaveDayByUserId(
            startTime, endTime, sortedStaffList);
        Map<String, List<BusinessTripTimeSlot>> tripMap = businessTripService.queryStateIsSuccessBusinessTripDayByUserId(
            startTime, endTime, sortedStaffList);

        // 存储最终的排班结果
        List<Scheduling> finalSchedules = new ArrayList<>();

        // 遍历每个日期
        for (LocalDate date : dateRange) {
            // 遍历每个班次
            for (String shiftId : schedulingShiftsIdList) {
                List<SchedulingShiftsTime> shiftTimes = schedlingIdTimeMap.get(shiftId);
                if (CollectionUtil.isEmpty(shiftTimes)) {
                    continue;
                }

                // 按时间段长度排序，优先分配较长的时间段
                shiftTimes.sort((a, b) -> {
                    int aDuration = calculateDuration(a.getStartTime(), a.getEndTime());
                    int bDuration = calculateDuration(b.getStartTime(), b.getEndTime());
                    return bDuration - aDuration;
                });

                // 遍历每个时间段
                for (SchedulingShiftsTime shiftTime : shiftTimes) {
                    // 获取当前可用的员工
                    List<Map<String, Object>> availableStaff = getAvailableStaffForTimeSlot(
                        sortedStaffList, date, shiftTime, leaveMap, tripMap);

                    // 为时间段分配员工
                    assignStaffToTimeSlot(availableStaff, shiftId, shiftTime, date, farmId, finalSchedules, employeeJobMap);
                }
            }
        }

        // 保存排班结果
        if (CollectionUtil.isNotEmpty(finalSchedules)) {
            // 为相邻时间段设置不同颜色
            setColorsForAdjacentTimeSlots(finalSchedules);

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
        LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
        return (int) java.time.Duration.between(start, end).toMinutes();
    }

    /**
     * 获取指定时间段可用的员工
     */
    private List<Map<String, Object>> getAvailableStaffForTimeSlot(
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
     * 为时间段分配员工
     * 分配规则：
     * 1. 按职位分组员工
     * 2. 检查每个职位的当前分配人数
     * 3. 根据最小和最大需求人数进行分配
     * 4. 如果可用人数不足，则使用所有可用人数
     */
    private void assignStaffToTimeSlot(
        List<Map<String, Object>> availableStaff,
        String shiftId,
        SchedulingShiftsTime shiftTime,
        LocalDate date,
        String farmId,
        List<Scheduling> finalSchedules,
        Map<String, Map<String, Object>> employeeJobMap) {

        // 按职位分组员工,只处理正式工
        Map<String, List<Map<String, Object>>> staffByJob = availableStaff.stream()
            .filter(staff -> staff.get("userId") != null) // 只处理正式工
            .collect(Collectors.groupingBy(staff -> staff.get("jobId").toString()));

        // 遍历每个职位
        for (Map.Entry<String, List<Map<String, Object>>> entry : staffByJob.entrySet()) {
            String jobId = entry.getKey();
            List<Map<String, Object>> jobStaff = entry.getValue();
            Map<String, Object> jobInfo = employeeJobMap.get(jobStaff.get(0).get("id").toString());

            // 获取该职位在当前时间段的已分配人数
            int currentAssigned = (int) finalSchedules.stream()
                .filter(s -> s.getJobId().equals(jobId) &&
                    s.getShiftTimeId().equals(shiftTime.getId()) &&
                    s.getScheduleDate().equals(date.format(DateTimeFormatter.ISO_DATE)))
                .count();
            // TODO 最小和最大需求人数的拿取
//            // 计算该职位还需要分配的人数
//            int minRequired = shiftTime.getMinStaff() != null ? shiftTime.getMinStaff() : 0;
//            int maxAllowed = shiftTime.getMaxStaff() != null ? shiftTime.getMaxStaff() : Integer.MAX_VALUE;
//
//            // 如果当前已分配人数超过最大限制，跳过该职位
//            if (currentAssigned >= maxAllowed) {
//                continue;
//            }
//
//            // 计算还需要分配的人数
//            int remainingSlots = maxAllowed - currentAssigned;
//            int toAssign = Math.min(remainingSlots, jobStaff.size());
//
//            // 如果当前已分配人数小于最小需求，但可用人数不足，则使用所有可用人数
//            if (currentAssigned < minRequired) {
//                toAssign = Math.min(jobStaff.size(), minRequired - currentAssigned);
//            }
//
//            // 分配员工
//            for (int i = 0; i < toAssign; i++) {
//                Map<String, Object> staff = jobStaff.get(i);
//                String employeeId = staff.get("id").toString();
//
//                Scheduling scheduling = new Scheduling();
//                scheduling.setEmployeeId(employeeId);
//                scheduling.setShiftId(shiftId);
//                scheduling.setShiftTimeId(shiftTime.getId());
//                scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
//                scheduling.setFarmId(farmId);
//                scheduling.setJobId(jobId);
//                scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());
//                scheduling.setSchedulePeopleType(SchedulePeopleType.ONDUTY.getKey());
//
//                // 设置权重
//                scheduling.setSchedulingWeight(calculateEmployeeWeights(Collections.singletonList(staff), employeeJobMap).get(employeeId));
//
//                finalSchedules.add(scheduling);
//            }
        }
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
        // 格式是 "yyyy-MM-dd"
        String endTime = commonPageInfo.getEndTime();
        // 车间id
        String holderId = commonPageInfo.getHolderId();
        // 获取当前日期和时间, 格式为 "yyyy-MM-dd"
        String timeAndToString = DateUtil.getYmdTimeAndToString();
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(holderId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getFarmId), holderId);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            queryWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), endTime);
        }
        queryWrapper.ge(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), timeAndToString);
        List<Scheduling> schedulingList = list(queryWrapper);
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

    /**
     * 为相邻时间段设置不同颜色
     * 颜色规则：
     * 1. 相邻时间段不能使用相同颜色
     * 2. 使用5种基本颜色循环
     * 3. 按时间顺序设置颜色
     */
    private void setColorsForAdjacentTimeSlots(List<Scheduling> schedules) {
        String[] colors = {"green", "blue", "orange", "red", "gray"};

        // 按日期和时间排序
        schedules.sort((a, b) -> {
            int dateCompare = a.getScheduleDate().compareTo(b.getScheduleDate());
            if (dateCompare != 0) {
                return dateCompare;
            }
            // 获取时间段信息
            SchedulingShiftsTime timeA = schedulingShiftsTimeService.getById(a.getShiftTimeId());
            SchedulingShiftsTime timeB = schedulingShiftsTimeService.getById(b.getShiftTimeId());
            return timeA.getStartTime().compareTo(timeB.getStartTime());
        });

        // 为每个时间段设置颜色
        Map<String, String> timeSlotColors = new HashMap<>();
        int colorIndex = 0;
        String lastColor = null;

        for (Scheduling schedule : schedules) {
            String timeSlotId = schedule.getShiftTimeId();

            // 如果这个时间段还没有颜色
            if (!timeSlotColors.containsKey(timeSlotId)) {
                // 找到与上一个颜色不同的颜色
                String color;
                do {
                    color = colors[colorIndex % colors.length];
                    colorIndex++;
                } while (color.equals(lastColor));

                timeSlotColors.put(timeSlotId, color);
                lastColor = color;
            }

            // 设置颜色
            SchedulingShiftsTime timeSlot = schedulingShiftsTimeService.getById(timeSlotId);
            timeSlot.setColor(timeSlotColors.get(timeSlotId));
            schedulingShiftsTimeService.updateById(timeSlot);
        }
    }

}
