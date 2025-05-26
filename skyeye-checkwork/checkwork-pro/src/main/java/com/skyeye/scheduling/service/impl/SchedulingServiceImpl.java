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
import com.skyeye.rest.erp.service.IFarmStaffService;
import com.skyeye.rest.promote.service.ISysEveUserStaffService;
import com.skyeye.scheduling.classenum.SchedulePeopleType;
import com.skyeye.scheduling.classenum.ScheduleType;
import com.skyeye.scheduling.dao.SchedulingDao;
import com.skyeye.scheduling.entity.Scheduling;
import com.skyeye.scheduling.entity.SchedulingLeave;
import com.skyeye.scheduling.entity.SchedulingShifts;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;
import com.skyeye.scheduling.service.SchedulingLeaveService;
import com.skyeye.scheduling.service.SchedulingService;
import com.skyeye.scheduling.service.SchedulingShiftsService;
import com.skyeye.scheduling.service.SchedulingShiftsTimeService;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班管理", groupName = "排班管理")
public class SchedulingServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingDao, Scheduling> implements SchedulingService {

    @Autowired
    private SchedulingShiftsService schedulingShiftsService;

    @Autowired
    private BusinessTripService businessTripService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private ISysEveUserStaffService iSysEveUserStaffService;

    @Autowired
    private SchedulingLeaveService schedulingLeaveService;

    @Autowired
    private SchedulingShiftsTimeService schedulingShiftsTimeService;

    @Autowired
    private IFarmStaffService iFarmStaffService;

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

        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getEmployeeId), employeeIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getShiftId), shiftIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), scheduleDates);
        queryWrapper.in(MybatisPlusUtil.toColumns(Scheduling::getFarmId), farmIds);
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
    public void writeAutoScheduling(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String startTime = map.get("startTime").toString();
        String endTime = map.get("endTime").toString();
        if (StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime)) {
            LocalDate startLocalTime = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endLocalTime = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            boolean isCrossDay = endLocalTime.isAfter(startLocalTime);
            if (!isCrossDay) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
        autoScheduling(startTime, endTime);
    }

    public void autoScheduling(String startTime, String endTime) {
        // 获取车间中的信息
        List<Map<String, Object>> farmStaffList = iFarmStaffService.queryAllFarmStaffList();
        // 获取车间Id
        List<String> farmIds = farmStaffList.stream().map(map -> map.get("farmId").toString()).distinct().collect(Collectors.toList());
        // 获取车间Id对应的员工的Id
        Map<String, List<Map<String, Object>>> staffFarmMap = farmStaffList.stream().collect(Collectors.groupingBy(map -> map.get("farmId").toString()));
        // 获取所有员工
        List<Map<String, Object>> allStaffList = iSysEveUserStaffService.queryAllStaffList();
        // 创建一个Map，用于快速查找员工信息
        Map<String, Map<String, Object>> staffMap = allStaffList.stream()
            .collect(Collectors.toMap(staff -> staff.get("id").toString(), staff -> staff));
        // 获取所有班次
        List<SchedulingShifts> shifts = schedulingShiftsService.queryAllData();
        // 获取所有班次时间段
        List<SchedulingShiftsTime> allShiftTimes = schedulingShiftsTimeService.queryAllData();

        // 按班次ID分组时间段
        Map<String, List<SchedulingShiftsTime>> shiftTimeMap = allShiftTimes.stream()
            .collect(Collectors.groupingBy(SchedulingShiftsTime::getShiftId));

        // 时间范围内的所有日期
        List<LocalDate> dateRange = generateDateRange(startTime, endTime);

        // 遍历每个车间进行排班
        for (String farmId : farmIds) {
            List<Map<String, Object>> farmStaff = staffFarmMap.get(farmId);

            // 分离正式员工和临时员工
            List<Map<String, Object>> regularStaff = new ArrayList<>();
            List<Map<String, Object>> tempStaff = new ArrayList<>();

            for (Map<String, Object> staff : farmStaff) {
                String staffId = staff.get("staffId").toString();
                Map<String, Object> staffInfo = staffMap.get(staffId);
                if (staffInfo != null) {
                    if (StrUtil.isNotEmpty(Objects.toString(staffInfo.get("userId"), ""))) {
                        regularStaff.add(staffInfo);
                    } else {
                        tempStaff.add(staffInfo);
                    }
                }
            }

            // 获取请假和出差信息
            Map<String, List<LeaveTimeSlot>> leaveMap = leaveService.queryStateIsSuccessLeaveDayByUserId(
                startTime, endTime, regularStaff);
            Map<String, List<BusinessTripTimeSlot>> tripMap = businessTripService.queryStateIsSuccessBusinessTripDayByUserId(
                startTime, endTime, regularStaff);
            Map<String, List<SchedulingLeave>> tempLeaveMap = schedulingLeaveService.queryStateIsSuccessLeaveDayByUserId(
                startTime, endTime, tempStaff);

            // 为正式员工排班
            List<Scheduling> regularSchedules = scheduleForStaff(
                regularStaff, shifts, shiftTimeMap, dateRange, farmId, leaveMap, tripMap, true);

            // 为临时员工排班
            List<Scheduling> tempSchedules = scheduleForStaff(
                tempStaff, shifts, shiftTimeMap, dateRange, farmId, tempLeaveMap, null, false);

            String userId = InputObject.getLogParamsStatic().get("id").toString();
            // 保存排班结果
            if (CollectionUtil.isNotEmpty(regularSchedules)) {
                super.createEntity(regularSchedules, userId);
            }
            if (CollectionUtil.isNotEmpty(tempSchedules)) {
                super.createEntity(tempSchedules, userId);
            }
        }
    }

    /**
     * 为员工排班
     */
    private <T> List<Scheduling> scheduleForStaff(
        List<Map<String, Object>> staffList,
        List<SchedulingShifts> shifts,
        Map<String, List<SchedulingShiftsTime>> shiftTimeMap,
        List<LocalDate> dates,
        String farmId,
        Map<String, List<T>> unavailabilityMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap,
        boolean isRegularStaff) {

        List<Scheduling> schedules = new ArrayList<>();

        // 按日期和班次组织排班
        for (LocalDate date : dates) {
            // 遍历所有班次
            for (SchedulingShifts shift : shifts) {
                List<SchedulingShiftsTime> shiftTimes = shiftTimeMap.get(shift.getId());
                if (CollectionUtil.isEmpty(shiftTimes)) {
                    continue;
                }

                // 获取当前班次的所有时间段
                for (SchedulingShiftsTime shiftTime : shiftTimes) {
                    // 获取可用员工列表
                    List<Map<String, Object>> availableStaff = getAvailableStaff(
                        staffList, date, shiftTime, unavailabilityMap, tripMap);

                    // 计算需要分配的人数
                    int requiredStaff = Math.min(
                        availableStaff.size(),
                        shiftTime.getMaxStaff() > 0 ? shiftTime.getMaxStaff() : availableStaff.size()
                    );

                    // 如果没有人可用，跳过
                    if (requiredStaff <= 0) {
                        continue;
                    }

                    // 随机选择员工（可根据实际需求改为更智能的分配方式）
                    Collections.shuffle(availableStaff);
                    List<Map<String, Object>> selectedStaff = availableStaff.subList(0, Math.min(requiredStaff, availableStaff.size()));

                    // 为选中的员工创建排班记录
                    for (Map<String, Object> staff : selectedStaff) {
                        String employeeId = isRegularStaff ?
                            staff.get("userId").toString() : staff.get("id").toString();

                        Scheduling scheduling = new Scheduling();
                        scheduling.setEmployeeId(employeeId);
                        scheduling.setShiftId(shift.getId());
                        scheduling.setShiftTimeId(shiftTime.getId());
                        scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
                        scheduling.setFarmId(farmId);
                        scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());

                        // 设置员工状态（1-在职中，2-请假中，3-出差中）
                        if (unavailabilityMap != null && unavailabilityMap.containsKey(isRegularStaff ? staff.get("userId").toString() : staff.get("id").toString())) {
                            scheduling.setSchedulePeopleType(SchedulePeopleType.ONLEAVE.getKey());
                        } else if (tripMap != null && tripMap.containsKey(isRegularStaff ? staff.get("userId").toString() : staff.get("id").toString())) {
                            scheduling.setSchedulePeopleType(SchedulePeopleType.ONBUSINESSTRIP.getKey());
                        } else {
                            scheduling.setSchedulePeopleType(SchedulePeopleType.ONDUTY.getKey());
                        }
                        schedules.add(scheduling);
                    }
                }
            }
        }

        return schedules;
    }

    /**
     * 获取可用员工列表
     */
    private <T> List<Map<String, Object>> getAvailableStaff(
        List<Map<String, Object>> staffList,
        LocalDate date,
        SchedulingShiftsTime shiftTime,
        Map<String, List<T>> unavailabilityMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap) {

        List<Map<String, Object>> availableStaff = new ArrayList<>();

        for (Map<String, Object> staff : staffList) {
            String staffId = staff.get("id").toString();
            String userId = staff.containsKey("userId") ? staff.get("userId").toString() : null;

            // 检查员工是否可用
            if (isStaffAvailable(userId != null ? userId : staffId, date, shiftTime, unavailabilityMap, tripMap)) {
                availableStaff.add(staff);
            }
        }

        return availableStaff;
    }

    /**
     * 检查员工在指定日期和时间段是否可用
     */
    private <T> boolean isStaffAvailable(
        String staffId,
        LocalDate date,
        SchedulingShiftsTime shiftTime,
        Map<String, List<T>> unavailabilityMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap) {

        // 检查请假
        if (unavailabilityMap != null && unavailabilityMap.containsKey(staffId)) {
            for (T item : unavailabilityMap.get(staffId)) {
                if (item instanceof LeaveTimeSlot) {
                    LeaveTimeSlot leave = (LeaveTimeSlot) item;
                    LocalDate leaveDate = LocalDate.parse(leave.getLeaveDay());
                    if (date.equals(leaveDate)) {
                        return false;
                    }
                } else if (item instanceof SchedulingLeave) {
                    SchedulingLeave leave = (SchedulingLeave) item;
                    LocalDateTime leaveStart = LocalDateTime.parse(leave.getStartTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime leaveEnd = LocalDateTime.parse(leave.getEndTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime checkDateTime = date.atStartOfDay();
                    if (!checkDateTime.isBefore(leaveStart) && !checkDateTime.isAfter(leaveEnd)) {
                        return false;
                    }
                }
            }
        }

        // 检查出差
        if (tripMap != null && tripMap.containsKey(staffId)) {
            for (BusinessTripTimeSlot trip : tripMap.get(staffId)) {
                LocalDate tripDate = LocalDate.parse(trip.getTravelDay());
                if (date.equals(tripDate)) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<LocalDate> generateDateRange(String startTime, String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
        if (StrUtil.isNotEmpty(endTime)){
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

}
