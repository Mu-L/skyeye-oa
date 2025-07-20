package com.skyeye.piecework.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.farm.entity.FarmStaff;
import com.skyeye.farm.service.FarmStaffService;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAccept;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptProductNum;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptProductNumService;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptService;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import com.skyeye.piecework.dao.PieceworkSystemDao;
import com.skyeye.piecework.entity.PieceworkSystem;
import com.skyeye.piecework.service.PieceworkSystemService;
import com.skyeye.rest.checkwork.checkwork.ICheckWorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "计件数量或工时统计信息", groupName = "计件数量或工时统计信息", manageShow = false)
public class PieceworkSystemServiceImpl extends SkyeyeBusinessServiceImpl<PieceworkSystemDao, PieceworkSystem> implements PieceworkSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PieceworkSystemServiceImpl.class);

    @Autowired
    private FarmStaffService farmStaffService;

    @Autowired
    private MachinProcedureAcceptProductNumService machinProcedureAcceptProductNumService;

    @Autowired
    private MachinProcedureAcceptService machinProcedureAcceptService;

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private ICheckWorkService iCheckWorkService;

    @Override
    public void writePieceworkSystem(InputObject inputObject, OutputObject outputObject) {
        // 获取用户Id
        String staffId = inputObject.getLogParams().get("staffId").toString();
        // 获取用户信息
        Map<String, Map<String, Object>> map = iAuthUserService.queryUserMationListByStaffIds(Collections.singletonList(staffId));
        Map<String, Object> staffMation = map.get(staffId);
        // 小时工
        if (staffMation.get("workstationType").equals(CommonNumConstants.NUM_TWO)) {
            // 获取小时工的一小时单价
            String hourlyPrice = staffMation.get("hourlyPrice").toString();
            // 获取上一个月份
            YearMonth yearMonth = YearMonth.now();
            YearMonth lastYearMonth = yearMonth.minus(CommonNumConstants.NUM_ONE, ChronoUnit.MONTHS);
            // 上个月第一天
            String firstDay = lastYearMonth.atDay(CommonNumConstants.NUM_ONE).toString();
            // 上个月最后一天
            String lastDay = lastYearMonth.atEndOfMonth().toString();
            List<String> betweenDates = getBetweenDates(firstDay, lastDay);
            // 获取这个员工的考勤时间记录
            List<Map<String, Object>> checkWorkInfo = iCheckWorkService.queryInfoByStaffIdsAndDates(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(Collections.singletonList(staffId)),
                Joiner.on(CommonCharConstants.COMMA_MARK).join(betweenDates));

            // 按日期分组工时信息
            Map<String, List<String>> dailyWorkHoursMap = checkWorkInfo.stream()
                .collect(Collectors.groupingBy(
                    m -> m.get("checkDate").toString(),
                    Collectors.mapping(m -> m.get("workHours").toString(), Collectors.toList())
                ));

            // 计算每天的工时(小时)
            Map<String, Double> dailyWorkHoursResult = new LinkedHashMap<>();
            for (String date : betweenDates) {
                List<String> hoursList = dailyWorkHoursMap.getOrDefault(date, Collections.emptyList());
                long totalMilliseconds = hoursList.stream()
                    .filter(StrUtil::isNotEmpty)
                    .map(this::calculateHours)
                    .reduce(0L, Long::sum);

                double hours = (double) TimeUnit.MILLISECONDS.toHours(totalMilliseconds);
                dailyWorkHoursResult.put(date, hours);
            }

            BigDecimal hourlyPriceDecimal = new BigDecimal(hourlyPrice);
            // 计算每日工资和总工资
            BigDecimal totalSalary = BigDecimal.ZERO;
            Map<String, BigDecimal> dailySalaryMap = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : dailyWorkHoursResult.entrySet()) {
                String date = entry.getKey();
                Double hours = entry.getValue();
                BigDecimal daySalary = hourlyPriceDecimal.multiply(new BigDecimal(hours));
                dailySalaryMap.put(date, daySalary);
                totalSalary = totalSalary.add(daySalary);
            }

            PieceworkSystem pieceworkSystem = new PieceworkSystem();
            pieceworkSystem.setJobNumber(staffMation.get("jobNumber").toString());
            pieceworkSystem.setName(staffMation.get("userName").toString());
            pieceworkSystem.setDepartmentId(staffMation.get("departmentId").toString());

            // 设置年月（格式：yyyy-MM）
            YearMonth lastYearMonth1 = YearMonth.now().minusMonths(1);
            pieceworkSystem.setDayMouth(lastYearMonth1.toString());

            pieceworkSystem.setIsNumTime(2); // 2表示按照工时计算
            pieceworkSystem.setTotalTimePrice(hourlyPrice); // 每小时价
            pieceworkSystem.setAllTime(String.valueOf(dailyWorkHoursResult.values().stream().mapToDouble(Double::doubleValue).sum())); // 总工时
            pieceworkSystem.setAllPrice(totalSalary.toString()); // 总金额

            // 设置每日工时（最多31天）
            Map<Integer, String> dayNumMap = new HashMap<>();
            int day = 1;
            for (Map.Entry<String, Double> entry : dailyWorkHoursResult.entrySet()) {
                if (day > 31) break;
                dayNumMap.put(day, String.valueOf(entry.getValue()));
                day++;
            }

            // 填充1-31天的数据
            mouthDay(pieceworkSystem, dayNumMap);
            super.createEntity(pieceworkSystem, inputObject.getLogParamsStatic().get("id").toString());
        }
        // 计件工
        if (staffMation.get("workstationType").equals(CommonNumConstants.NUM_THREE)) {
            // 获取车间Id和计件价格
            List<FarmStaff> farmStaffList = farmStaffService.queryFarmsStaffByStaffId(staffId);
            if (farmStaffList.isEmpty()) return;

            // 获取员工所有计件记录
            List<MachinProcedureAcceptProductNum> machinProductNumList =
                machinProcedureAcceptProductNumService.queryMachinProcedureAcceptProductNumByStaffId(staffId);
            if (machinProductNumList.isEmpty()) return;

            // 获取验收单IdList
            List<String> procedureAcceptIdList = machinProductNumList.stream()
                .map(MachinProcedureAcceptProductNum::getParentId)
                .collect(Collectors.toList());

            // 获取验收单信息
            List<MachinProcedureAccept> machinProcedureAcceptList =
                machinProcedureAcceptService.queryProcedureAcceptByIds(procedureAcceptIdList);
            if (machinProcedureAcceptList.isEmpty()) return;

            // 获取车间任务ID列表
            List<String> farmTaskIds = machinProcedureAcceptList.stream()
                .map(MachinProcedureAccept::getMachinProcedureFarmId)
                .distinct()
                .collect(Collectors.toList());

            // 获取车间任务信息
            List<MachinProcedureFarm> farmTaskList = machinProcedureFarmService.listByIds(farmTaskIds);
            Map<String, MachinProcedureFarm> farmTaskMap = farmTaskList.stream()
                .collect(Collectors.toMap(MachinProcedureFarm::getId, Function.identity()));

            // 获取加工单子单据工序ID列表
            List<String> machinProcedureIdList = machinProcedureAcceptList.stream()
                .map(MachinProcedureAccept::getMachinProcedureId)
                .collect(Collectors.toList());

            // 获取加工单子单据工序信息
            List<MachinProcedure> machinProcedureList =
                machinProcedureService.queryMachinProcedureByIds(machinProcedureIdList);

            // 构建映射
            Map<String, MachinProcedureAccept> acceptMap = machinProcedureAcceptList.stream()
                .collect(Collectors.toMap(MachinProcedureAccept::getId, Function.identity()));

            Map<String, MachinProcedure> procedureMap = machinProcedureList.stream()
                .collect(Collectors.toMap(MachinProcedure::getId, Function.identity()));

            // 按月份分组统计计件数量
            Map<YearMonth, Map<Integer, Double>> monthlyData = new TreeMap<>();
            // 4. 只计算上个月的数据
            YearMonth lastMonth = YearMonth.now().minusMonths(CommonNumConstants.NUM_ONE);
            for (MachinProcedureAcceptProductNum numRecord : machinProductNumList) {
                MachinProcedureAccept accept = acceptMap.get(numRecord.getParentId());
                if (accept == null) continue;
                MachinProcedure procedure = procedureMap.get(accept.getMachinProcedureId());
                if (procedure == null || StrUtil.isEmpty(procedure.getActualStartTime())) continue;
                LocalDate startDate = LocalDate.parse(procedure.getActualStartTime(), DateTimeFormatter.ISO_DATE);
                LocalDate endDate = procedure.getActualEndTime() != null
                    ? LocalDate.parse(procedure.getActualEndTime(), DateTimeFormatter.ISO_DATE)
                    : startDate;
                // 如果工序的日期范围不包含上个月，则跳过
                if (YearMonth.from(startDate).isAfter(lastMonth) || YearMonth.from(endDate).isBefore(lastMonth)) {
                    continue;
                }
                // 计算每日平均数量
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (days <= 0) continue;
                double dailyAvg = (double) numRecord.getAllNumber() / days;
                // 只处理上个月的日期
                LocalDate currentDate = startDate;
                while (!currentDate.isAfter(endDate)) {
                    YearMonth currentYearMonth = YearMonth.from(currentDate);
                    if (!currentYearMonth.equals(lastMonth)) {
                        currentDate = currentDate.plusDays(1);
                        continue;
                    }
                    int dayOfMonth = currentDate.getDayOfMonth();
                    monthlyData
                        .computeIfAbsent(lastMonth, k -> new HashMap<>())
                        .merge(dayOfMonth, dailyAvg, Double::sum);

                    currentDate = currentDate.plusDays(1);
                }
            }
            if (!monthlyData.isEmpty()) {
                Map<Integer, Double> dayCountMap = monthlyData.get(lastMonth);
                // 检查是否已存在该月的记录（避免重复插入）
                PieceworkSystem existingRecord = queryByStaffAndMonth(
                    staffMation.get("jobNumber").toString(),
                    lastMonth.toString()
                );
                if (ObjectUtil.isEmpty(existingRecord)) {
                    // 计算总数量
                    double totalNum = dayCountMap.values().stream().mapToDouble(Double::doubleValue).sum();
                    PieceworkSystem pieceworkSystem = new PieceworkSystem();
                    pieceworkSystem.setJobNumber(staffMation.get("jobNumber").toString());
                    pieceworkSystem.setName(staffMation.get("userName").toString());
                    pieceworkSystem.setDepartmentId(staffMation.get("departmentId").toString());
                    pieceworkSystem.setFarmId(farmStaffList.get(0).getFarmId());
                    pieceworkSystem.setFarmStationId(farmStaffList.get(0).getFarmStationId());
                    pieceworkSystem.setDayMouth(lastMonth.toString());
                    pieceworkSystem.setIsNumTime(CommonNumConstants.NUM_ONE);
                    pieceworkSystem.setTotalNumPrice(new BigDecimal(farmStaffList.get(0).getPieceWorkPrice()));
                    pieceworkSystem.setAllNum(BigDecimal.valueOf(totalNum));
                    // 动态设置每日数据（day1~day31）
                    for (int day = 1; day <= 31; day++) {
                        double count = dayCountMap.getOrDefault(day, 0.0);
                        String dayFieldName = getDayFieldName(day);
                        setFieldValue(pieceworkSystem, dayFieldName, String.valueOf(count));
                    }
                    // 保存记录
                    super.createEntity(pieceworkSystem, inputObject.getLogParams().get("id").toString());
                }
            }
        }
    }

    @Override
    public void queryPieceworkSystemByUserId(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getLogParams().get("staffId").toString();
        // 获取用户信息
        Map<String, Map<String, Object>> map = iAuthUserService.queryUserMationListByStaffIds(Collections.singletonList(staffId));
        Map<String, Object> staffMation = map.get(staffId);
        String jobNumber = staffMation.get("jobNumber").toString();
        QueryWrapper<PieceworkSystem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PieceworkSystem::getJobNumber), jobNumber);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(PieceworkSystem::getDayMouth));
        outputObject.setBeans(list(queryWrapper));
        outputObject.settotal(list(queryWrapper).size());
    }

    private static final String[] DAY_FIELD_NAMES = {
        null, // 占位，使索引从1开始
        "oneDayNum", "twoDayNum", "threeDayNum", "fourDayNum", "fiveDayNum",
        "sixDayNum", "sevenDayNum", "eightDayNum", "nineDayNum", "tenDayNum",
        "elevenDayNum", "twelveDayNum", "thirteenDayNum", "fourteenDayNum", "fifteenDayNum",
        "sixteenDayNum", "seventeenDayNum", "eighteenDayNum", "nineteenDayNum", "twentyDayNum",
        "twentyOneDayNum", "twentyTwoDayNum", "twentyThreeDayNum", "twentyFourDayNum", "twentyFiveDayNum",
        "twentySixDayNum", "twentySevenDayNum", "twentyEightDayNum", "twentyNineDayNum", "thirtyDayNum",
        "thirtyOneDayNum"
    };

    private static void mouthDay(PieceworkSystem pieceworkSystem, Map<Integer, String> dayNumMap) {
        for (int day = 1; day <= 31; day++) {
            String fieldName = DAY_FIELD_NAMES[day];
            try {
                Field field = PieceworkSystem.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(pieceworkSystem, dayNumMap.getOrDefault(day, "0"));
            } catch (Exception e) {
                LOGGER.error("设置第{}天工时失败", day, e);
            }
        }
    }

    private void setFieldValue(PieceworkSystem obj, String fieldName, String value) {
        try {
            Field field = PieceworkSystem.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    private String getDayFieldName(int day) {
        switch (day) {
            case 1:
                return "oneDayNum";
            case 2:
                return "twoDayNum";
            case 3:
                return "threeDayNum";
            case 4:
                return "fourDayNum";
            case 5:
                return "fiveDayNum";
            case 6:
                return "sixDayNum";
            case 7:
                return "sevenDayNum";
            case 8:
                return "eightDayNum";
            case 9:
                return "nineDayNum";
            case 10:
                return "tenDayNum";
            case 11:
                return "elevenDayNum";
            case 12:
                return "twelveDayNum";
            case 13:
                return "thirteenDayNum";
            case 14:
                return "fourteenDayNum";
            case 15:
                return "fifteenDayNum";
            case 16:
                return "sixteenDayNum";
            case 17:
                return "seventeenDayNum";
            case 18:
                return "eighteenDayNum";
            case 19:
                return "nineteenDayNum";
            case 20:
                return "twentyDayNum";
            case 21:
                return "twentyOneDayNum";
            case 22:
                return "twentyTwoDayNum";
            case 23:
                return "twentyThreeDayNum";
            case 24:
                return "twentyFourDayNum";
            case 25:
                return "twentyFiveDayNum";
            case 26:
                return "twentySixDayNum";
            case 27:
                return "twentySevenDayNum";
            case 28:
                return "twentyEightDayNum";
            case 29:
                return "twentyNineDayNum";
            case 30:
                return "thirtyDayNum";
            case 31:
                return "thirtyOneDayNum";
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }

    public PieceworkSystem queryByStaffAndMonth(String jobNumber, String yearMonth) {
        QueryWrapper<PieceworkSystem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PieceworkSystem::getJobNumber), jobNumber)
            .eq(MybatisPlusUtil.toColumns(PieceworkSystem::getDayMouth), yearMonth);
        return getOne(queryWrapper);
    }

    /**
     * HH:MM:SS的日期转换为毫秒
     *
     * @param time1 时间1 HH:MM:SS
     * @return 两个时间之和 HH
     */
    private Long calculateHours(String time1) {
        // 将两个HH:MM:SS转换成毫秒
        String[] parts = time1.split(CommonCharConstants.COLON_MARK);
        long hours = Long.parseLong(parts[0]) * 3600 * 1000;
        long minutes = Long.parseLong(parts[1]) * 60 * 1000;
        long seconds = Long.parseLong(parts[2]) * 1000;
        return hours + minutes + seconds;
    }

    /**
     * 获取两个日期之间的所有日期YYYY-MM-dd
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return [YYYY-MM-dd,YYYY-MM-dd]
     */
    public static List<String> getBetweenDates(String startDate, String endDate) {
        List<String> result = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        while (!start.isAfter(end)) {
            result.add(start.toString());
            start = start.plusDays(CommonNumConstants.NUM_ONE);
        }
        return result;
    }
}
