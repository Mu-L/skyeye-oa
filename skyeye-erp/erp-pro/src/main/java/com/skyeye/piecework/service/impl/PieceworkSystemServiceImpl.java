package com.skyeye.piecework.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "计件数量或工时统计信息", groupName = "计件数量或工时统计信息", manageShow = false)
public class PieceworkSystemServiceImpl extends SkyeyeBusinessServiceImpl<PieceworkSystemDao, PieceworkSystem> implements PieceworkSystemService {

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
        Map<String, Object> staffMation = iAuthUserService.queryDataMationById(staffId);
        // 小时工
        if (staffMation.get("workstationType").equals(CommonNumConstants.NUM_TWO)) {
            // 获取小时工的一小时单价
            String hourlyPrice = staffMation.get("hourlyPrice").toString();
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(Collections.singletonList(staffId));
            // 获取当前月

//            List<Map<String, Object>> checkWorkInfo = iCheckWorkService.queryInfoByStaffIdsAndDates(
//                Joiner.on(CommonCharConstants.COMMA_MARK).join(Collections.singletonList(staffId)), Joiner.on(CommonCharConstants.COMMA_MARK).join(betweenDates));
//            Map<String, List<String>> workHourListMap = checkWorkInfo.stream()
//                .collect(Collectors.groupingBy(m -> m.get("createId").toString(), Collectors.mapping(m -> m.get("workHours").toString(), Collectors.toList())));


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

            for (MachinProcedureAcceptProductNum numRecord : machinProductNumList) {
                // 获取关联的工序验收单
                MachinProcedureAccept accept = acceptMap.get(numRecord.getParentId());
                if (ObjectUtil.isEmpty(accept)) continue;

                // 获取工序信息
                MachinProcedure procedure = procedureMap.get(accept.getMachinProcedureId());
                if (ObjectUtil.isEmpty(procedure) || StrUtil.isEmpty(procedure.getActualStartTime()) || StrUtil.isEmpty(procedure.getActualEndTime()))
                    continue;

                // 解析日期
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(procedure.getActualStartTime(), formatter);
                LocalDate endDate = LocalDate.parse(procedure.getActualEndTime(), formatter);

                // 计算工序持续天数
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (days <= 0) continue;

                // 计算每日平均数量
                double dailyAvg = (double) numRecord.getAllNumber() / days;

                // 分配到每一天
                LocalDate currentDate = startDate;
                while (!currentDate.isAfter(endDate)) {
                    YearMonth yearMonth = YearMonth.from(currentDate);
                    int dayOfMonth = currentDate.getDayOfMonth();

                    // 更新月份数据
                    monthlyData.computeIfAbsent(yearMonth, k -> new HashMap<>())
                        .merge(dayOfMonth, dailyAvg, Double::sum);

                    currentDate = currentDate.plusDays(CommonNumConstants.NUM_ONE);
                }
            }

            // 处理每月数据
            for (Map.Entry<YearMonth, Map<Integer, Double>> entry : monthlyData.entrySet()) {
                YearMonth yearMonth = entry.getKey();
                Map<Integer, Double> dayCountMap = entry.getValue();

                // 计算总数量
                double totalNum = dayCountMap.values().stream().mapToDouble(Double::doubleValue).sum();

                // 获取计件单价（取第一个车间的单价）
                BigDecimal totalNumPrice = new BigDecimal(farmStaffList.get(0).getPieceWorkPrice());

                PieceworkSystem pieceworkSystem = new PieceworkSystem();
                pieceworkSystem.setJobNumber(staffMation.get("jobNumber").toString());
                pieceworkSystem.setDepartmentId(staffMation.get("departmentId").toString());
                pieceworkSystem.setIsNumTime(CommonNumConstants.NUM_ONE);
                pieceworkSystem.setTotalNumPrice(totalNumPrice);
                pieceworkSystem.setAllNum(BigDecimal.valueOf(totalNum));
                pieceworkSystem.setDayMouth(yearMonth.toString());

                // 设置31天的计件数量
                for (int day = 1; day <= 31; day++) {
                    String fieldName = "day" + day;
                    double count = dayCountMap.getOrDefault(day, 0.0);
                    try {
                        PieceworkSystem.class.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), BigDecimal.class)
                            .invoke(pieceworkSystem, BigDecimal.valueOf(count));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 设置创建信息
                super.createEntity(pieceworkSystem, inputObject.getLogParams().get("id").toString());
            }
        }

    }
}
