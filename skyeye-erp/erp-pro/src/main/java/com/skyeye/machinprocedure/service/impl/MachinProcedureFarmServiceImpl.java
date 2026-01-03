/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.entity.MachinChild;
import com.skyeye.machin.entity.MachinPut;
import com.skyeye.machin.service.MachinPutService;
import com.skyeye.machin.service.MachinService;
import com.skyeye.machinprocedure.classenum.MachinProcedureFarmState;
import com.skyeye.machinprocedure.classenum.MachinProcedureState;
import com.skyeye.machinprocedure.dao.MachinProcedureFarmDao;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAccept;
import com.skyeye.machinprocedure.entity.MachinProcedureFarm;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptService;
import com.skyeye.machinprocedure.service.MachinProcedureFarmService;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.pick.service.DepartmentStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinProcedureFarmServiceImpl
 * @Description: 车间任务服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 19:08
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车间任务", groupName = "车间任务")
public class MachinProcedureFarmServiceImpl extends SkyeyeBusinessServiceImpl<MachinProcedureFarmDao, MachinProcedureFarm> implements MachinProcedureFarmService {

    @Autowired
    private FarmService farmService;

    @Autowired
    private MachinService machinService;

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Autowired
    private MachinProcedureAcceptService machinProcedureAcceptService;

    @Autowired
    private MachinPutService machinPutService;

    @Autowired
    private BomService bomService;

    @Autowired
    private DepartmentStockService departmentStockService;

    @Override
    public QueryWrapper<MachinProcedureFarm> getQueryWrapper(CommonPageInfo commonPageInfo) {
        if (StrUtil.isEmpty(commonPageInfo.getType())) {
            throw new CustomException("type不能为空");
        }
        QueryWrapper<MachinProcedureFarm> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
            // 指定车间的任务
            queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getFarmId), commonPageInfo.getObjectId());
        } else if (StrUtil.equals(commonPageInfo.getType(), "department")) {
            // 我所在部门的所有车间的任务
            String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
            List<String> farmIdList = farmService.queryFarmIdListByDepartmentId(departmentId);
            queryWrapper.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getFarmId), farmIdList);
        } else if (StrUtil.equals(commonPageInfo.getType(), "all")) {
            // 所有任务
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        farmService.setMationForMap(beans, "farmId", "farmMation");
        // 加工单子单据工序信息
        machinProcedureService.setMationForMap(beans, "machinProcedureId", "machinProcedureMation");
        // 获取加工单信息
        List<String> machinIds = beans.stream().map(bean -> MapUtil.getStr(bean, "machinId"))
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Machin> machinMap = machinService.selectMapByIds(machinIds);
        // 判断是否是最后一条工序
        beans.forEach(bean -> {
            String machinId = MapUtil.getStr(bean, "machinId");
            bean.put("machinMation", machinMap.get(machinId));
            Map<String, Object> machinProcedureMation = (Map<String, Object>) bean.get("machinProcedureMation");
            if (MapUtil.isNotEmpty(machinProcedureMation)) {
                String childId = MapUtil.getStr(machinProcedureMation, "childId");
                String bomChildId = MapUtil.getStr(machinProcedureMation, "bomChildId");
                String wayProcedureId = MapUtil.getStr(machinProcedureMation, "wayProcedureId");
                String materialId = MapUtil.getStr(machinProcedureMation, "materialId");
                String normsId = MapUtil.getStr(machinProcedureMation, "normsId");
                String procedureId = MapUtil.getStr(machinProcedureMation, "procedureId");
                Boolean isLastProcedure = machinService.checkIsLastProcedure(machinMap.get(machinId), childId, bomChildId, wayProcedureId,
                    materialId, normsId, procedureId);
                bean.put("isLastProcedure", isLastProcedure);
            } else {
                bean.put("isLastProcedure", false);
            }
        });
        return beans;
    }

    @Override
    public void createPrepose(MachinProcedureFarm entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public void createPrepose(List<MachinProcedureFarm> entity) {
        String serviceClassName = getServiceClassName();
        entity.forEach(bean -> {
            Map<String, Object> business = BeanUtil.beanToMap(bean);
            String oddNumber = iCodeRuleService.getNextCodeByClassName(serviceClassName, business);
            bean.setOddNumber(oddNumber);
        });
    }

    @Override
    public MachinProcedureFarm selectById(String id) {
        MachinProcedureFarm machinProcedureFarm = super.selectById(id);
        farmService.setDataMation(machinProcedureFarm, MachinProcedureFarm::getFarmId);
        machinService.setDataMation(machinProcedureFarm, MachinProcedureFarm::getMachinId);
        machinProcedureService.setDataMation(machinProcedureFarm, MachinProcedureFarm::getMachinProcedureId);
        // 设置工序验收单信息
        Map<String, List<MachinProcedureAccept>> machinProcedureAcceptMap = machinProcedureAcceptService.queryMachinProcedureAcceptByMachinProcedureFarmId(id);
        List<MachinProcedureAccept> machinProcedureAccepts = machinProcedureAcceptMap.get(id);
        iAuthUserService.setName(machinProcedureAccepts, "createId", "createName");
        iAuthUserService.setName(machinProcedureAccepts, "lastUpdateId", "lastUpdateName");
        machinProcedureFarm.setMachinProcedureAcceptList(machinProcedureAccepts);
        // 设置加工入库单信息
        List<MachinPut> machinPutList = machinPutService.queryMachinPutByMachinProcedureFarmId(id);
        iAuthUserService.setName(machinProcedureAccepts, "createId", "createName");
        iAuthUserService.setName(machinProcedureAccepts, "lastUpdateId", "lastUpdateName");
        machinProcedureFarm.setMachinPutList(machinPutList);
        return machinProcedureFarm;
    }

    @Override
    public List<MachinProcedureFarm> selectByIds(String... ids) {
        List<MachinProcedureFarm> machinProcedureFarms = super.selectByIds(ids);
        farmService.setDataMation(machinProcedureFarms, MachinProcedureFarm::getFarmId);
        return machinProcedureFarms;
    }

    @Override
    public Map<String, List<MachinProcedureFarm>> queryMachinProcedureFarmMapByMachinId(String machinId) {
        QueryWrapper<MachinProcedureFarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinId), machinId);
        List<MachinProcedureFarm> machinProcedureFarmList = list(queryWrapper);
        if (CollectionUtil.isEmpty(machinProcedureFarmList)) {
            return MapUtil.newHashMap();
        }
        // 设置车间的工序验收单信息
        List<String> ids = machinProcedureFarmList.stream().map(MachinProcedureFarm::getId).collect(Collectors.toList());
        Map<String, List<MachinProcedureAccept>> machinProcedureAcceptMap = machinProcedureAcceptService
            .queryMachinProcedureAcceptByMachinProcedureFarmId(ids.toArray(new String[]{}));
        machinProcedureFarmList.forEach(machinProcedureFarm -> {
            machinProcedureFarm.setMachinProcedureAcceptList(machinProcedureAcceptMap.get(machinProcedureFarm.getId()));
        });

        Map<String, List<MachinProcedureFarm>> machinProcedureFarmMap = machinProcedureFarmList.stream()
            .collect(Collectors.groupingBy(MachinProcedureFarm::getMachinProcedureId));
        return machinProcedureFarmMap;
    }

    @Override
    public Map<String, Map<String, List<MachinProcedureFarm>>> queryMachinProcedureFarmMapByMachinIds(List<String> machinIds) {
        if (CollectionUtil.isEmpty(machinIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<MachinProcedureFarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinId), machinIds);
        List<MachinProcedureFarm> machinProcedureFarmList = list(queryWrapper);
        if (CollectionUtil.isEmpty(machinProcedureFarmList)) {
            return MapUtil.newHashMap();
        }
        // 设置车间的工序验收单信息
        List<String> ids = machinProcedureFarmList.stream().map(MachinProcedureFarm::getId).collect(Collectors.toList());
        Map<String, List<MachinProcedureAccept>> machinProcedureAcceptMap = machinProcedureAcceptService
            .queryMachinProcedureAcceptByMachinProcedureFarmId(ids.toArray(new String[]{}));
        machinProcedureFarmList.forEach(machinProcedureFarm -> {
            machinProcedureFarm.setMachinProcedureAcceptList(machinProcedureAcceptMap.get(machinProcedureFarm.getId()));
        });
        Map<String, Map<String, List<MachinProcedureFarm>>> machinProcedureFarmMap = machinProcedureFarmList.stream()
            .collect(Collectors.groupingBy(MachinProcedureFarm::getMachinId, Collectors.groupingBy(MachinProcedureFarm::getMachinProcedureId)));
        return machinProcedureFarmMap;
    }

    @Override
    public void receiveMachinProcedureFarm(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        MachinProcedureFarm machinProcedureFarm = selectById(id);
        if (StrUtil.equals(machinProcedureFarm.getState(), MachinProcedureFarmState.WAIT_RECEIVE.getKey())) {
            // 待接收的车间任务可以进行接收
            editStateById(id, MachinProcedureFarmState.WAIT_EXECUTED.getKey());
            // 计算所执行工序的耗材数量信息
            Map<String, BomChild> needConsumables = calculateProcedureConsumables(machinProcedureFarm);
            // 增加已分配库存
            needConsumables.forEach((normsId, newPutNum) -> {
                departmentStockService.updateDepartmentStock(machinProcedureFarm.getFarmMation().getDepartmentId(), machinProcedureFarm.getFarmId(),
                    newPutNum.getMaterialId(), newPutNum.getNormsId(), newPutNum.getNeedNum(), DepotPutOutType.PUT.getKey(), MaterialNormsStockType.ALLOCATED_STOCK.getKey());
            });
        }
    }

    /**
     * 计算所执行工序的耗材数量信息
     *
     * @param machinProcedureFarm 车间任务
     */
    private Map<String, BomChild> calculateProcedureConsumables(MachinProcedureFarm machinProcedureFarm) {
        // 获取加工单工序信息
        MachinProcedure machinProcedure = machinProcedureFarm.getMachinProcedureMation();
        if (machinProcedure == null || StrUtil.isEmpty(machinProcedure.getBomChildId())) {
            // 如果没有BOM子件清单ID，说明该工序不需要耗材
            return Collections.emptyMap();
        }

        // 获取加工单子单据信息
        String childId = machinProcedure.getChildId();
        MachinChild machinChild = machinProcedureFarm.getMachinMation().getMachinChildList().stream()
            .filter(child -> StrUtil.equals(child.getId(), childId))
            .findFirst().orElse(null);
        if (machinChild == null || StrUtil.isEmpty(machinChild.getBomId())) {
            // 如果没有BOM ID，无法计算耗材
            return Collections.emptyMap();
        }

        // 获取BOM信息
        Bom bom = bomService.selectById(machinChild.getBomId());
        if (bom == null || CollectionUtil.isEmpty(bom.getBomChildList())) {
            return Collections.emptyMap();
        }

        // 从BOM子件列表中找到对应的子件
        BomChild targetBomChild = bom.getBomChildList().stream()
            .filter(bomChild -> StrUtil.equals(bomChild.getId(), machinProcedure.getBomChildId()))
            .findFirst().orElse(null);
        if (targetBomChild == null) {
            return Collections.emptyMap();
        }

        // 获取车间任务的目标数量
        String targetNum = StrUtil.isEmpty(machinProcedureFarm.getTargetNum())
            ? CommonNumConstants.NUM_ZERO.toString()
            : machinProcedureFarm.getTargetNum();
        if (CalculationUtil.compareTo(targetNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) <= 0) {
            return Collections.emptyMap();
        }

        // 计算单元比例 = 目标数量 / BOM制造数量
        String unitRatio = CalculationUtil.divide(targetNum, String.valueOf(bom.getMakeNum()), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);

        // 构建BOM子件Map，key为normsId，用于查找子节点
        Map<String, BomChild> bomChildMap = bom.getBomChildList().stream()
            .collect(Collectors.toMap(BomChild::getNormsId, child -> child, (k1, k2) -> k1));

        // 递归计算该子件及其所有子节点的耗材数量
        List<BomChild> consumablesList = new ArrayList<>();
        calculateConsumablesRecursive(targetBomChild, bomChildMap, unitRatio, consumablesList);

        // 根据规格ID去重并合并所需数量
        Map<String, BomChild> consumablesMap = new HashMap<>();
        consumablesList.forEach(bomChild -> {
            if (consumablesMap.containsKey(bomChild.getNormsId())) {
                BomChild existBomChild = consumablesMap.get(bomChild.getNormsId());
                String sum = CalculationUtil.add(
                    StrUtil.isEmpty(existBomChild.getNeedNum()) ? CommonNumConstants.NUM_ZERO.toString() : existBomChild.getNeedNum(),
                    StrUtil.isEmpty(bomChild.getNeedNum()) ? CommonNumConstants.NUM_ZERO.toString() : bomChild.getNeedNum(),
                    ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
                existBomChild.setNeedNum(sum);
            } else {
                BomChild newBomChild = new BomChild();
                BeanUtil.copyProperties(bomChild, newBomChild);
                consumablesMap.put(bomChild.getNormsId(), newBomChild);
            }
        });

        return consumablesMap;
    }

    /**
     * 递归计算耗材数量
     *
     * @param bomChild        当前BOM子件
     * @param bomChildMap     BOM子件Map（key为normsId）
     * @param unitRatio       单元比例（目标数量 / BOM制造数量）
     * @param consumablesList 耗材列表（用于收集结果）
     */
    private void calculateConsumablesRecursive(BomChild bomChild, Map<String, BomChild> bomChildMap,
                                               String unitRatio, List<BomChild> consumablesList) {
        if (bomChild == null || StrUtil.isEmpty(bomChild.getNeedNum())) {
            return;
        }

        // 计算当前节点的实际需要数量 = 单元比例 * 当前节点的needNum
        String actualNeedNum = CalculationUtil.multiply(unitRatio, bomChild.getNeedNum(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);

        // 创建新的BOM子件对象，设置计算后的数量
        BomChild consumable = new BomChild();
        BeanUtil.copyProperties(bomChild, consumable);
        consumable.setNeedNum(actualNeedNum);
        consumablesList.add(consumable);

        // 查找当前节点的所有子节点（parentId等于当前节点的materialId）
        List<BomChild> childNodes = bomChildMap.values().stream()
            .filter(child -> StrUtil.equals(child.getParentId(), bomChild.getMaterialId()))
            .collect(Collectors.toList());

        // 递归处理子节点，使用相同的unitRatio
        if (CollectionUtil.isNotEmpty(childNodes)) {
            for (BomChild childNode : childNodes) {
                calculateConsumablesRecursive(childNode, bomChildMap, unitRatio, consumablesList);
            }
        }
    }

    @Override
    public void receptionReceiveMachinProcedureFarm(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        MachinProcedureFarm machinProcedureFarm = selectById(id);
        if (StrUtil.equals(machinProcedureFarm.getState(), MachinProcedureFarmState.WAIT_EXECUTED.getKey())) {
            // 待执行的车间任务可以进行反接收
            // 先判断是否有下达过工序验收单
            String num = machinProcedureAcceptService.calcAllNumByMachinProcedureFarmId(id);
            if (CalculationUtil.compareTo(num, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
                throw new CustomException("该车间任务已有工序验收单，不能进行反接收");
            }
            editStateById(id, MachinProcedureFarmState.WAIT_RECEIVE.getKey());
            // 计算所执行工序的耗材数量信息
            Map<String, BomChild> needConsumables = calculateProcedureConsumables(machinProcedureFarm);
            // 减少已分配库存
            needConsumables.forEach((normsId, newPutNum) -> {
                departmentStockService.updateDepartmentStock(machinProcedureFarm.getFarmMation().getDepartmentId(), machinProcedureFarm.getFarmId(),
                    newPutNum.getMaterialId(), newPutNum.getNormsId(), newPutNum.getNeedNum(), DepotPutOutType.OUT.getKey(), MaterialNormsStockType.ALLOCATED_STOCK.getKey());
            });
        }
    }

    @Override
    public void editStateById(String id, String state) {
        UpdateWrapper<MachinProcedureFarm> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(MachinProcedureFarm::getState), state);
        update(updateWrapper);
        if (StrUtil.equals(state, MachinProcedureFarmState.PARTIAL_COMPLETION.getKey())) {
            // 部分完成
            MachinProcedureFarm machinProcedureFarm = selectById(id);
            machinProcedureService.editStateById(machinProcedureFarm.getMachinProcedureId(), MachinProcedureState.PARTIAL_COMPLETION.getKey());
        } else if (StrUtil.equals(state, MachinProcedureFarmState.ALL_COMPLETED.getKey())
            || StrUtil.equals(state, MachinProcedureFarmState.EXCESS_COMPLETED.getKey())) {
            // 全部完成/超额完成
            MachinProcedureFarm machinProcedureFarm = selectById(id);
            // 查询该工序下是否存在未接收/未执行/部分完成的车间任务
            List<String> stateList = Arrays.asList(new String[]{MachinProcedureFarmState.WAIT_RECEIVE.getKey(), MachinProcedureFarmState.WAIT_EXECUTED.getKey(),
                MachinProcedureFarmState.PARTIAL_COMPLETION.getKey()});
            QueryWrapper<MachinProcedureFarm> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinProcedureId), machinProcedureFarm.getMachinProcedureId());
            queryWrapper.in(MybatisPlusUtil.toColumns(MachinProcedureFarm::getState), stateList);
            long count = count(queryWrapper);
            if (count == 0) {
                machinProcedureService.editStateById(machinProcedureFarm.getMachinProcedureId(), MachinProcedureState.ALL_COMPLETED.getKey());
            } else {
                machinProcedureService.editStateById(machinProcedureFarm.getMachinProcedureId(), MachinProcedureState.PARTIAL_COMPLETION.getKey());
            }
        }
    }

    @Override
    public void deleteMachinProcedureFarmByMachinProcedureId(String machinProcedureId) {
        // 只删除未接收的
        QueryWrapper<MachinProcedureFarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinProcedureId), machinProcedureId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getState), MachinProcedureFarmState.WAIT_RECEIVE.getKey());
        remove(queryWrapper);
    }

    @Override
    public List<MachinProcedureFarm> queryMachinProcedureFarmByMachinProcedureId(String machinProcedureId) {
        QueryWrapper<MachinProcedureFarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinProcedureId), machinProcedureId);
        queryWrapper.ne(MybatisPlusUtil.toColumns(MachinProcedureFarm::getState), MachinProcedureFarmState.WAIT_RECEIVE.getKey());
        List<MachinProcedureFarm> machinProcedureFarmList = list(queryWrapper);
        return machinProcedureFarmList;
    }

    @Override
    public List<MachinProcedureFarm> queryAllMachinProcedureFarmByMachinProcedureId(String machinProcedureId) {
        QueryWrapper<MachinProcedureFarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureFarm::getMachinProcedureId), machinProcedureId);
        List<MachinProcedureFarm> machinProcedureFarmList = list(queryWrapper);
        farmService.setDataMation(machinProcedureFarmList, MachinProcedureFarm::getFarmId);
        machinProcedureFarmList.forEach(machinProcedureFarm -> {
            machinProcedureFarm.setStateMation(MachinProcedureFarmState.getMation(machinProcedureFarm.getState()));
            machinProcedureFarm.setServiceClassName(getServiceClassName());
        });
        return machinProcedureFarmList;
    }

    @Override
    public void queryMachinProcedureFarmToInOrOutList(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        MachinProcedureFarm machinProcedureFarm = selectById(id);
        // 加工单子单据id
        String machinChildId = machinProcedureFarm.getMachinProcedureMation().getChildId();
        // 加工单子单据信息
        MachinChild machinChild = machinProcedureFarm.getMachinMation().getMachinChildList().stream()
            .filter(bean -> StrUtil.equals(bean.getId(), machinChildId))
            .findFirst().orElse(null);
        if (machinChild == null) {
            throw new CustomException("该车间任务未关联加工单子单据或该加工单子单据不存在");
        }
        // 根据车间任务id查询已经生成加工入库单的商品数量
        Map<String, String> normsNumMap = machinPutService.calcMaterialNormsNumByFromId(id);
        // 计算剩余数量：加工单子单据的数量 - 已经生成的加工入库单的数量
        String operNumber = StrUtil.isEmpty(machinChild.getOperNumber())
            ? CommonNumConstants.NUM_ZERO.toString()
            : machinChild.getOperNumber();
        String putNum = normsNumMap.getOrDefault(machinChild.getNormsId(), CommonNumConstants.NUM_ZERO.toString());
        if (StrUtil.isEmpty(putNum)) {
            putNum = CommonNumConstants.NUM_ZERO.toString();
        }
        String surplusNum = CalculationUtil.subtract(operNumber, putNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
        if (CalculationUtil.compareTo(surplusNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
            surplusNum = CommonNumConstants.NUM_ZERO.toString();
        }
        machinChild.setOperNumber(surplusNum);

        Map<String, Object> result = new HashMap<>();
        result.put("erpOrderItemList", Arrays.asList(machinChild));
        result.put("farmId", machinProcedureFarm.getFarmId());
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        List<String> ids = beans.stream().filter(bean -> !com.skyeye.common.util.MapUtil.checkKeyIsNull(bean, idKey))
            .map(bean -> bean.get(idKey).toString()).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        QueryWrapper<MachinProcedureFarm> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, ids);
        List<MachinProcedureFarm> entityList = list(queryWrapper);
        Map<String, MachinProcedureFarm> entityMap = entityList.stream().collect(Collectors.toMap(MachinProcedureFarm::getId, bean -> bean));
        for (Map<String, Object> bean : beans) {
            if (!com.skyeye.common.util.MapUtil.checkKeyIsNull(bean, idKey)) {
                MachinProcedureFarm entity = entityMap.get(bean.get(idKey).toString());
                if (ObjectUtil.isEmpty(entity)) {
                    continue;
                }
                bean.put(mationKey, entity);
            }
        }
    }

}
