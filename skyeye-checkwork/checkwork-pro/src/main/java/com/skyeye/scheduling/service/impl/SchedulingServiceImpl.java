package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
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
import com.skyeye.scheduling.service.SchedulingLeaveService;
import com.skyeye.scheduling.service.SchedulingService;
import com.skyeye.scheduling.service.SchedulingShiftsService;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    //配置整体规划算法的静态代码块
    static {
        try {
            // 通过类加载器获取资源路径
            ClassLoader classLoader = SchedulingServiceImpl.class.getClassLoader();
            URL resourceUrl = classLoader.getResource("lib/jniortools.dll");

            if (resourceUrl == null) {
                throw new UnsatisfiedLinkError("jniortools.dll未在资源目录中找到");
            }

            // 转换URL为文件路径并处理特殊字符
            String dllPath = URLDecoder.decode(resourceUrl.getFile(), "UTF-8");

            // 处理Windows路径前的斜杠问题
            if (dllPath.startsWith("/") && System.getProperty("os.name").contains("Windows")) {
                dllPath = dllPath.substring(1);
            }

            // 加载库
            System.load(dllPath);
        } catch (UnsupportedEncodingException e) {
            throw new CustomException("路径解码失败", e);
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

        // 时间范围内的所有日期
        List<LocalDate> dateRange = generateDateRange(startTime, endTime);

        // 遍历每个车间进行排班
        for (String farmId : farmIds) {
            List<Map<String, Object>> farmStaff = staffFarmMap.get(farmId);

            // 分离每个车间中正式员工和临时员工
            List<Map<String, Object>> farmStaffWithUserId = new ArrayList<>();
            List<Map<String, Object>> farmStaffWithoutUserId = new ArrayList<>();
            Set<String> userIds = new HashSet<>();

            for (Map<String, Object> staff : farmStaff) {
                String staffId = staff.get("staffId").toString();
                Map<String, Object> staffInfo = staffMap.get(staffId);
                if (!CollectionUtils.isEmpty(staffInfo)) {
                    String userId = Objects.toString(staffInfo.get("userId"), "");
                    if (StrUtil.isNotEmpty(userId)) {
                        farmStaffWithUserId.add(staffInfo);
                        userIds.add(userId);
                    } else {
                        farmStaffWithoutUserId.add(staffInfo);
                    }
                }
            }

            // 检查临时员工集合中是否有与正式员工重复的id
            farmStaffWithoutUserId.removeIf(staff -> userIds.contains(staff.get("id").toString()));
            Set<String> allEmployeeIds = allStaffList.stream()
                .map(staff -> staff.get("id").toString())
                .collect(Collectors.toSet());
            farmStaffWithoutUserId.removeIf(staff -> !allEmployeeIds.contains(staff.get("id").toString()));

            // 获取正式员工的请假和出差信息
            Map<String, List<LeaveTimeSlot>> leaveList = leaveService.queryStateIsSuccessLeaveDayByUserId(startTime, endTime, farmStaffWithUserId);
            Map<String, List<BusinessTripTimeSlot>> businessTripList = businessTripService.queryStateIsSuccessBusinessTripDayByUserId(startTime, endTime, farmStaffWithUserId);
            // 获取临时员工的请假信息
            Map<String, List<SchedulingLeave>> schedulingLeaveMap = schedulingLeaveService.queryStateIsSuccessLeaveDayByUserId(startTime, endTime, farmStaffWithoutUserId);

            // 正式员工排班
            Map<String, Map<LocalDate, Scheduling>> scheduleResult = solveIntegerProgramming(
                farmStaffWithUserId, shifts, dateRange, leaveList, businessTripList, farmId
            );

            // 临时员工排班
            Map<String, Map<LocalDate, Scheduling>> tempScheduleResult = solveSchedulingProgramming(
                farmStaffWithoutUserId, shifts, dateRange, schedulingLeaveMap, farmId
            );

            // 合并正式员工的排班结果
            for (Map.Entry<String, Map<LocalDate, Scheduling>> entry : scheduleResult.entrySet()) {
                String userId = entry.getKey();
                Map<LocalDate, Scheduling> employeeSchedule = entry.getValue();

                // 合并相同日期的排班记录
                Map<LocalDate, Scheduling> mergedSchedule = new HashMap<>();
                for (Map.Entry<LocalDate, Scheduling> scheduleEntry : employeeSchedule.entrySet()) {
                    LocalDate date = scheduleEntry.getKey();
                    Scheduling scheduling = scheduleEntry.getValue();

                    // 如果已经存在排班记录，跳过
                    if (mergedSchedule.containsKey(date)) {
                        continue;
                    }

                    mergedSchedule.put(date, scheduling);
                }

                // 保存合并后的排班记录
                List<Scheduling> schedulingList = mergedSchedule.values().stream().collect(Collectors.toList());
                super.createEntity(schedulingList, userId);
            }

            // 合并临时员工的排班结果
            for (Map.Entry<String, Map<LocalDate, Scheduling>> entry : tempScheduleResult.entrySet()) {
                String employeeId = entry.getKey();
                Map<LocalDate, Scheduling> employeeSchedule = entry.getValue();

                // 合并相同日期的排班记录
                Map<LocalDate, Scheduling> mergedSchedule = new HashMap<>();
                for (Map.Entry<LocalDate, Scheduling> scheduleEntry : employeeSchedule.entrySet()) {
                    LocalDate date = scheduleEntry.getKey();
                    Scheduling scheduling = scheduleEntry.getValue();

                    // 如果已经存在排班记录，跳过
                    if (mergedSchedule.containsKey(date)) {
                        continue;
                    }

                    mergedSchedule.put(date, scheduling);
                }

                // 保存合并后的排班记录
                List<Scheduling> schedulingList = mergedSchedule.values().stream().collect(Collectors.toList());
                super.createEntity(schedulingList, employeeId);
            }
        }
    }

    /**
     * 临时员工的整数规划求解排班问题
     */
    private Map<String, Map<LocalDate, Scheduling>> solveSchedulingProgramming(
        List<Map<String, Object>> staffListWithoutUserId,
        List<SchedulingShifts> shifts,
        List<LocalDate> dates,
        Map<String, List<SchedulingLeave>> leaveMap,
        String farmId
    ) {
        List<Map<String, Object>> validStaffList = staffListWithoutUserId.stream()
            .filter(staff -> StrUtil.isNotEmpty(staff.get("id").toString()))
            .collect(Collectors.toList());

        // 初始化OR-Tools求解器
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new CustomException("OR-Tools求解器初始化失败");
        }
        // 设置求解器超时时间（单位：毫秒）
        solver.setTimeLimit(20_000);
        // 定义变量：x[staffIndex][dateIndex][shiftIndex] = 1表示员工在该日期被分配到此班次
        int numStaff = validStaffList.size();
        int numDates = dates.size();
        int numShifts = shifts.size();
        MPVariable[][][] x = new MPVariable[numStaff][numDates][numShifts];

        // 创建变量并添加约束
        for (int s = 0; s < numStaff; s++) {
            Map<String, Object> staffInfo = validStaffList.get(s);
            if (ObjectUtils.isEmpty(staffInfo)) {
                continue;
            }

            String staffId = staffInfo.get("id").toString();
            for (int d = 0; d < numDates; d++) {
                LocalDate date = dates.get(d);

                // 获取员工状态（1=在职，2=请假）
                int status = getStaffStatus(staffId, date, leaveMap);
                boolean isAvailable = (status == 1);

                for (int sh = 0; sh < numShifts; sh++) {
                    x[s][d][sh] = solver.makeBoolVar("x_" + s + "_" + d + "_" + sh);
                    if (!isAvailable) {
                        x[s][d][sh].setBounds(0, 0);
                    }
                }

                // 每天最多一个班次约束
                MPConstraint constraint = solver.makeConstraint(0, 1);
                for (int sh = 0; sh < numShifts; sh++) {
                    constraint.setCoefficient(x[s][d][sh], 1);
                }
            }
        }

        // 约束：每个班次每天人数满足[minStaff, maxStaff]
        for (int d = 0; d < numDates; d++) {
            for (int sh = 0; sh < numShifts; sh++) {
                SchedulingShifts shift = shifts.get(sh);

                // 计算当天可用的员工数量
                int availableStaff = 0;
                for (int s = 0; s < numStaff; s++) {
                    Map<String, Object> staffInfo = validStaffList.get(s);
                    String staffId = staffInfo.get("id").toString();
                    int status = getStaffStatus(staffId, dates.get(d), leaveMap);
                    if (status == 1)
                        availableStaff++;
                }

                // 动态调整最小需求（关键逻辑）
                int minStaff = Math.min(shift.getMinStaff(), availableStaff);
                MPConstraint minConstraint = solver.makeConstraint(minStaff, Double.POSITIVE_INFINITY);
                MPConstraint maxConstraint = solver.makeConstraint(0, shift.getMaxStaff());

                for (int s = 0; s < numStaff; s++) {
                    minConstraint.setCoefficient(x[s][d][sh], 1);
                    maxConstraint.setCoefficient(x[s][d][sh], 1);
                }
            }
        }

        // 目标函数：最小化总排班次数（可根据需求调整，例如均衡负载）
        MPObjective objective = solver.objective();
        for (int s = 0; s < numStaff; s++) {
            for (int d = 0; d < numDates; d++) {
                for (int sh = 0; sh < numShifts; sh++) {
                    // 权重系数可根据需求调整，此处示例为1.0
                    objective.setCoefficient(x[s][d][sh], 1.0);
                }
            }
        }
        objective.setMaximization();

        // 求解
        MPSolver.ResultStatus resultStatus = solver.solve();
        // 处理结果时构建 Scheduling 对象
        Map<String, Map<LocalDate, Scheduling>> schedule = new HashMap<>();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            for (int s = 0; s < numStaff; s++) {
                Map<String, Object> staffInfo = validStaffList.get(s);
                if (CollectionUtils.isEmpty(staffInfo)) {
                    continue;
                }

                String staffId = staffInfo.get("id").toString();
                Map<LocalDate, Scheduling> staffSchedule = schedule.computeIfAbsent(staffId, k -> new HashMap<>());

                for (int d = 0; d < numDates; d++) {
                    LocalDate date = dates.get(d);
                    for (int sh = 0; sh < numShifts; sh++) {
                        if (x[s][d][sh].solutionValue() == 1) {
                            Scheduling scheduling = new Scheduling();
                            scheduling.setId(IdUtil.simpleUUID());
                            scheduling.setEmployeeId(staffId);
                            scheduling.setShiftId(shifts.get(sh).getId());
                            scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
                            scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());
                            scheduling.setSchedulePeopleType(SchedulePeopleType.ONLEAVE.getKey());
                            scheduling.setFarmId(farmId);
                            staffSchedule.put(date, scheduling);
                            break;
                        }
                    }
                }
            }
        } else {
            throw new CustomException("整数规划求解失败");
        }
        return schedule;
    }

    /**
     * 正式员工的整数规划求解排班问题
     */
    private Map<String, Map<LocalDate, Scheduling>> solveIntegerProgramming(
        List<Map<String, Object>> staffListWithUserId,
        List<SchedulingShifts> shifts,
        List<LocalDate> dates,
        Map<String, List<LeaveTimeSlot>> leaveMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap,
        String farmId
    ) {
        List<Map<String, Object>> validStaffList = staffListWithUserId.stream()
            .filter(staff -> StrUtil.isNotEmpty(staff.get("userId").toString()))
            .collect(Collectors.toList());

        for (Map<String, Object> staffInfo : validStaffList) {
            String staffId = staffInfo.get("userId").toString();
            for (LocalDate date : dates) {
                getStaffStatus(staffId, date, leaveMap, tripMap);
            }
        }
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new CustomException("OR-Tools求解器初始化失败");
        }
        // 设置求解器超时时间（单位：毫秒）
        solver.setTimeLimit(20_000);
        int numStaff = validStaffList.size();
        int numDates = dates.size();
        int numShifts = shifts.size();
        MPVariable[][][] x = new MPVariable[numStaff][numDates][numShifts];

        for (int s = 0; s < numStaff; s++) {
            Map<String, Object> staffInfo = validStaffList.get(s);
            String staffId = staffInfo.get("userId").toString();
            for (int d = 0; d < numDates; d++) {
                LocalDate date = dates.get(d);
                int status = getStaffStatus(staffId, date, leaveMap, tripMap);
                boolean isAvailable = (status == 1);

                for (int sh = 0; sh < numShifts; sh++) {
                    x[s][d][sh] = solver.makeBoolVar("x_" + s + "_" + d + "_" + sh);
                    if (!isAvailable) {
                        x[s][d][sh].setBounds(0, 0);
                    }
                }

                MPConstraint constraint = solver.makeConstraint(0, 1);
                for (int sh = 0; sh < numShifts; sh++) {
                    constraint.setCoefficient(x[s][d][sh], 1);
                }
            }
        }

        // 约束：每个班次每天人数满足动态调整的最小需求
        for (int d = 0; d < numDates; d++) {
            LocalDate currentDate = dates.get(d);
            // 仅计算当前车间可用员工
            int availableStaffInFarm = 0;
            for (int s = 0; s < numStaff; s++) {
                Map<String, Object> staffInfo = validStaffList.get(s);
                String staffId = staffInfo.get("userId").toString();
                int status = getStaffStatus(staffId, currentDate, leaveMap, tripMap);
                if (status == 1) availableStaffInFarm++;
            }

            // 遍历所有班次，确保总需求不超过可用员工数
            int totalMinStaff = 0;
            for (int sh = 0; sh < numShifts; sh++) {
                SchedulingShifts shift = shifts.get(sh);
                int maxStaff = shift.getMaxStaff();

                // 动态约束：确保当前车间班次人数不超过其最大限制
                MPConstraint maxConstraint = solver.makeConstraint(0, maxStaff);
                for (int s = 0; s < numStaff; s++) {
                    maxConstraint.setCoefficient(x[s][d][sh], 1);
                }

                // 调试
                System.out.printf(
                    "车间 %s 日期 %s 班次 %s: 最大人数=%d, 当前可用员工=%d\n",
                    farmId, currentDate, shift.getShiftName(), maxStaff, availableStaffInFarm
                );
            }
        }

        // 目标函数：最小化总排班次数（可根据需求调整，例如均衡负载）
        MPObjective objective = solver.objective();
        for (int s = 0; s < numStaff; s++) {
            for (int d = 0; d < numDates; d++) {
                for (int sh = 0; sh < numShifts; sh++) {
                    // 权重系数可根据需求调整
                    objective.setCoefficient(x[s][d][sh], 1.0);
                }
            }
        }
        objective.setMaximization();

        // 求解
        MPSolver.ResultStatus resultStatus = solver.solve();

        // 处理结果构建 Scheduling 对象
        Map<String, Map<LocalDate, Scheduling>> schedule = new HashMap<>();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            for (int s = 0; s < numStaff; s++) {
                Map<String, Object> staffInfo = validStaffList.get(s);
                String staffId = staffInfo.get("userId").toString();
                Map<LocalDate, Scheduling> staffSchedule = schedule.computeIfAbsent(staffId, k -> new HashMap<>());

                for (int d = 0; d < numDates; d++) {
                    LocalDate date = dates.get(d);
                    for (int sh = 0; sh < numShifts; sh++) {
                        if (x[s][d][sh].solutionValue() == 1) {
                            Scheduling scheduling = new Scheduling();
                            scheduling.setId(IdUtil.simpleUUID());
                            scheduling.setEmployeeId(staffId);
                            scheduling.setShiftId(shifts.get(sh).getId());
                            scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
                            scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());
                            scheduling.setSchedulePeopleType(SchedulePeopleType.ONDUTY.getKey()); // 正式员工
                            scheduling.setFarmId(farmId);
                            staffSchedule.put(date, scheduling);
                            break;
                        }
                    }
                }
            }
        } else {
            throw new CustomException("整数规划求解失败");
        }
        return schedule;
    }

    private int getStaffStatus(String staffId, LocalDate date, Map<String, List<SchedulingLeave>> leaveMap) {
        // 检查请假
        List<SchedulingLeave> leaves = leaveMap.getOrDefault(staffId, Collections.emptyList());
        for (SchedulingLeave leave : leaves) {
            // 请假的开始时间和结束时间
            LocalDateTime leaveStart = LocalDateTime.parse(leave.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime leaveEnd = LocalDateTime.parse(leave.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime checkDate = LocalDateTime.of(date, LocalTime.MIN);

            // 检查日期是否在请假时间段内
            if (!checkDate.isBefore(leaveStart) && !checkDate.isAfter(leaveEnd)) {
                // 请假
                return 2;
            }
        }
        // 在职
        return 1;
    }

    private int getStaffStatus(String staffId, LocalDate date,
                               Map<String, List<LeaveTimeSlot>> leaveMap,
                               Map<String, List<BusinessTripTimeSlot>> tripMap) {
        // 检查请假（精确到日期）
        List<LeaveTimeSlot> leaves = leaveMap.getOrDefault(staffId, Collections.emptyList());
        for (LeaveTimeSlot leave : leaves) {
            LocalDate leaveDate = LocalDate.parse(leave.getLeaveDay()); // 解析请假日期
            LocalTime leaveStart = LocalTime.parse(leave.getLeaveStartTime()); // 解析请假开始时间
            LocalTime leaveEnd = LocalTime.parse(leave.getLeaveEndTime()); // 解析请假结束时间

            // 检查日期是否匹配
            if (!date.isBefore(leaveDate) && !date.isAfter(leaveDate)) {
                // 检查时间是否在请假时间段内
                LocalTime nowTime = LocalTime.now();
                if (!nowTime.isBefore(leaveStart) && !nowTime.isAfter(leaveEnd)) {
                    return 2;
                }
            }
        }
        // 检查出差（精确到日期）
        List<BusinessTripTimeSlot> trips = tripMap.getOrDefault(staffId, Collections.emptyList());
        for (BusinessTripTimeSlot trip : trips) {
            LocalDate tripDate = LocalDate.parse(trip.getTravelDay());
            LocalTime tripStart = LocalTime.parse(trip.getStartTime());
            LocalTime tripEnd = LocalTime.parse(trip.getEndTime());

            // 检查日期是否匹配
            if (!date.isBefore(tripDate) && !date.isAfter(tripDate)) {
                // 检查时间是否在出差时间段内
                LocalTime nowTime = LocalTime.now();
                if (!nowTime.isBefore(tripStart) && !nowTime.isAfter(tripEnd)) {
                    return 3;
                }
            }
        }
        return 1;
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
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getFarmId), holderId);
        queryWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), endTime);
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
