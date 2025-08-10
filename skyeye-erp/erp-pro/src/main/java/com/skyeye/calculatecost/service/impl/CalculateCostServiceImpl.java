package com.skyeye.calculatecost.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.skyeye.calculatecost.entity.MachinCost;
import com.skyeye.calculatecost.entity.MachinProcedureAcceptCost;
import com.skyeye.calculatecost.entity.MachinProcedureCost;
import com.skyeye.calculatecost.entity.MachinPutCost;
import com.skyeye.calculatecost.service.CalculateCostService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.farm.entity.FarmStaff;
import com.skyeye.farm.service.FarmStaffService;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.entity.MachinChild;
import com.skyeye.machin.entity.MachinPut;
import com.skyeye.machin.service.MachinChildService;
import com.skyeye.machin.service.MachinPutService;
import com.skyeye.machin.service.MachinService;
import com.skyeye.machinprocedure.classenum.MachinProcedureAcceptChildType;
import com.skyeye.machinprocedure.entity.*;
import com.skyeye.machinprocedure.service.*;
import com.skyeye.material.entity.Material;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.procedure.entity.WorkProcedure;
import com.skyeye.procedure.service.WorkProcedureService;
import com.skyeye.rest.checkwork.checkwork.ICheckWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CalculateCostServiceImpl implements CalculateCostService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private MachinProcedureAcceptService machinProcedureAcceptService;

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Autowired
    private MachinProcedureAcceptProductNumService machinProcedureAcceptProductNumService;

    @Autowired
    protected IAuthUserService iAuthUserService;

    @Autowired
    private MachinProcedureAcceptChildService machinProcedureAcceptChildService;

    @Autowired
    private ICheckWorkService iCheckWorkService;

    @Autowired
    private FarmStaffService farmStaffService;

    @Autowired
    private MachinPutService machinPutService;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private MachinService machinService;

    @Autowired
    private MachinChildService machinChildService;


    /**
     * 计算一个工序每个员工的工单数量
     *
     * @param productNumList 员工生产数量信息
     * @return <‘staffId’, 生产数量对象>
     */
    private Map<String, MachinProcedureAcceptProductNum> calculateAcceptProductNum(List<MachinProcedureAcceptProductNum> productNumList) {
        Map<String, MachinProcedureAcceptProductNum> productNumMap = new HashMap<>();
        for (MachinProcedureAcceptProductNum productNum : productNumList) {
            String staffId = productNum.getStaffId();
            if (productNumMap.containsKey(staffId)) {
                // 求和
                productNumMap.get(staffId).setAllNumber(productNumMap.get(staffId).getAllNumber() + productNum.getAllNumber());
                productNumMap.get(staffId).setQualifiedNum(productNumMap.get(staffId).getQualifiedNum() + productNum.getQualifiedNum());
                productNumMap.get(staffId).setReworkNum(productNumMap.get(staffId).getReworkNum() + productNum.getReworkNum());
                productNumMap.get(staffId).setScrapNum(productNumMap.get(staffId).getScrapNum() + productNum.getScrapNum());
                productNumMap.get(staffId).setStaffMation(productNum.getStaffMation());
            } else {
                productNumMap.put(staffId, productNum);
            }
        }
        return productNumMap;
    }

    @Autowired
    private WorkProcedureService workProcedureService;

    @Override
    public void calculateMachinProcedureAcceptCost(InputObject inputObject, OutputObject outputObject) {
        String machinProcedureAcceptId = inputObject.getParams().get("machinProcedureAcceptId").toString();
        // 工序验收单信息 -- 耗材信息已存在
        MachinProcedureAccept machinProcedureAccept = machinProcedureAcceptService.selectById(machinProcedureAcceptId);
        if (StrUtil.isEmpty(machinProcedureAccept.getId())) {
            throw new RuntimeException("工序验收单信息不存在");
        }
        // 加工单子单据工序信息----没有详细的工序信息
        MachinProcedure machinProcedure = machinProcedureService.selectById(machinProcedureAccept.getMachinProcedureId());
        // 详细工序信息
        WorkProcedure workProcedure = workProcedureService.selectById(machinProcedure.getProcedureId());
        machinProcedure.setProcedureMation(workProcedure);
        // 获取商品信息和规格信息
        materialService.setDataMation(machinProcedure, MachinProcedure::getMaterialId);
        materialNormsService.setDataMation(machinProcedure, MachinProcedure::getNormsId);
        // 查询耗材信息
        List<MachinProcedureAcceptChild> acceptChildList = machinProcedureAcceptChildService.selectByParentId(machinProcedureAcceptId);
        // 工序验收单信息中已经查询过员工生产数量列表
        List<MachinProcedureAcceptProductNum> productNumList = machinProcedureAccept.getMachinProcedureAcceptProductNumList();
        Map<String, MachinProcedureAcceptProductNum> staffNumMap = calculateAcceptProductNum(productNumList);
        // 获取员工信息
        List<String> staffIdList = productNumList.stream()
                .map(MachinProcedureAcceptProductNum::getStaffId).collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = productNumList.stream().collect(Collectors.toMap(MachinProcedureAcceptProductNum::getStaffId, MachinProcedureAcceptProductNum::getStaffMation));
        // 获取日期间的所有日期值
        List<String> betweenDates = getBetweenDates(machinProcedure.getPlanStartTime(), machinProcedure.getPlanEndTime());
        // 查询考勤信息
        List<Map<String, Object>> checkWorkInfo = iCheckWorkService.queryInfoByStaffIdsAndDates(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(staffIdList), Joiner.on(CommonCharConstants.COMMA_MARK).join(betweenDates));
        Map<String, List<String>> workHourListMap = checkWorkInfo.stream()
                .collect(Collectors.groupingBy(m -> m.get("createId").toString(), Collectors.mapping(m -> m.get("workHours").toString(), Collectors.toList())));
        // 计算所有员工的工时信息
        Map<String, String> workHoursMap = calculateHours(workHourListMap);
        // 计件工价格信息
        List<FarmStaff> farmStaffList = farmStaffService.queryListByFarmIdsAndStaffIds(Collections.singletonList(machinProcedureAccept.getFarmId()), staffIdList);
        Map<String, String> farmStaffMap = farmStaffList.stream().collect(
                Collectors.toMap(FarmStaff::getStaffId, f -> StrUtil.isEmpty(f.getPieceWorkPrice()) ? "0" : f.getPieceWorkPrice()));
        // 计算所有员工的成本总和，并设置数量信息
        MachinProcedureAcceptCost bean = calculateMachinProcedureAcceptCost(
                machinProcedure, staffNumMap, staffMap, workHoursMap, acceptChildList, farmStaffMap);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void calculateMachinProcedureCost(InputObject inputObject, OutputObject outputObject) {
        String machinProcedureId = inputObject.getParams().get("machinProcedureId").toString();
        // 查询加工单子单据工序信息----没有详细的工序信息
        MachinProcedure machinProcedure = machinProcedureService.selectById(machinProcedureId);
        WorkProcedure workProcedure = workProcedureService.selectById(machinProcedure.getProcedureId());
        machinProcedure.setProcedureMation(workProcedure);
        materialService.setDataMation(machinProcedure, MachinProcedure::getMaterialId);
        // 查询所有工序验收单信息
        List<MachinProcedureAccept> acceptList = machinProcedureAcceptService.queryListByMachinProcedureId(machinProcedureId);
        if (CollectionUtil.isEmpty(acceptList)) {
            MachinProcedureCost bean = setMachinProcedureDate(new ArrayList<>(), machinProcedure);
            outputObject.setBean(bean);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        List<String> farmIdList = acceptList.stream().map(MachinProcedureAccept::getFarmId).distinct().collect(Collectors.toList());
        List<String> acceptIdList = acceptList.stream().map(MachinProcedureAccept::getId).collect(Collectors.toList());
        // 耗耗材信息
        List<MachinProcedureAcceptChild> machinProcedureAcceptChildList = machinProcedureAcceptChildService.queryListByParentId(acceptIdList);
        Map<String, List<MachinProcedureAcceptChild>> acceptChildMap = machinProcedureAcceptChildList.stream().collect(Collectors.groupingBy(MachinProcedureAcceptChild::getParentId));
        // 每个工序验收单信息中已经查询过员工生产数量列表
        List<MachinProcedureAcceptProductNum> productNumList = machinProcedureAcceptProductNumService.queryListByParentIds(acceptIdList);
        Map<String, List<MachinProcedureAcceptProductNum>> productNumMap = productNumList.stream().collect(Collectors.groupingBy(MachinProcedureAcceptProductNum::getParentId));
        // 获取员工信息
        List<String> staffIdList = productNumList.stream().map(MachinProcedureAcceptProductNum::getStaffId).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIdList);
        // 获取日期间的所有日期值
        List<String> betweenDates = getBetweenDates(machinProcedure.getPlanStartTime(), machinProcedure.getPlanEndTime());
        // 查询考勤信息
        List<Map<String, Object>> checkWorkInfo = iCheckWorkService.queryInfoByStaffIdsAndDates(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(staffIdList), Joiner.on(CommonCharConstants.COMMA_MARK).join(betweenDates));
        Map<String, List<String>> workHourListMap = checkWorkInfo.stream()
                .collect(Collectors.groupingBy(m -> m.get("createId").toString(), Collectors.mapping(m -> m.get("workHours").toString(), Collectors.toList())));
        // 计算所有员工的工时信息
        Map<String, String> workHoursMap = calculateHours(workHourListMap);
        // 计件工价格信息
        List<FarmStaff> farmStaffList = farmStaffService.queryListByFarmIdsAndStaffIds(farmIdList, staffIdList);
        Map<String, Map<String, String>> farmStaffMap = farmStaffList.stream().collect(Collectors.groupingBy(FarmStaff::getFarmId,
                Collectors.toMap(FarmStaff::getStaffId, f -> StrUtil.isEmpty(f.getPieceWorkPrice()) ? "0" : f.getPieceWorkPrice())));
        // 计算所有员工的成本总和，并设置数量信息
        List<MachinProcedureAcceptCost> acceptCostList = calculateMachinProcedureCost(
                machinProcedure, acceptList, productNumMap, staffMap,
                workHoursMap, acceptChildMap, farmStaffMap);
        MachinProcedureCost bean = setMachinProcedureDate(acceptCostList, machinProcedure);
        outputObject.setBean(bean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void calculateMachinPutCost(InputObject inputObject, OutputObject outputObject) {
        String machinPutId = inputObject.getParams().get("machinPutId").toString();
        // 加工入库单信息
        MachinPut machinPut = machinPutService.selectById(machinPutId);
        // 车间任务信息
        MachinProcedureFarm machinProcedureFarm = machinProcedureFarmService.selectById(machinPut.getFromId());
        if (StrUtil.isEmpty(machinProcedureFarm.getId())) {
            outputObject.setBean(setMachinPutDate(machinPut, new ArrayList<>(), new MachinChild()));
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        // 加工单子单据的所有工序信息-----没有详细的工序信息
        List<MachinProcedure> machinProcedureList = machinProcedureService.querySameListById(machinProcedureFarm.getMachinProcedureId());
        List<String> MPIdList = machinProcedureList.stream().map(MachinProcedure::getId).collect(Collectors.toList());
        materialService.setDataMation(machinProcedureList, MachinProcedure::getMaterialId);

        // 加工单子单据信息
        MachinChild machinChild = machinChildService.getById(machinProcedureList.get(CommonNumConstants.NUM_ZERO).getChildId());

        // 工序验收单信息
        List<MachinProcedureAccept> acceptList = machinProcedureAcceptService.queryListByMachinProcedureIdList(MPIdList);
        List<String> farmIdList = acceptList.stream().map(MachinProcedureAccept::getFarmId).distinct().collect(Collectors.toList());
        List<String> acceptIdList = acceptList.stream().map(MachinProcedureAccept::getId).collect(Collectors.toList());
        Map<String, List<MachinProcedureAccept>> acceptMap = acceptList.stream().collect(Collectors.groupingBy(MachinProcedureAccept::getMachinProcedureId));

        // 耗材信息
        List<MachinProcedureAcceptChild> acceptChildList = machinProcedureAcceptChildService.queryListByParentId(acceptIdList);
        Map<String, List<MachinProcedureAcceptChild>> acceptChildMap = acceptChildList.stream().collect(Collectors.groupingBy(MachinProcedureAcceptChild::getParentId));

        // 每个工序验收单信息中已经查询过员工生产数量列表
        List<MachinProcedureAcceptProductNum> productNumList = machinProcedureAcceptProductNumService.queryListByParentIds(acceptIdList);
        Map<String, List<MachinProcedureAcceptProductNum>> productNumMap = productNumList.stream()
                .collect(Collectors.groupingBy(MachinProcedureAcceptProductNum::getParentId));

        // 获取员工信息
        List<String> staffIdList = productNumList.stream().map(MachinProcedureAcceptProductNum::getStaffId).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIdList);

        // 所有工序包含的日期YYYY-MM-dd
        List<String> betweenDates = getBetweenDates(machinProcedureList);

        // 查询考勤信息
        List<Map<String, Object>> checkWorkInfo = iCheckWorkService.queryInfoByStaffIdsAndDates(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(staffIdList), Joiner.on(CommonCharConstants.COMMA_MARK).join(betweenDates));
        // <'YYYY-MM-dd',<'staffId','HH:MM:SS'>>
        Map<String, Map<String, String>> workHourListMap = checkWorkInfo.stream()
                .collect(Collectors.groupingBy(m -> m.get("createDate").toString(),
                        Collectors.toMap(m -> m.get("staffId").toString(), m -> m.get("workHours").toString())));
        // 计件工价格信息
        List<FarmStaff> farmStaffList = farmStaffService.queryListByFarmIdsAndStaffIds(farmIdList, staffIdList);
        Map<String, Map<String, String>> farmStaffMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(farmStaffList)) {
            farmStaffMap = farmStaffList.stream().collect(Collectors.groupingBy(FarmStaff::getFarmId,
                    Collectors.toMap(FarmStaff::getStaffId, f -> StrUtil.isEmpty(f.getPieceWorkPrice()) ? "0" : f.getPieceWorkPrice())));
        }
        // 计算所有员工的成本总和
        List<MachinProcedureCost> machinProcedureCostList = calculateMachinPutCost(
                machinProcedureList, acceptMap, productNumMap,
                staffMap, workHourListMap, acceptChildMap, farmStaffMap);
        // 设置返回会数据
        MachinPutCost machinPutCost = setMachinPutDate(machinPut, machinProcedureCostList, machinChild);
        outputObject.setBean(machinPutCost);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void calculateMachinCost(InputObject inputObject, OutputObject outputObject) {
        String machinId = inputObject.getParams().get("machinId").toString();
        Machin machin = machinService.selectById(machinId);
        // 部门加工单下所有的工序信息
        List<MachinProcedure> machinProcedureList = machinProcedureService.queryListByMachinId(machin.getId());
        List<String> MPIdList = machinProcedureList.stream().map(MachinProcedure::getId).collect(Collectors.toList());
        Map<String, List<MachinProcedure>> childIdMPMap = machinProcedureList.stream().collect(Collectors.groupingBy(MachinProcedure::getChildId));
        materialService.setDataMation(machinProcedureList, MachinProcedure::getMaterialId);
        // 验收信息
        List<MachinProcedureAccept> acceptList = machinProcedureAcceptService.queryListByMachinProcedureIdList(MPIdList);
        if (CollectionUtil.isEmpty(acceptList)) {
            MachinCost machinCost = setMachinCost(machin, new ArrayList<>());
            outputObject.setBean(machinCost);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
            return;
        }
        List<String> farmIdList = acceptList.stream().map(MachinProcedureAccept::getFarmId).distinct().collect(Collectors.toList());
        List<String> acceptIdList = acceptList.stream().map(MachinProcedureAccept::getId).collect(Collectors.toList());
        Map<String, List<MachinProcedureAccept>> MPIdAcceptMap = acceptList.stream().collect(Collectors.groupingBy(MachinProcedureAccept::getMachinProcedureId));

        // 耗材信息
        List<MachinProcedureAcceptChild> acceptChildList = machinProcedureAcceptChildService.queryListByParentId(acceptIdList);
        Map<String, List<MachinProcedureAcceptChild>> acceptChildMap = acceptChildList.stream().collect(Collectors.groupingBy(MachinProcedureAcceptChild::getParentId));

        // 生产数量信息
        List<MachinProcedureAcceptProductNum> productNumList = machinProcedureAcceptProductNumService.queryListByParentIds(acceptIdList);
        List<String> staffIdList = productNumList.stream().map(MachinProcedureAcceptProductNum::getStaffId).collect(Collectors.toList());
        Map<String, List<MachinProcedureAcceptProductNum>> acceptNumMap = productNumList.stream()
                .collect(Collectors.groupingBy(MachinProcedureAcceptProductNum::getParentId));

        // 员工信息
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIdList);

        // 所有工序包含的日期YYYY-MM-dd
        List<String> betweenDates = getBetweenDates(machinProcedureList);
        // 考勤信息
        List<Map<String, Object>> checkWorkMap = iCheckWorkService.queryInfoByStaffIdsAndDates(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(staffIdList), Joiner.on(CommonCharConstants.COMMA_MARK).join(betweenDates));
        // <'YYYY-MM-dd',<'staffId','workHours'>>
        Map<String, Map<String, String>> dateWorkHoursMap = checkWorkMap.stream().collect(Collectors.groupingBy(m -> m.get("checkDate").toString()
                , Collectors.toMap(m -> m.get("createId").toString(), m -> m.get("workHours").toString())));

        // 计件工价格信息
        List<FarmStaff> farmStaffList = farmStaffService.queryListByFarmIdsAndStaffIds(farmIdList, staffIdList);
        Map<String, Map<String, String>> farmStaffMap = farmStaffList.stream().collect(Collectors.groupingBy(FarmStaff::getFarmId,
                Collectors.toMap(FarmStaff::getStaffId, f -> StrUtil.isEmpty(f.getPieceWorkPrice()) ? "0" : f.getPieceWorkPrice())));
        List<MachinPutCost> machinPutCosts = calculateMachinCost(machin.getMachinChildList(), childIdMPMap, MPIdAcceptMap, acceptNumMap, acceptChildMap, staffMap, dateWorkHoursMap, farmStaffMap);
        MachinCost machinCost = setMachinCost(machin, machinPutCosts);
        outputObject.setBean(machinCost);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private List<MachinPutCost> calculateMachinCost(List<MachinChild> machinChildList, Map<String, List<MachinProcedure>> childIdMPMap
            , Map<String, List<MachinProcedureAccept>> acceptMap, Map<String, List<MachinProcedureAcceptProductNum>> acceptNumMap
            , Map<String, List<MachinProcedureAcceptChild>> acceptChildMap, Map<String, Map<String, Object>> staffMap
            , Map<String, Map<String, String>> dateWorkHoursMap, Map<String, Map<String, String>> farmStaffMap) {
        List<MachinPutCost> machinPutCostList = new ArrayList<>();
        for (MachinChild machinChild : machinChildList) {
            String childId = machinChild.getId();

            List<MachinProcedureCost> machinProcedureCostList = calculateMachinPutCost(
                    childIdMPMap.getOrDefault(childId, new ArrayList<>()), acceptMap, acceptNumMap, staffMap, dateWorkHoursMap, acceptChildMap, farmStaffMap);
            MachinPutCost machinPutCost = setMachinPutDate(machinChild, machinProcedureCostList);
            machinPutCostList.add(machinPutCost);
        }
        return machinPutCostList;
    }

    private MachinCost setMachinCost(Machin machin, List<MachinPutCost> machinPutCosts) {
        MachinCost machinCost = new MachinCost();
        machinCost.setNormalConsumablePrice("0");
        machinCost.setScrapConsumablePrice("0");
        machinCost.setConsumablePrice("0");
        machinCost.setWage("0");
        machinCost.setTotalPrice("0");
        machinCost.setPutCostList(machinPutCosts);
        for (MachinPutCost machinPutCost : machinPutCosts) {
            machinCost.setNormalConsumablePrice(CalculationUtil.add(machinCost.getNormalConsumablePrice(), machinPutCost.getNormalConsumablePrice(), CommonNumConstants.NUM_SIX));
            machinCost.setScrapConsumablePrice(CalculationUtil.add(machinCost.getScrapConsumablePrice(), machinPutCost.getScrapConsumablePrice(), CommonNumConstants.NUM_SIX));
            machinCost.setConsumablePrice(CalculationUtil.add(machinCost.getConsumablePrice(), machinPutCost.getConsumablePrice(), CommonNumConstants.NUM_SIX));
            machinCost.setWage(CalculationUtil.add(machinCost.getWage(), machinPutCost.getWage(), CommonNumConstants.NUM_SIX));
            machinCost.setTotalPrice(CalculationUtil.add(machinCost.getTotalPrice(), machinPutCost.getTotalPrice(), CommonNumConstants.NUM_SIX));
        }
        return machinCost;
    }

    private List<MachinProcedureCost> calculateMachinPutCost(List<MachinProcedure> machinProcedureList
            , Map<String, List<MachinProcedureAccept>> acceptMap, Map<String, List<MachinProcedureAcceptProductNum>> productNumMap
            , Map<String, Map<String, Object>> staffMap, Map<String, Map<String, String>> workHourListMap
            , Map<String, List<MachinProcedureAcceptChild>> acceptChildMap, Map<String, Map<String, String>> farmStaffMap) {
        List<MachinProcedureCost> machinProcedureCostList = new ArrayList<>();
        for (MachinProcedure machinProcedure : machinProcedureList) {
            String machinProcedureId = machinProcedure.getId();
            // 计算该工序的所有员工工时信息
            Map<String, String> workHourMap = calculateHours(machinProcedure, workHourListMap);
            List<MachinProcedureAcceptCost> acceptCostList = calculateMachinProcedureCost(machinProcedure, acceptMap.getOrDefault(machinProcedureId, new ArrayList<>()),
                    productNumMap, staffMap, workHourMap, acceptChildMap, farmStaffMap);
            // 设置工序成本信息
            MachinProcedureCost bean = setMachinProcedureDate(acceptCostList, machinProcedure);
            machinProcedureCostList.add(bean);
        }
        return machinProcedureCostList;
    }

    private MachinPutCost setMachinPutDate(MachinPut machinPut, List<MachinProcedureCost> MPCostList, MachinChild machinChild) {
        Integer currentOperNumber = machinPut.getErpOrderItemList().get(CommonNumConstants.NUM_ZERO).getOperNumber();
        Material materialMation = machinPut.getErpOrderItemList().get(CommonNumConstants.NUM_ZERO).getMaterialMation();
        String materialName = StrUtil.isEmpty(materialMation.getId()) ? null : materialMation.getName();
        MachinPutCost machinPutCost = new MachinPutCost();
        machinPutCost.setMaterialName(materialName);
        machinPutCost.setMachinProcedureCostList(MPCostList);
        machinPutCost.setConsumablePrice("0");
        machinPutCost.setScrapConsumablePrice("0");
        machinPutCost.setNormalConsumablePrice("0");
        machinPutCost.setAllNum(currentOperNumber);
        machinPutCost.setPrice("0");
        machinPutCost.setWage("0");
        machinPutCost.setTotalPrice("0");
        if (currentOperNumber <= CommonNumConstants.NUM_ZERO) {
            return machinPutCost;
        }
        for (MachinProcedureCost machinProcedureCost : MPCostList) {
            // 耗材总成本
            machinPutCost.setConsumablePrice(CalculationUtil.add(machinPutCost.getConsumablePrice(), machinProcedureCost.getConsumablePrice(), CommonNumConstants.NUM_SIX));
            // 正常耗材成本
            machinPutCost.setNormalConsumablePrice(CalculationUtil.add(machinPutCost.getNormalConsumablePrice(), machinProcedureCost.getNormalConsumablePrice(), CommonNumConstants.NUM_SIX));
            // 报废耗材成本
            machinPutCost.setScrapConsumablePrice(CalculationUtil.add(machinPutCost.getScrapConsumablePrice(), machinProcedureCost.getScrapConsumablePrice(), CommonNumConstants.NUM_SIX));
            // 加工单价
            machinPutCost.setPrice(CalculationUtil.add(machinPutCost.getPrice(), machinProcedureCost.getPrice(), CommonNumConstants.NUM_SIX));
            // 工资金额
            machinPutCost.setWage(CalculationUtil.add(machinPutCost.getWage(), machinProcedureCost.getWage(), CommonNumConstants.NUM_SIX));
            // 总价
            machinPutCost.setTotalPrice(CalculationUtil.add(machinPutCost.getTotalPrice(), machinProcedureCost.getTotalPrice(), CommonNumConstants.NUM_SIX));
        }
        // 计算此次加工入库单占加工单子单据生产数量的比例
        String proportion = CalculationUtil.divide(String.valueOf(currentOperNumber), String.valueOf(machinChild.getOperNumber()), CommonNumConstants.NUM_SIX);
        // 计算占的耗材成本、报废耗材成本、正常耗材成本、加工单价、工资金额、总价
        machinPutCost.setConsumablePrice(CalculationUtil.multiply(machinPutCost.getConsumablePrice(), proportion, CommonNumConstants.NUM_SIX));
        machinPutCost.setScrapConsumablePrice(CalculationUtil.multiply(machinPutCost.getScrapConsumablePrice(), proportion, CommonNumConstants.NUM_SIX));
        machinPutCost.setNormalConsumablePrice(CalculationUtil.multiply(machinPutCost.getNormalConsumablePrice(), proportion, CommonNumConstants.NUM_SIX));
        machinPutCost.setPrice(CalculationUtil.multiply(machinPutCost.getPrice(), proportion, CommonNumConstants.NUM_SIX));
        machinPutCost.setWage(CalculationUtil.multiply(machinPutCost.getWage(), proportion, CommonNumConstants.NUM_SIX));
        machinPutCost.setTotalPrice(CalculationUtil.multiply(machinPutCost.getTotalPrice(), proportion, CommonNumConstants.NUM_SIX));
        return machinPutCost;
    }

    private MachinPutCost setMachinPutDate(MachinChild machinChild, List<MachinProcedureCost> MPCostList) {
        Material materialMation = machinChild.getMaterialMation();
        String materialName = StrUtil.isEmpty(materialMation.getId()) ? null : materialMation.getName();
        MachinPutCost machinPutCost = new MachinPutCost();
        machinPutCost.setMaterialName(materialName);
        machinPutCost.setMachinProcedureCostList(MPCostList);
        machinPutCost.setConsumablePrice("0");
        machinPutCost.setScrapConsumablePrice("0");
        machinPutCost.setNormalConsumablePrice("0");
        machinPutCost.setAllNum(machinChild.getOperNumber());
        machinPutCost.setPrice("0");
        machinPutCost.setWage("0");
        machinPutCost.setTotalPrice("0");
        machinPutCost.setNowNum(CommonNumConstants.NUM_ZERO);
        for (MachinProcedureCost machinProcedureCost : MPCostList) {
            // 耗材成本
            machinPutCost.setConsumablePrice(CalculationUtil.add(machinPutCost.getConsumablePrice(), machinProcedureCost.getConsumablePrice(), CommonNumConstants.NUM_SIX));
            // 报废耗材成本
            machinPutCost.setScrapConsumablePrice(CalculationUtil.add(machinPutCost.getScrapConsumablePrice(), machinProcedureCost.getScrapConsumablePrice(), CommonNumConstants.NUM_SIX));
            // 正常耗材成本
            machinPutCost.setNormalConsumablePrice(CalculationUtil.add(machinPutCost.getNormalConsumablePrice(), machinProcedureCost.getNormalConsumablePrice(), CommonNumConstants.NUM_SIX));
            // 加工单价
            machinPutCost.setPrice(CalculationUtil.add(machinPutCost.getPrice(), machinProcedureCost.getPrice(), CommonNumConstants.NUM_SIX));
            // 工资金额
            machinPutCost.setWage(CalculationUtil.add(machinPutCost.getWage(), machinProcedureCost.getWage(), CommonNumConstants.NUM_SIX));
            // 总价
            machinPutCost.setTotalPrice(CalculationUtil.add(machinPutCost.getTotalPrice(), machinPutCost.getPrice(), CommonNumConstants.NUM_SIX));
            // 当前已经生产的数量
            if (machinChild.getMaterialId().equals(machinProcedureCost.getMaterialId()) && machinChild.getNormsId().equals(machinProcedureCost.getNormsId())) {
                machinPutCost.setNowNum(machinPutCost.getNowNum() + machinProcedureCost.getAllNum());
            }
        }
        return machinPutCost;
    }

    /**
     * 计算一个工序的所有员工成本
     *
     * @param machinProcedure 工序信息
     * @param acceptList      工序内所有的验收单信息
     * @param productNumMap   工序内所有的员工生产数量信息
     * @param staffMap        员工信息
     * @param workHoursMap    工时信息
     * @param acceptChildMap  工序内所有的耗耗材信息
     * @param farmStaffMap    计件工单价信息
     * @return 工序内所有员工成本信息
     */
    private List<MachinProcedureAcceptCost> calculateMachinProcedureCost(
            MachinProcedure machinProcedure, List<MachinProcedureAccept> acceptList,
            Map<String, List<MachinProcedureAcceptProductNum>> productNumMap,
            Map<String, Map<String, Object>> staffMap, Map<String, String> workHoursMap,
            Map<String, List<MachinProcedureAcceptChild>> acceptChildMap, Map<String, Map<String, String>> farmStaffMap) {
        List<MachinProcedureAcceptCost> acceptCostList = new ArrayList<>();
        for (MachinProcedureAccept machinProcedureAccept : acceptList) {
            String procedureAcceptId = machinProcedureAccept.getId();
            // 获取该工序下所有员工生产数量信息
            Map<String, MachinProcedureAcceptProductNum> productNumMapChild = calculateAcceptProductNum(productNumMap.getOrDefault(procedureAcceptId, new ArrayList<>()));
            // 计算该工序内所有员工成本信息
            MachinProcedureAcceptCost acceptCost = calculateMachinProcedureAcceptCost(
                    machinProcedure, productNumMapChild, staffMap, workHoursMap,
                    acceptChildMap.getOrDefault(procedureAcceptId, new ArrayList<>()),
                    farmStaffMap.getOrDefault(machinProcedureAccept.getFarmId(), new HashMap<>()));
            acceptCostList.add(acceptCost);
        }
        return acceptCostList;
    }

    private Map<String, String> calculateHours(MachinProcedure machinProcedure, Map<String, Map<String, String>> workHourListMap) {
        // 获取工序包含的所有日期
        List<String> betweenDates = getBetweenDates(machinProcedure.getPlanStartTime(), machinProcedure.getPlanEndTime());
        // 过滤出工序内的日期，然后计算出每一个员工的总工时
        List<Map<String, String>> workHourList = workHourListMap.entrySet().stream()
                .filter(entry -> betweenDates.contains(entry.getKey())).map(entry -> entry.getValue()).collect(Collectors.toList());
        Map<String, List<String>> workHourMap = new HashMap<>();

        for (Map<String, String> map : workHourList) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!workHourMap.containsKey(entry.getKey())) {
                    workHourMap.put(entry.getKey(), new ArrayList<>());
                }
                workHourMap.get(entry.getKey()).add(entry.getValue());
            }
        }
        return calculateHours(workHourMap);
    }

    /**
     * 计算工序下所有验收单的成本信息
     *
     * @param acceptCostList 验收单成本信息
     * @return 工序成本信息
     */
    private MachinProcedureCost setMachinProcedureDate(List<MachinProcedureAcceptCost> acceptCostList, MachinProcedure machinProcedure) {
        Material materialMation = machinProcedure.getMaterialMation();
        MachinProcedureCost bean = new MachinProcedureCost();
        bean.setMaterialId(StrUtil.isEmpty(machinProcedure.getMaterialId()) ? "":machinProcedure.getMaterialId());
        bean.setNormsId(StrUtil.isEmpty(machinProcedure.getNormsId()) ? "" :machinProcedure.getNormsId());
        bean.setMaterialName(StrUtil.isEmpty(materialMation.getId()) ? "" : materialMation.getName());
        bean.setConsumablePrice("0");
        bean.setNormalConsumablePrice("0");
        bean.setScrapConsumablePrice("0");
        bean.setAllNum(0);
        bean.setQualifiedNum(0);
        bean.setReworkNum(0);
        bean.setScrapNum(0);
        bean.setPrice("0");
        bean.setWage("0");
        bean.setTotalPrice("0");
        bean.setAcceptCostList(acceptCostList);
        for (MachinProcedureAcceptCost acceptCost : acceptCostList) {
            bean.setConsumablePrice(CalculationUtil.add(bean.getConsumablePrice(), acceptCost.getConsumablePrice(), CommonNumConstants.NUM_SIX));
            bean.setNormalConsumablePrice(CalculationUtil.add(bean.getNormalConsumablePrice(), acceptCost.getNormalConsumablePrice(), CommonNumConstants.NUM_SIX));
            bean.setScrapConsumablePrice(CalculationUtil.add(bean.getScrapConsumablePrice(), acceptCost.getScrapConsumablePrice(), CommonNumConstants.NUM_SIX));
            bean.setAllNum(bean.getAllNum() + acceptCost.getAllNum());
            bean.setQualifiedNum(bean.getQualifiedNum() + acceptCost.getQualifiedNum());
            bean.setReworkNum(bean.getReworkNum() + acceptCost.getReworkNum());
            bean.setScrapNum(bean.getScrapNum() + acceptCost.getScrapNum());
            bean.setPrice(CalculationUtil.add(bean.getPrice(), acceptCost.getPrice(), CommonNumConstants.NUM_SIX));
            bean.setWage(CalculationUtil.add(bean.getWage(), acceptCost.getWage(), CommonNumConstants.NUM_SIX));
            bean.setTotalPrice(CalculationUtil.add(bean.getTotalPrice(), acceptCost.getTotalPrice(), CommonNumConstants.NUM_SIX));
        }
        return bean;
    }

    /**
     * 计算所有员工的工时
     *
     * @param workHoursMap <'staffId',['10:22:11','05:32:19']>
     * @return <'staffId',工时>
     */
    private Map<String, String> calculateHours(Map<String, List<String>> workHoursMap) {
        Map<String, String> hoursMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : workHoursMap.entrySet()) {
            String staffId = entry.getKey();
            List<String> workHoursList = entry.getValue();
            // 过滤掉空值    转换为毫秒     以0L为初始值求和
            long milliseconds = workHoursList.stream().filter(StrUtil::isNotEmpty).map(this::calculateHours).reduce(0L, Long::sum);
            double hours = (double) TimeUnit.MILLISECONDS.toHours(milliseconds);
            hoursMap.put(staffId, String.valueOf(hours));
        }
        return hoursMap;
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
     * 获取多个日期之间的所有日期YYYY-MM-dd
     *
     * @param machinProcedureList 加工单子单据工序信息列表
     * @return [YYYY-MM-dd,YYYY-MM-dd]
     */
    private List<String> getBetweenDates(List<MachinProcedure> machinProcedureList) {
        List<String> betweenDates = new ArrayList<>();
        for (MachinProcedure machinProcedure : machinProcedureList) {
            betweenDates.addAll(getBetweenDates(machinProcedure.getPlanStartTime(), machinProcedure.getPlanEndTime()));
        }
        return betweenDates.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取两个日期之间的所有日期YYYY-MM-dd
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return [YYYY-MM-dd,YYYY-MM-dd]
     */
    private List<String> getBetweenDates(String startTime, String endTime) {
        List<String> dates = new ArrayList<>();
        if (StrUtil.isEmpty(startTime) || StrUtil.isEmpty(endTime)) {
            return dates;
        }
        int distanceDay = DateUtil.getDistanceDay(startTime, endTime);
        dates.add(startTime);
        for (int i = 0; i < distanceDay; i++) {
            // 获取开始日期后一天的日期
            Date d = DateUtil.getAfDate(DateUtil.getPointTime(startTime, DateUtil.YYYY_MM_DD), 1, "d");
            dates.add(DateUtil.formatDate2Str(d, DateUtil.YYYY_MM_DD));
        }
        return dates;
    }

    private String calculateChildCost(List<MachinProcedureAcceptChild> childList) {
        String result = "0";
        if (CollectionUtil.isEmpty(childList)) {
            return result;
        }
        for (MachinProcedureAcceptChild child : childList) {
            // 计算每一个耗材的成本     数量 * 采购价
            String operNumber = child.getOperNumber() == null ? "0" : String.valueOf(child.getOperNumber());
            String estimatePurchasePrice = ObjectUtil.isEmpty(child.getNormsMation()) ? "0" : child.getNormsMation().getEstimatePurchasePrice();
            String multiply = CalculationUtil.multiply(operNumber, estimatePurchasePrice, CommonNumConstants.NUM_SIX);
            result = CalculationUtil.add(result, multiply, CommonNumConstants.NUM_SIX);
        }
        return result;
    }


    /**
     * 计算每一位员工的成本，并设置数量信息
     *
     * @param machinProcedure 工序信息
     * @param staffNumMap     员工的生产数量信息
     * @param staffMap        员工信息
     * @param workHoursMap    员工工时信息
     * @param childList       耗材列表
     * @param farmStaffMap    计件工单价信息
     * @Param childList 耗材列表
     */
    private MachinProcedureAcceptCost calculateMachinProcedureAcceptCost(
            MachinProcedure machinProcedure, Map<String, MachinProcedureAcceptProductNum> staffNumMap, Map<String, Map<String, Object>> staffMap
            , Map<String, String> workHoursMap, List<MachinProcedureAcceptChild> childList, Map<String, String> farmStaffMap) {
        MachinProcedureAcceptCost machinProcedureCost = new MachinProcedureAcceptCost();
        // 设置总数量、 合格数量、返工数量、报废数量 均为0、工序信息、初始化员工生产数量信息
        machinProcedureCost.setAllNum(CommonNumConstants.NUM_ZERO);
        machinProcedureCost.setQualifiedNum(CommonNumConstants.NUM_ZERO);
        machinProcedureCost.setReworkNum(CommonNumConstants.NUM_ZERO);
        machinProcedureCost.setScrapNum(CommonNumConstants.NUM_ZERO);
        machinProcedureCost.setProcedureName(machinProcedure.getProcedureMation().getName());
        machinProcedureCost.setProcedureNumber(machinProcedure.getProcedureMation().getNumber());
        machinProcedureCost.setProductNumMationList(new ArrayList<>());
        // 获取所有耗材的成本
        String consumablePrice = calculateChildCost(childList);
        machinProcedureCost.setConsumablePrice(consumablePrice);
        Map<Integer, List<MachinProcedureAcceptChild>> childMap = childList.stream().collect(Collectors.groupingBy(MachinProcedureAcceptChild::getType));
        machinProcedureCost.setNormalChildLIst(childMap.getOrDefault(MachinProcedureAcceptChildType.NORMAL.getKey(), new ArrayList<>()));
        machinProcedureCost.setScrapChildLIst(childMap.getOrDefault(MachinProcedureAcceptChildType.SCRAP.getKey(), new ArrayList<>()));
        // 获取正常耗材的成本 和 报废耗材的成本
        String normalChildCost = CollectionUtil.isEmpty(machinProcedureCost.getNormalChildLIst()) ? "0" : calculateChildCost(machinProcedureCost.getNormalChildLIst());
        machinProcedureCost.setNormalConsumablePrice(normalChildCost);
        String scrapChildCost = CalculationUtil.subtract(consumablePrice, normalChildCost, CommonNumConstants.NUM_SIX);
        machinProcedureCost.setScrapConsumablePrice(scrapChildCost);

        String staffCost = "0";
        for (Map.Entry<String, MachinProcedureAcceptProductNum> productNum : staffNumMap.entrySet()) {
            if (!staffMap.containsKey(productNum.getKey())) {
                continue;
            }
            MachinProcedureAcceptProductNum staffProductNum = productNum.getValue();
            Map<String, Object> staffMation = staffMap.getOrDefault(productNum.getKey(), new HashMap<>());
            // 计算数量信息
            machinProcedureCost.setAllNum(machinProcedureCost.getAllNum() + staffProductNum.getAllNumber());
            machinProcedureCost.setQualifiedNum(machinProcedureCost.getQualifiedNum() + staffProductNum.getQualifiedNum());
            machinProcedureCost.setReworkNum(machinProcedureCost.getReworkNum() + staffProductNum.getReworkNum());
            machinProcedureCost.setScrapNum(machinProcedureCost.getScrapNum() + staffProductNum.getScrapNum());
            // 获取一位员工的成本
            String oneStaffCost = calculateOneStaffCost(
                    staffProductNum, staffMation,
                    workHoursMap, farmStaffMap.getOrDefault(staffProductNum.getStaffId(), "0"),
                    machinProcedure.getPlanStartTime(), machinProcedure.getPlanEndTime());
            // 员工生产数量信息、工资信息、员工信息
            staffMation.put("wage", oneStaffCost);
            staffProductNum.setStaffMation(staffMation);
            machinProcedureCost.getProductNumMationList().add(staffProductNum);
            staffCost = CalculationUtil.add(staffCost, oneStaffCost, CommonNumConstants.NUM_SIX);
        }
        machinProcedureCost.setWage(staffCost);
        // 总价 = 耗材成本 + 工资成本
        machinProcedureCost.setTotalPrice(CalculationUtil.add(consumablePrice, staffCost, CommonNumConstants.NUM_SIX));
        // 加工单价 = 总价 / 合格数量
        machinProcedureCost.setPrice(Double.valueOf(machinProcedureCost.getTotalPrice()) == 0 ?
                "0" : CalculationUtil.divide(machinProcedureCost.getTotalPrice(), String.valueOf(machinProcedureCost.getQualifiedNum()), CommonNumConstants.NUM_SIX));
        return machinProcedureCost;
    }

    /**
     * 计算一位员工的成本
     *
     * @param staffNumMap    员工生产数量信息
     * @param staffMation    员工信息
     * @param workHoursMap   工时信息
     * @param pieceWorkPrice 计件工单价
     * @param startTime      工序实际开始时间
     * @param endTime        工序实际结束时间
     * @return 员工成本
     */
    private String calculateOneStaffCost(MachinProcedureAcceptProductNum staffNumMap, Map<String, Object> staffMation
            , Map<String, String> workHoursMap, String pieceWorkPrice, String startTime, String endTime) {
        if (staffMation.getOrDefault("workstationType", -1).toString().equals("1")) {
            // 合同工 月薪 / 30 * 工序时长
            if (staffMation.getOrDefault("designWages", CommonNumConstants.NUM_ONE).toString().equals("1")) {
                // 薪资未定
                return "0";
            }
            if (staffMation.getOrDefault("actWages", StrUtil.EMPTY).toString().isEmpty()) {
                return "0";
            }
            String actWages = CalculationUtil.divide(staffMation.get("actWages").toString(), "30", CommonNumConstants.NUM_SIX);
            int actualDuration = DateUtil.getDistanceDay(startTime, endTime);
            return CalculationUtil.multiply(actWages, String.valueOf(actualDuration), CommonNumConstants.NUM_SIX);
        } else if (staffMation.get("workstationType").toString().equals("2")) {
            // 小时工      小时工的小时单价 * 工时
            String hourlyPrice = staffMation.getOrDefault("hourlyPrice", CommonNumConstants.NUM_ZERO).toString();
            String workHour = workHoursMap.getOrDefault(staffMation.get("id").toString(), CommonNumConstants.NUM_ZERO.toString());
            return CalculationUtil.multiply(hourlyPrice, workHour, CommonNumConstants.NUM_SIX);
        } else if (staffMation.get("workstationType").toString().equals("3")) {
            // 计件工
            return CalculationUtil.multiply(pieceWorkPrice, staffNumMap.getAllNumber().toString(), CommonNumConstants.NUM_SIX);
        }
        return "0";
    }
}
