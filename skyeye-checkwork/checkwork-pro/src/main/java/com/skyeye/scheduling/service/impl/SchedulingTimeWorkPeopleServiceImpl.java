package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.erp.service.IFarmStaffService;
import com.skyeye.scheduling.dao.SchedulingTimeWorkPeopleDao;
import com.skyeye.scheduling.entity.Scheduling;
import com.skyeye.scheduling.entity.SchedulingLeave;
import com.skyeye.scheduling.entity.SchedulingTime;
import com.skyeye.scheduling.entity.SchedulingTimeWorkPeople;
import com.skyeye.scheduling.service.SchedulingLeaveService;
import com.skyeye.scheduling.service.SchedulingService;
import com.skyeye.scheduling.service.SchedulingTimeService;
import com.skyeye.scheduling.service.SchedulingTimeWorkPeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排班工位下员工管理", groupName = "排班工位下员工管理")
public class SchedulingTimeWorkPeopleServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingTimeWorkPeopleDao, SchedulingTimeWorkPeople> implements SchedulingTimeWorkPeopleService {

    @Autowired
    private SchedulingLeaveService schedulingLeaveService;

    @Autowired
    private SchedulingService schedulingService;

    @Autowired
    private SchedulingTimeService schedulingTimeService;

    @Autowired
    private IFarmStaffService iFarmStaffService;

    @Override
    public List<SchedulingTimeWorkPeople> queryTimeWorkByThreeId(String id, List<String> timeIds, List<String> timeWorkIds) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), id);
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeId), timeIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeWorkId), timeWorkIds);
        List<SchedulingTimeWorkPeople> timeWorkPeopleList = list(queryWrapper);
        String employIds = String.join(CommonCharConstants.COMMA_MARK, timeWorkPeopleList.stream()
            .map(SchedulingTimeWorkPeople::getEmployeeId).collect(Collectors.toList()));
        List<Map<String, Object>> allStaffList = iAuthUserService.queryDataMationByIds(employIds);
        timeWorkPeopleList.forEach(
            staff -> {
                String employeeId = staff.getEmployeeId();
                Map<String, Object> staffMap = allStaffList.stream().filter(map -> ObjectUtil.equal(map.get("id"), employeeId)).findFirst().orElse(null);
                if (ObjectUtil.isNotEmpty(staffMap)) {
                    staff.setStaffMation(staffMap);
                }
            }
        );
        iAuthUserService.setName(timeWorkPeopleList, "createId", "createName");
        iAuthUserService.setName(timeWorkPeopleList, "lastUpdateId", "lastUpdateName");
        return timeWorkPeopleList;
    }

    @Override
    public void deleteBySchedulingTimeIdsAndOthorId(List<String> schedulingTimeWorkIds) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeId), schedulingTimeWorkIds);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTimeWorkPeople> queryPeopleByThreeId(String id, String schedulingId, String
        schedulingTimeId) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), schedulingId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeId), schedulingTimeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeWorkId), id);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySchedulingTimeIds(List<String> deleteIdList) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, deleteIdList);
        remove(queryWrapper);
    }

    @Override
    public void deleteSchedulingTimeWorkPeopleByTimeIds(List<String> allDeleteIds) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingTimeWorkId), allDeleteIds);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTimeWorkPeople> querySchedulingByschedulingIdsAndStaffId(List<String> schedulingIds, String staffId) {
        if (CollectionUtil.isEmpty(schedulingIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getEmployeeId), staffId);
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), schedulingIds);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySchedulingIds(List<String> ids) {
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getSchedulingId), ids);
        remove(queryWrapper);
    }

    @Override
    public List<SchedulingTimeWorkPeople> findSchedulingTimeByEmployeeIdList(List<String> employeeIdList) {
        if (CollectionUtil.isEmpty(employeeIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getEmployeeId), employeeIdList);
        return list(queryWrapper);
    }

    @Override
    public void trackEmployeeAttendanceLeaveTime(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String startTime = commonPageInfo.getStartTime();
        String endTime = commonPageInfo.getEndTime();
        String farmId = commonPageInfo.getFromId();
        // 获取所有员工信息
        List<Map<String, Object>> satffMation = iFarmStaffService.queryStaffByFarmId(farmId);
        if (CollectionUtil.isEmpty(satffMation)) {
            outputObject.setBean(Collections.emptyList());
            return;
        }
        Map<String, String> employeeNameMap = satffMation.stream()
            .collect(Collectors.toMap(
                staff -> staff.get("staffId").toString(),
                staff -> staff.get("userName").toString()
            ));

        // 存储所有员工的统计结果
        List<Map<String, Object>> finalResult = new ArrayList<>();

        for (Map<String, Object> staff : satffMation) {
            String employeeId = staff.get("staffId").toString();
            String employeeName = employeeNameMap.get(employeeId);

            // 为当前员工创建统计结果
            Map<String, Object> employeeResult = new HashMap<>();
            employeeResult.put("employeeId", employeeId);
            employeeResult.put("employeeName", employeeName);

            // 1. 获取当前员工的排班记录
            QueryWrapper<SchedulingTimeWorkPeople> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SchedulingTimeWorkPeople::getEmployeeId), employeeId);
            List<SchedulingTimeWorkPeople> workPeopleList = list(queryWrapper);
            if (CollectionUtil.isNotEmpty(workPeopleList)) {
                workPeopleList = workPeopleList.stream()
                    .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                            p -> Arrays.asList(
                                p.getSchedulingTimeWorkId(),
                                p.getEmployeeId(),
                                p.getSchedulingId(),
                                p.getSchedulingTimeId()),
                            p -> p,
                            (existing, replacement) -> existing
                        ),
                        map -> new ArrayList<>(map.values())
                    ));
            }
            // 初始化工作统计变量
            long totalSeconds = 0;
            int totalShifts = 0;
            double totalLeaveHours = 0;
            int leaveCount = 0;

            // 2. 统计工作时间（仅当有排班记录时）
            if (CollectionUtil.isNotEmpty(workPeopleList)) {
                // 获取排班ID并查询排班信息
                List<String> schedulingIds = workPeopleList.stream()
                    .map(SchedulingTimeWorkPeople::getSchedulingId)
                    .distinct()
                    .collect(Collectors.toList());
                List<Scheduling> schedulingList = schedulingService.querySchedulingByIdList(schedulingIds);

                // 筛选日期范围内有交集的排班
                List<Scheduling> filteredSchedules = schedulingList.stream()
                    .filter(s -> s.getStartTime().compareTo(endTime) <= 0 &&
                        s.getEndTime().compareTo(startTime) >= 0)
                    .collect(Collectors.toList());

                if (CollectionUtil.isNotEmpty(filteredSchedules)) {
                    // 获取有效排班下的员工记录
                    List<SchedulingTimeWorkPeople> yesTimeWorkPeople = workPeopleList.stream()
                        .filter(wp -> filteredSchedules.stream()
                            .anyMatch(s -> s.getId().equals(wp.getSchedulingId())))
                        .collect(Collectors.toList());

                    // 获取排班时间段并构建快速查找Map
                    List<String> timeIds = yesTimeWorkPeople.stream()
                        .map(SchedulingTimeWorkPeople::getSchedulingTimeId)
                        .distinct()
                        .collect(Collectors.toList());
                    List<SchedulingTime> schedulingTimes = schedulingTimeService.querySchedulingTimeByIds(timeIds);

                    Map<String, Scheduling> scheduleMap = filteredSchedules.stream()
                        .collect(Collectors.toMap(Scheduling::getId, s -> s));
                    Map<String, SchedulingTime> timeMap = schedulingTimes.stream()
                        .collect(Collectors.toMap(SchedulingTime::getId, t -> t));

                    // 计算工作时间和班次
                    for (SchedulingTimeWorkPeople workPeople : yesTimeWorkPeople) {
                        Scheduling schedule = scheduleMap.get(workPeople.getSchedulingId());
                        SchedulingTime time = timeMap.get(workPeople.getSchedulingTimeId());
                        if (ObjectUtil.isEmpty(schedule) || ObjectUtil.isEmpty(time)) continue;
                        List<LocalDate> workDates = getOverlapDates(
                            schedule.getStartTime(), schedule.getEndTime(),
                            startTime, endTime
                        );
                        long durationSeconds = calculateDuration(time);
                        totalSeconds += durationSeconds * workDates.size();
                        totalShifts += workDates.size();
                    }
                }
            }

            // 3. 统计请假时间
            List<SchedulingLeave> schedulingLeaveList = schedulingLeaveService.querySchedulingLeaveByEmployeeId(employeeId);
            if (CollectionUtil.isNotEmpty(schedulingLeaveList)) {
                LocalDate queryStartDate = LocalDate.parse(startTime);
                LocalDate queryEndDate = LocalDate.parse(endTime);

                for (SchedulingLeave leave : schedulingLeaveList) {
                    LocalDateTime leaveStart = LocalDateTime.parse(leave.getStartTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime leaveEnd = LocalDateTime.parse(leave.getEndTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    LocalDate leaveStartDate = leaveStart.toLocalDate();
                    LocalDate leaveEndDate = leaveEnd.toLocalDate();

                    // 检查日期交集
                    if (leaveEndDate.isBefore(queryStartDate) || leaveStartDate.isAfter(queryEndDate)) {
                        continue;
                    }

                    // 计算实际请假时间
                    LocalDateTime effectiveStart = leaveStart.isBefore(queryStartDate.atStartOfDay())
                        ? queryStartDate.atStartOfDay()
                        : leaveStart;

                    LocalDateTime effectiveEnd = leaveEnd.isAfter(queryEndDate.atTime(23, 59, 59))
                        ? queryEndDate.atTime(23, 59, 59)
                        : leaveEnd;

                    // 计算时长并累加
                    long seconds = Duration.between(effectiveStart, effectiveEnd).getSeconds();
                    double hours = seconds / 3600.0;
                    if (hours > 0) {
                        totalLeaveHours += hours;
                        leaveCount++;
                    }
                }
            }

            double totalWorkHours = Math.round(totalSeconds / 3600.0 * 100) / 100.0;
            totalLeaveHours = Math.round(totalLeaveHours * 100) / 100.0;

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalWorkHours", totalWorkHours);
            stats.put("totalShifts", totalShifts);
            stats.put("totalLeaveHours", totalLeaveHours);
            stats.put("leaveCount", leaveCount);

            employeeResult.put("result", Collections.singletonList(stats));
            finalResult.add(employeeResult);
        }
        String name = commonPageInfo.getKeyword();
        Integer pageNum = commonPageInfo.getPage();
        Integer pageSize = commonPageInfo.getLimit();
        if (pageNum == null || pageNum <= CommonNumConstants.NUM_ZERO) {
            pageNum = CommonNumConstants.NUM_ONE;
        }
        if (pageSize == null || pageSize <= CommonNumConstants.NUM_ZERO) {
            pageSize = CommonNumConstants.NUM_TEN;
        }
        List<Map<String, Object>> filteredList = new ArrayList<>();
        //对name模糊查询
        if (StrUtil.isEmpty(name)) {
            filteredList = new ArrayList<>(finalResult);
        } else {
            filteredList = finalResult.stream().filter(
                map1 -> {
                    Object employeeName = map1.get("employeeName");
                    if (ObjectUtil.isEmpty(employeeName)) {
                        return false;
                    }
                    String stringName = employeeName.toString();
                    return stringName.contains(name);
                }
            ).collect(Collectors.toList());

        }
        Integer total = filteredList.size();
        List<Map<String, Object>> paginatedList = new ArrayList<>();
        if (total > CommonNumConstants.NUM_ZERO) {
            // 计算起始索引
            int startIndex = (pageNum - CommonNumConstants.NUM_ONE) * pageSize;
            startIndex = Math.max(startIndex, CommonNumConstants.NUM_ZERO);
            // 计算结束索引
            int endIndex = Math.min(startIndex + pageSize, total);
            // 截取集合，得到分页数据
            paginatedList = filteredList.subList(startIndex, endIndex);
        }
        outputObject.setBeans(paginatedList);
        outputObject.settotal(total);
    }

    private List<LocalDate> getOverlapDates(String scheduleStart, String scheduleEnd, String queryStart, String queryEnd) {
        LocalDate sStart = LocalDate.parse(scheduleStart);
        LocalDate sEnd = LocalDate.parse(scheduleEnd);
        LocalDate qStart = LocalDate.parse(queryStart);
        LocalDate qEnd = LocalDate.parse(queryEnd);

        LocalDate realStart = sStart.isAfter(qStart) ? sStart : qStart;
        LocalDate realEnd = sEnd.isBefore(qEnd) ? sEnd : qEnd;

        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate date = realStart; !date.isAfter(realEnd); date = date.plusDays(1)) {
            dates.add(date);
        }
        return dates;
    }

    private long calculateDuration(SchedulingTime time) {
        LocalTime start = LocalTime.parse(time.getStartTime());
        LocalTime end = LocalTime.parse(time.getEndTime());
        if (end.isBefore(start) || time.getIsNextDay() == 1) {
            // 跨天时间段：从开始时间到24:00 + 00:00到结束时间
            return Duration.between(start, LocalTime.MAX).getSeconds() + 1
                + Duration.between(LocalTime.MIN, end).getSeconds();
        } else {
            // 当天时间段：直接计算持续时间
            return Duration.between(start, end).getSeconds();
        }
    }
}
