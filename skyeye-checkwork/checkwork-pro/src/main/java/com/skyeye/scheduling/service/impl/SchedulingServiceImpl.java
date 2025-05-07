package com.skyeye.scheduling.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.skyeye.rest.promote.service.ISysEveUserStaffService;
import com.skyeye.scheduling.classenum.SchedulePeopleType;
import com.skyeye.scheduling.classenum.ScheduleType;
import com.skyeye.scheduling.dao.SchedulingDao;
import com.skyeye.scheduling.entity.Scheduling;
import com.skyeye.scheduling.entity.SchedulingShifts;
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

    @Override
    protected void createPrepose(Scheduling entity) {
        String employeeId = entity.getEmployeeId();
        String shiftId = entity.getShiftId();
        String scheduleDate = entity.getScheduleDate();
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getEmployeeId), employeeId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getShiftId), shiftId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), scheduleDate);
        Scheduling scheduling = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(scheduling)) {
            throw new CustomException("该员工已存在该班次");
        }
        entity.setScheduleType(ScheduleType.MANUAL.getKey());
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
            LocalTime startLocalTime = LocalTime.parse(startTime);
            LocalTime endLocalTime = LocalTime.parse(endTime);

            boolean isCrossDay = startLocalTime.isAfter(endLocalTime);

            // 如果跨日，调整逻辑
            if (isCrossDay) {
            } else {
                if (startLocalTime.isAfter(endLocalTime)) {
                    throw new CustomException("开始时间不能大于结束时间");
                }
            }
        }
        autoScheduling(startTime, endTime);
    }

    public void autoScheduling(String startTime, String endTime) {
        //获取所有员工
        List<Map<String, Object>> allStaffList = iSysEveUserStaffService.queryAllStaffList();
        // 获取所有班次
        List<SchedulingShifts> shifts = schedulingShiftsService.queryAllData();
        // 获取员工id对应的请假时间段表
        Map<String, List<LeaveTimeSlot>> leaveList = leaveService.queryStateIsSuccessLeaveDayByUserId(startTime, endTime);
        // 获取出差信息
        Map<String, List<BusinessTripTimeSlot>> businessTripList = businessTripService.queryStateIsSuccessBusinessTripDayByUserId(startTime, endTime);
        // 时间范围内的所有日期
        List<LocalDate> dateRange = generateDateRange(startTime, endTime);
        // 使用整数规划求解排班，返回 Scheduling 对象集合
        Map<String, Map<LocalDate, Scheduling>> scheduleResult = solveIntegerProgramming(
            allStaffList, shifts, dateRange, leaveList, businessTripList
        );
        // 保存排班结果（直接传递 Scheduling 对象）
        for (Map.Entry<String, Map<LocalDate, Scheduling>> entry : scheduleResult.entrySet()) {
            String userId = entry.getKey();
            for (Scheduling scheduling : entry.getValue().values()) {
                createEntity(scheduling, userId); // 参数为对象和用户ID
            }
        }
    }

    /**
     * 整数规划求解排班问题
     */
    private Map<String, Map<LocalDate, Scheduling>> solveIntegerProgramming(
        List<Map<String, Object>> staffList,
        List<SchedulingShifts> shifts,
        List<LocalDate> dates,
        Map<String, List<LeaveTimeSlot>> leaveMap,
        Map<String, List<BusinessTripTimeSlot>> tripMap
    ) {
        // 在开始处理前先过滤无效员工
        List<Map<String, Object>> validStaffList = staffList.stream()
            .filter(staff ->
                staff != null &&
                    staff.containsKey("userId") &&
                    staff.get("userId") != null &&
                    !staff.get("userId").toString().isEmpty()
            )
            .collect(Collectors.toList());

        // 初始化OR-Tools求解器
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new CustomException("OR-Tools求解器初始化失败");
        }

        // 定义变量：x[staffIndex][dateIndex][shiftIndex] = 1表示员工在该日期被分配到此班次
        // 更新员工数量为有效员工数
        int numStaff = validStaffList.size();
        int numDates = dates.size();
        int numShifts = shifts.size();
        MPVariable[][][] x = new MPVariable[numStaff][numDates][numShifts];

        // 创建变量并添加约束
        for (int s = 0; s < numStaff; s++) {
            // 获取员工信息
            // 直接从有效员工列表获取（已过滤null和userId为空的情况）
            Map<String, Object> staffInfo = validStaffList.get(s);
            if (ObjectUtils.isEmpty(staffInfo)) {
                // 如果员工信息为空，跳过当前员工
                continue;
            }

            // 获取员工 ID
            String staffId = staffInfo.get("userId").toString();
            for (int d = 0; d < numDates; d++) {
                LocalDate date = dates.get(d);

                // 获取员工状态（1=在职，2=请假，3=出差）
                int status = getStaffStatus(staffId, date, leaveMap, tripMap);
                boolean isAvailable = (status == 1);

                for (int sh = 0; sh < numShifts; sh++) {
                    x[s][d][sh] = solver.makeBoolVar("x_" + s + "_" + d + "_" + sh);
                    // 如果员工不可用（请假/出差），强制变量为0
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
                int min = Integer.parseInt(shift.getMinStaff());
                int max = Integer.parseInt(shift.getMaxStaff());
                MPConstraint minConstraint = solver.makeConstraint(min, Double.POSITIVE_INFINITY);
                MPConstraint maxConstraint = solver.makeConstraint(0, max);
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
                    objective.setCoefficient(x[s][d][sh], 1);
                }
            }
        }
        objective.setMinimization();

        // 求解
        MPSolver.ResultStatus resultStatus = solver.solve();

        // 处理结果时构建 Scheduling 对象
        Map<String, Map<LocalDate, Scheduling>> schedule = new HashMap<>();
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            for (int s = 0; s < numStaff; s++) {
                // 获取员工信息
                Map<String, Object> staffInfo = validStaffList.get(s);
                if (CollectionUtils.isEmpty(staffInfo)) {
                    // 如果员工信息为空，跳过当前员工
                    continue;
                }

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
                            scheduling.setSchedulePeopleType(SchedulePeopleType.ONDUTY.getKey()); // 排班时状态为在职
                            staffSchedule.put(date, scheduling);
                            break;
                        }
                    }
                }
            }
            for (int s = 0; s < numStaff; s++) {
                // 获取员工信息
                Map<String, Object> staffInfo = validStaffList.get(s);
                if (CollectionUtils.isEmpty(staffInfo)) {
                    // 如果员工信息为空，跳过当前员工
                    continue;
                }

                // 获取员工 ID
                String staffId = staffInfo.get("userId").toString();
                Map<LocalDate, Scheduling> staffSchedule = schedule.computeIfAbsent(staffId, k -> new HashMap<>());

                for (int d = 0; d < numDates; d++) {
                    LocalDate date = dates.get(d);
                    // 只处理没有排班记录的日期
                    if (!staffSchedule.containsKey(date)) {
                        int status = getStaffStatus(staffId, date, leaveMap, tripMap);
                        if (status != 1) {
                            Scheduling scheduling = new Scheduling();
                            scheduling.setId(IdUtil.simpleUUID());
                            scheduling.setEmployeeId(staffId);
                            scheduling.setScheduleDate(date.format(DateTimeFormatter.ISO_DATE));
                            scheduling.setScheduleType(ScheduleType.AUTOMATIC.getKey());
                            scheduling.setSchedulePeopleType(status); // 2或3
                            // 无班次信息
                            scheduling.setShiftId(null);
                            staffSchedule.put(date, scheduling);
                        }
                    }
                }
            }
            return schedule;
        } else {
            throw new CustomException("整数规划求解失败");
        }
    }

    private int getStaffStatus(String staffId, LocalDate date,
                               Map<String, List<LeaveTimeSlot>> leaveMap,
                               Map<String, List<BusinessTripTimeSlot>> tripMap) {
        String dateStr = date.format(DateTimeFormatter.ISO_DATE);
        // 检查请假
        List<LeaveTimeSlot> leaves = leaveMap.getOrDefault(staffId, Collections.emptyList());
        for (LeaveTimeSlot leave : leaves) {
            if (leave.getLeaveDay().equals(dateStr)) {
                return 2;
            }
        }
        // 检查出差
        List<BusinessTripTimeSlot> trips = tripMap.getOrDefault(staffId, Collections.emptyList());
        for (BusinessTripTimeSlot trip : trips) {
            if (trip.getTravelDay().equals(dateStr)) {
                return 3;
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
            start = start.plusDays(1); // 日期加1天
        }
        return dates;
    }

    @Override
    public void querySchedulingListByTimeSlot(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page<Object> page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 格式是 "yyyy-MM-dd"
        String endTime = commonPageInfo.getEndTime();
        // 获取当前日期和时间, 格式化为 "yyyy-MM-dd"
        String timeAndToString = DateUtil.getYmdTimeAndToString();
        QueryWrapper<Scheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.le(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), endTime);
        queryWrapper.ge(MybatisPlusUtil.toColumns(Scheduling::getScheduleDate), timeAndToString);
        List<Scheduling> schedulingList = list(queryWrapper);
        List<Map<String, Object>> allStaffList = iSysEveUserStaffService.queryAllStaffList();
        Map<String, List<Scheduling>> schedulingMap = schedulingList.stream().collect(Collectors.groupingBy(Scheduling::getEmployeeId));
        Map<String, List<Map<String, Object>>> userIdMap = allStaffList.stream()
            .filter(staffInfo -> staffInfo != null && staffInfo.get("userId") != null)
            .collect(Collectors.groupingBy(staffInfo -> staffInfo.get("userId").toString()));
        for (Map.Entry<String, List<Scheduling>> entry : schedulingMap.entrySet()) {
            String employeeId = entry.getKey();
            List<Scheduling> schedulingListForEmployee = entry.getValue();
            List<Map<String, Object>> staffInfoList = userIdMap.get(employeeId);
            if (CollectionUtil.isNotEmpty(staffInfoList)) {
                for (Scheduling scheduling : schedulingListForEmployee) {
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
