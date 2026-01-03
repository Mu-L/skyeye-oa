/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machinprocedure.classenum.MachinProcedureAcceptChildType;
import com.skyeye.machinprocedure.classenum.MachinProcedureFarmState;
import com.skyeye.machinprocedure.dao.MachinProcedureAcceptDao;
import com.skyeye.machinprocedure.entity.*;
import com.skyeye.machinprocedure.service.*;
import com.skyeye.material.classenum.MaterialItemCode;
import com.skyeye.material.classenum.MaterialNormsCodeInDepot;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.entity.MaterialNormsCode;
import com.skyeye.material.service.MaterialNormsCodeService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.pick.classenum.PickNormsCodeUseState;
import com.skyeye.pick.service.DepartmentStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinProcedureAcceptServiceImpl
 * @Description: 工序验收服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 20:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工序验收", groupName = "工序验收", flowable = true)
public class MachinProcedureAcceptServiceImpl extends SkyeyeBusinessServiceImpl<MachinProcedureAcceptDao, MachinProcedureAccept> implements MachinProcedureAcceptService {

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Autowired
    private MachinProcedureFarmService machinProcedureFarmService;

    @Autowired
    private MachinProcedureAcceptChildService machinProcedureAcceptChildService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private DepartmentStockService departmentStockService;

    @Autowired
    protected MaterialNormsCodeService materialNormsCodeService;

    @Autowired
    private MachinProcedureAcceptChildCodeService machinProcedureAcceptChildCodeService;

    @Autowired
    private MachinProcedureAcceptProductNumService machinProcedureAcceptProductNumService;

    @Autowired
    private FarmService farmService;

    @Override
    public void validatorEntity(MachinProcedureAccept entity) {
        if (StrUtil.isEmpty(entity.getId())) {
            MachinProcedureFarm machinProcedureFarm = machinProcedureFarmService.selectById(entity.getMachinProcedureFarmId());
            entity.setMachinId(machinProcedureFarm.getMachinId());
            entity.setDepartmentId(machinProcedureFarm.getFarmMation().getDepartmentId());
            entity.setFarmId(machinProcedureFarm.getFarmId());
            entity.setMachinProcedureId(machinProcedureFarm.getMachinProcedureId());
        } else {
            MachinProcedureAccept oldEntity = super.selectById(entity.getId());
            entity.setMachinId(oldEntity.getMachinId());
            entity.setDepartmentId(oldEntity.getDepartmentId());
            entity.setFarmId(oldEntity.getFarmId());
            entity.setMachinProcedureId(oldEntity.getMachinProcedureId());
        }

        // 实际验收总数量 = 合格数量 + 返工数量 + 报废数量
        String qualifiedNum = StrUtil.isEmpty(entity.getQualifiedNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getQualifiedNum();
        String reworkNum = StrUtil.isEmpty(entity.getReworkNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getReworkNum();
        String scrapNum = StrUtil.isEmpty(entity.getScrapNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getScrapNum();
        String tempNum = CalculationUtil.add(
            CalculationUtil.add(qualifiedNum, reworkNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP),
            scrapNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
        String acceptNum = StrUtil.isEmpty(entity.getAcceptNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getAcceptNum();
        if (CalculationUtil.compareTo(acceptNum, tempNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
            throw new CustomException("验收数量不等于【合格数量】 + 【返工数量】 + 【报废数量】，请确认.");
        }

        // 校验员工生产数量列表
        checkProductNumList(entity);
        boolean isCompleted = machinProcedureService.checkPrevMachinProcedureIsCompleted(entity.getMachinProcedureId());
        if (!isCompleted) {
            throw new CustomException("请先完成上一个工序的验收");
        }

        // 校验并修改条形码信息
        checkNormsCodeAndSave(entity, true);
    }

    private void checkProductNumList(MachinProcedureAccept entity) {
        if (CollectionUtil.isEmpty(entity.getMachinProcedureAcceptProductNumList())) {
            throw new CustomException("请填写验收人生产数量列表");
        }
        String allNumLast = CommonNumConstants.NUM_ZERO.toString();
        String qualifiedNumLast = CommonNumConstants.NUM_ZERO.toString();
        String reworkNumLast = CommonNumConstants.NUM_ZERO.toString();
        String scrapNumLast = CommonNumConstants.NUM_ZERO.toString();
        for (MachinProcedureAcceptProductNum ProductNum : entity.getMachinProcedureAcceptProductNumList()) {
            // 如果总数量 != 合格数量 +返工数量 +报废数量 则提示
            String allNumber = StrUtil.isEmpty(ProductNum.getAllNumber()) ? CommonNumConstants.NUM_ZERO.toString() : String.valueOf(ProductNum.getAllNumber());
            String qualifiedNum = StrUtil.isEmpty(ProductNum.getQualifiedNum()) ? CommonNumConstants.NUM_ZERO.toString() : String.valueOf(ProductNum.getQualifiedNum());
            String reworkNum = StrUtil.isEmpty(ProductNum.getReworkNum()) ? CommonNumConstants.NUM_ZERO.toString() : String.valueOf(ProductNum.getReworkNum());
            String scrapNum = StrUtil.isEmpty(ProductNum.getScrapNum()) ? CommonNumConstants.NUM_ZERO.toString() : String.valueOf(ProductNum.getScrapNum());
            String sumNum = CalculationUtil.add(
                CalculationUtil.add(qualifiedNum, reworkNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP),
                scrapNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            if (CalculationUtil.compareTo(allNumber, sumNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
                throw new CustomException("总数量 != 合格数量 + 返工数量 + 报废数量");
            }
            allNumLast = CalculationUtil.add(allNumLast, allNumber, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            qualifiedNumLast = CalculationUtil.add(qualifiedNumLast, qualifiedNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            reworkNumLast = CalculationUtil.add(reworkNumLast, reworkNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            scrapNumLast = CalculationUtil.add(scrapNumLast, scrapNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
        }
        String acceptNum = StrUtil.isEmpty(entity.getAcceptNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getAcceptNum();
        String entityQualifiedNum = StrUtil.isEmpty(entity.getQualifiedNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getQualifiedNum();
        String entityReworkNum = StrUtil.isEmpty(entity.getReworkNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getReworkNum();
        String entityScrapNum = StrUtil.isEmpty(entity.getScrapNum()) ? CommonNumConstants.NUM_ZERO.toString() : entity.getScrapNum();
        if (CalculationUtil.compareTo(acceptNum, allNumLast, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0
            || CalculationUtil.compareTo(entityQualifiedNum, qualifiedNumLast, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0
            || CalculationUtil.compareTo(entityReworkNum, reworkNumLast, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0
            || CalculationUtil.compareTo(entityScrapNum, scrapNumLast, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
            throw new CustomException("员工生产数量需要与验收数量一致");
        }
    }

    @Override
    public void writePostpose(MachinProcedureAccept entity, String userId) {
        List<MachinProcedureAcceptChild> childList = new ArrayList<>();
        mergeAcceptChild(entity, childList);
        machinProcedureAcceptChildService.saveList(entity.getId(), childList);
        // 保存商品规格编码信息
        saveErpOrderItemCode(entity);
        // 保存/修改员工生产数量信息
        entity.getMachinProcedureAcceptProductNumList().forEach(productNum -> {
            productNum.setParentId(entity.getId());
            productNum.setMaterialId(productNum.getMaterialId());
            productNum.setNormsId(productNum.getNormsId());
        });
        if (StrUtil.isNotEmpty(entity.getId())) {
            // 更新操作删除原有员工生产数量信息
            machinProcedureAcceptProductNumService.deleteByParentId(entity.getId());
        }
        machinProcedureAcceptProductNumService.writeList(entity.getId(), entity.getMachinProcedureAcceptProductNumList());
        super.writePostpose(entity, userId);
    }

    @Override
    protected QueryWrapper<MachinProcedureAccept> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MachinProcedureAccept> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAccept::getFarmId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        farmService.setMationForMap(beans, "farmId", "farmMation");
        return beans;
    }

    private void saveErpOrderItemCode(MachinProcedureAccept entity) {
        List<String> materialIdList = entity.getMachinProcedureAcceptChildList().stream()
            .map(MachinProcedureAcceptChild::getMaterialId).distinct().collect(Collectors.toList());
        List<String> scrapMaterialIdList = entity.getMachinScrapProcedureAcceptChildList().stream()
            .map(MachinProcedureAcceptChild::getMaterialId).distinct().collect(Collectors.toList());
        materialIdList.addAll(scrapMaterialIdList);
        materialIdList = materialIdList.stream().distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        // 保存单据子表关联的条形码编号信息
        List<MachinProcedureAcceptChildCode> machinProcedureAcceptChildCodeList = new ArrayList<>();
        // 正常耗材
        for (MachinProcedureAcceptChild machinProcedureAcceptChild : entity.getMachinProcedureAcceptChildList()) {
            Material material = materialMap.get(machinProcedureAcceptChild.getMaterialId());
            if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                // 一物一码
                // 过滤掉空的，并且去重
                List<String> normsCodeList = Arrays.asList(machinProcedureAcceptChild.getNormsCode().split("\n")).stream()
                    .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                String operNumber = StrUtil.isEmpty(machinProcedureAcceptChild.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : String.valueOf(machinProcedureAcceptChild.getOperNumber());
                if (CalculationUtil.compareTo(operNumber, String.valueOf(normsCodeList.size()), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "商品【%s】的条形码数量与明细数量不一致，请确认", material.getName()));
                }
                normsCodeList.forEach(normsCode -> {
                    MachinProcedureAcceptChildCode erpOrderItemCode = new MachinProcedureAcceptChildCode();
                    erpOrderItemCode.setNormsCode(normsCode);
                    erpOrderItemCode.setMaterialId(machinProcedureAcceptChild.getMaterialId());
                    erpOrderItemCode.setNormsId(machinProcedureAcceptChild.getNormsId());
                    erpOrderItemCode.setParentId(machinProcedureAcceptChild.getId());
                    machinProcedureAcceptChildCodeList.add(erpOrderItemCode);
                });
            }
        }
        // 报废耗材
        for (MachinProcedureAcceptChild machinProcedureAcceptChild : entity.getMachinScrapProcedureAcceptChildList()) {
            Material material = materialMap.get(machinProcedureAcceptChild.getMaterialId());
            if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                // 一物一码
                // 过滤掉空的，并且去重
                List<String> normsCodeList = Arrays.asList(machinProcedureAcceptChild.getNormsCode().split("\n")).stream()
                    .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                String operNumber = StrUtil.isEmpty(machinProcedureAcceptChild.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : String.valueOf(machinProcedureAcceptChild.getOperNumber());
                if (CalculationUtil.compareTo(operNumber, String.valueOf(normsCodeList.size()), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "商品【%s】的条形码数量与明细数量不一致，请确认", material.getName()));
                }
                normsCodeList.forEach(normsCode -> {
                    MachinProcedureAcceptChildCode erpOrderItemCode = new MachinProcedureAcceptChildCode();
                    erpOrderItemCode.setNormsCode(normsCode);
                    erpOrderItemCode.setMaterialId(machinProcedureAcceptChild.getMaterialId());
                    erpOrderItemCode.setNormsId(machinProcedureAcceptChild.getNormsId());
                    erpOrderItemCode.setParentId(machinProcedureAcceptChild.getId());
                    machinProcedureAcceptChildCodeList.add(erpOrderItemCode);
                });
            }
        }
        machinProcedureAcceptChildCodeService.saveList(entity.getId(), machinProcedureAcceptChildCodeList);
    }

    @Override
    public MachinProcedureAccept getDataFromDb(String id) {
        MachinProcedureAccept machinProcedureAccept = super.getDataFromDb(id);
        // 设置耗材信息
        List<MachinProcedureAcceptChild> machinProcedureAcceptChildList = machinProcedureAcceptChildService.selectByParentId(id);
        // 查询单据子表关联的条形码编号信息
        Map<String, List<String>> codeMap = machinProcedureAcceptChildCodeService.selectMapByOrderId(id);
        machinProcedureAcceptChildList.forEach(machinProcedureAcceptChild -> {
            String key = String.format(Locale.ROOT, "%s_%s", machinProcedureAcceptChild.getId(), machinProcedureAcceptChild.getNormsId());
            List<String> codeList = codeMap.get(key);
            if (CollectionUtil.isNotEmpty(codeList)) {
                machinProcedureAcceptChild.setNormsCodeList(codeList);
                machinProcedureAcceptChild.setNormsCode(Joiner.on("\n").join(codeList));
            }
        });

        Map<Integer, List<MachinProcedureAcceptChild>> childMap = machinProcedureAcceptChildList.stream()
            .collect(Collectors.groupingBy(MachinProcedureAcceptChild::getType));
        machinProcedureAccept.setMachinProcedureAcceptChildList(childMap.get(MachinProcedureAcceptChildType.NORMAL.getKey()));
        machinProcedureAccept.setMachinScrapProcedureAcceptChildList(childMap.get(MachinProcedureAcceptChildType.SCRAP.getKey()));
        return machinProcedureAccept;
    }

    @Override
    public MachinProcedureAccept selectById(String id) {
        MachinProcedureAccept machinProcedureAccept = super.selectById(id);
        // 设置产品/规格信息
        materialService.setDataMation(machinProcedureAccept.getMachinProcedureAcceptChildList(), MachinProcedureAcceptChild::getMaterialId);
        materialNormsService.setDataMation(machinProcedureAccept.getMachinProcedureAcceptChildList(), MachinProcedureAcceptChild::getNormsId);

        materialService.setDataMation(machinProcedureAccept.getMachinScrapProcedureAcceptChildList(), MachinProcedureAcceptChild::getMaterialId);
        materialNormsService.setDataMation(machinProcedureAccept.getMachinScrapProcedureAcceptChildList(), MachinProcedureAcceptChild::getNormsId);

        if (CollectionUtil.isNotEmpty(machinProcedureAccept.getMachinProcedureAcceptChildList())) {
            machinProcedureAccept.getMachinProcedureAcceptChildList().forEach(machinProcedureAcceptChild -> {
                machinProcedureAcceptChild.setTypeMation(MachinProcedureAcceptChildType.getMation(machinProcedureAcceptChild.getType()));
            });
        }

        if (CollectionUtil.isNotEmpty(machinProcedureAccept.getMachinScrapProcedureAcceptChildList())) {
            machinProcedureAccept.getMachinScrapProcedureAcceptChildList().forEach(machinProcedureAcceptChild -> {
                machinProcedureAcceptChild.setTypeMation(MachinProcedureAcceptChildType.getMation(machinProcedureAcceptChild.getType()));
            });
        }
        // 设置员工生产数量信息列表
        List<MachinProcedureAcceptProductNum> productNumList = machinProcedureAcceptProductNumService.queryListByParentIdOnly(id);
        List<String> staffIdList = productNumList.stream().map(MachinProcedureAcceptProductNum::getStaffId).filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIdList);
        productNumList.forEach(productNum -> {
            productNum.setStaffMation(staffMap.getOrDefault(productNum.getStaffId(), new HashMap<>()));
        });
        machinProcedureAccept.setMachinProcedureAcceptProductNumList(productNumList);
        iAuthUserService.setDataMation(machinProcedureAccept, MachinProcedureAccept::getAcceptUserId);
        machinProcedureAccept.setFarmMation(farmService.selectById(machinProcedureAccept.getFarmId()));
        return machinProcedureAccept;
    }

    @Override
    public void deletePostpose(String id) {
        machinProcedureAcceptChildService.deleteByParentId(id);
        // 删除关联的编码信息
        machinProcedureAcceptChildCodeService.deleteByOrderId(id);
        // 删除员工生产数量信息
        machinProcedureAcceptProductNumService.deleteByParentId(id);
    }

    @Override
    public void approvalEndIsSuccess(MachinProcedureAccept entity) {
        MachinProcedureAccept realEntity = selectById(entity.getId());
        // 获取车间任务
        MachinProcedureFarm machinProcedureFarm = machinProcedureFarmService.selectById(realEntity.getMachinProcedureFarmId());
        // 获取该任务下已经完成的量
        String allComplateNum = calcNumByMachinProcedureFarmId(realEntity.getMachinProcedureFarmId());
        // 计算未完成的量 = 车间任务目标量 - 已完成的量 - 当前单据合格的量
        String targetNum = StrUtil.isEmpty(machinProcedureFarm.getTargetNum())
            ? CommonNumConstants.NUM_ZERO.toString()
            : machinProcedureFarm.getTargetNum();
        String qualifiedNum = StrUtil.isEmpty(realEntity.getQualifiedNum())
            ? CommonNumConstants.NUM_ZERO.toString()
            : realEntity.getQualifiedNum();
        String noComplateNum = CalculationUtil.subtract(
            CalculationUtil.subtract(targetNum, allComplateNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP),
            qualifiedNum, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
        int compareResult = CalculationUtil.compareTo(noComplateNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
        if (compareResult == 0) {
            machinProcedureFarmService.editStateById(machinProcedureFarm.getId(), MachinProcedureFarmState.ALL_COMPLETED.getKey());
        } else if (compareResult > 0) {
            machinProcedureFarmService.editStateById(machinProcedureFarm.getId(), MachinProcedureFarmState.PARTIAL_COMPLETION.getKey());
        } else {
            machinProcedureFarmService.editStateById(machinProcedureFarm.getId(), MachinProcedureFarmState.EXCESS_COMPLETED.getKey());
        }
        // 校验并修改条形码信息
        checkNormsCodeAndSave(realEntity, false);
        // 根据正常耗材和报废耗材减少已分配库存
        reduceAllocatedStock(realEntity);
    }

    /**
     * 根据正常耗材和报废耗材减少已分配库存
     *
     * @param entity 工序验收单
     */
    private void reduceAllocatedStock(MachinProcedureAccept entity) {
        // 合并正常耗材和报废耗材
        List<MachinProcedureAcceptChild> childList = new ArrayList<>();
        mergeAcceptChild(entity, childList);
        // 遍历所有耗材，减少已分配库存
        if (CollectionUtil.isNotEmpty(childList)) {
            childList.forEach(acceptChild -> {
                String operNumber = StrUtil.isEmpty(acceptChild.getOperNumber())
                    ? CommonNumConstants.NUM_ZERO.toString()
                    : acceptChild.getOperNumber();
                // 减少已分配库存（正常耗材和报废耗材都需要减少）
                departmentStockService.updateDepartmentStock(entity.getDepartmentId(), entity.getFarmId(),
                    acceptChild.getMaterialId(), acceptChild.getNormsId(), operNumber,
                    DepotPutOutType.OUT.getKey(), MaterialNormsStockType.ALLOCATED_STOCK.getKey());
            });
        }
    }

    /**
     * 校验商品规格条形码与单据明细的参数是否匹配
     *
     * @param entity
     * @param onlyCheck 是否只进行校验，true：是；false：否
     */
    public List<String> checkNormsCodeAndSave(MachinProcedureAccept entity, Boolean onlyCheck) {
        // 合并正常消耗和报废耗材
        List<MachinProcedureAcceptChild> childList = new ArrayList<>();
        mergeAcceptChild(entity, childList);
        // 查询商品/规格信息
        List<String> materialIdList = childList.stream().map(MachinProcedureAcceptChild::getMaterialId).distinct().collect(Collectors.toList());
        List<String> normsIdList = childList.stream().map(MachinProcedureAcceptChild::getNormsId).distinct().collect(Collectors.toList());
        Map<String, Material> materialMap = materialService.selectMapByIds(materialIdList);
        Map<String, MaterialNorms> normsMap = materialNormsService.selectMapByIds(normsIdList);
        // 所有需要进行耗材处理的条形码编码
        List<String> allNormsCodeList = new ArrayList<>();
        Map<String, Integer> normsCodeType = new HashMap<>();
        int allCodeNum = checkErpOrderItemDetail(childList, materialMap, normsMap, allNormsCodeList, normsCodeType);
        if (CollectionUtil.isNotEmpty(allNormsCodeList)) {
            allNormsCodeList = allNormsCodeList.stream().distinct().collect(Collectors.toList());
            if (allCodeNum != allNormsCodeList.size()) {
                throw new CustomException("商品明细中存在相同的条形码编号，请确认");
            }
            // 1. 校验数量
            Map<String, String> stock = departmentStockService.queryNormsDepartmentStock(entity.getDepartmentId(), entity.getFarmId(), normsIdList);
            Map<String, String> collect = childList.stream()
                .collect(Collectors.groupingBy(MachinProcedureAcceptChild::getNormsId,
                    Collectors.reducing(CommonNumConstants.NUM_ZERO.toString(),
                        child -> StrUtil.isEmpty(child.getOperNumber()) ? CommonNumConstants.NUM_ZERO.toString() : String.valueOf(child.getOperNumber()),
                        (a, b) -> CalculationUtil.add(a, b, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP))));
            collect.forEach((normsId, changeNum) -> {
                String departmentFarmStock = stock.getOrDefault(normsId, CommonNumConstants.NUM_ZERO.toString());
                if (StrUtil.isEmpty(departmentFarmStock)) {
                    departmentFarmStock = CommonNumConstants.NUM_ZERO.toString();
                }
                if (CalculationUtil.compareTo(changeNum, departmentFarmStock, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) > 0) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "商品【%s】超出当前仓库的库存，请确认", normsMap.get(normsId).getName()));
                }
            });
            // 2. 校验条形码
            //  2.1 从数据库查询出库状态的条形码信息，
            //  2.2 只有部门信息不为空的说明已经领料，才可以进行工序耗材。
            List<MaterialNormsCode> materialNormsCodeList = materialNormsCodeService.queryMaterialNormsCodeByCodeNum(StrUtil.EMPTY, allNormsCodeList,
                MaterialNormsCodeInDepot.OUTBOUND.getKey());
            materialNormsCodeList = materialNormsCodeList.stream()
                .filter(bean -> StrUtil.isNotEmpty(bean.getDepartmentId()) && StrUtil.equals(entity.getDepartmentId(), bean.getDepartmentId()))
                .collect(Collectors.toList());
            //  2.3 如果车间不为空，则需要获取过滤出当前车间的库存
            if (StrUtil.isNotEmpty(entity.getFarmId())) {
                materialNormsCodeList = materialNormsCodeList.stream()
                    .filter(bean -> StrUtil.isNotEmpty(bean.getFarmId()) && StrUtil.equals(entity.getFarmId(), bean.getFarmId()))
                    .collect(Collectors.toList());
            }
            //  1.4 只有未使用的可以进行工序耗材
            materialNormsCodeList = materialNormsCodeList.stream()
                .filter(bean -> PickNormsCodeUseState.WAIT_USE.getKey() == bean.getPickUseState())
                .collect(Collectors.toList());
            List<String> inSqlNormsCodeList = materialNormsCodeList.stream().map(MaterialNormsCode::getCodeNum).collect(Collectors.toList());
            // 获取所有前端传递过来的条形码信息，求差集(在入参中有，但是在数据库中不包含的条形码信息)
            List<String> diffList = allNormsCodeList.stream()
                .filter(num -> !inSqlNormsCodeList.contains(num)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(diffList)) {
                throw new CustomException(
                    String.format(Locale.ROOT, "编码【%s】不存在或已被使用，请确认", Joiner.on(CommonCharConstants.COMMA_MARK).join(diffList)));
            }
            if (!onlyCheck) {
                // 批量修改条形码信息
                materialNormsCodeList.forEach(materialNormsCode -> {
                    materialNormsCode.setPickUseState(PickNormsCodeUseState.USED.getKey());
                    materialNormsCode.setPickState(normsCodeType.get(materialNormsCode.getCodeNum()));
                });
                materialNormsCodeService.updateEntityPick(materialNormsCodeList);
            }
        }
        if (!onlyCheck) {
            // 修改部门/车间的库存
            childList.forEach(acceptChild -> {
                departmentStockService.updateDepartmentStock(entity.getDepartmentId(), entity.getFarmId(), acceptChild.getMaterialId(),
                    acceptChild.getNormsId(), acceptChild.getOperNumber(), DepotPutOutType.OUT.getKey(), MaterialNormsStockType.ORDER_STOCK.getKey());
            });
        }
        return allNormsCodeList;
    }

    private int checkErpOrderItemDetail(List<MachinProcedureAcceptChild> childList, Map<String, Material> materialMap,
                                        Map<String, MaterialNorms> normsMap, List<String> allNormsCodeList,
                                        Map<String, Integer> normsCodeType) {
        int allCodeNum = 0;
        for (MachinProcedureAcceptChild acceptChild : childList) {
            Material material = materialMap.get(acceptChild.getMaterialId());
            MaterialNorms norms = normsMap.get(acceptChild.getNormsId());
            String operNumber = StrUtil.isEmpty(acceptChild.getOperNumber())
                ? CommonNumConstants.NUM_ZERO.toString()
                : String.valueOf(acceptChild.getOperNumber());
            if (CalculationUtil.compareTo(operNumber, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) == 0) {
                throw new CustomException(
                    String.format(Locale.ROOT, "商品【%s】【%s】的数量不能为0，请确认", material.getName(), norms.getName()));
            }
            if (material.getItemCode() == MaterialItemCode.ONE_ITEM_CODE.getKey()) {
                // 一物一码
                // 过滤掉空的，并且去重
                List<String> normsCodeList = Arrays.asList(acceptChild.getNormsCode().split("\n")).stream()
                    .filter(str -> StrUtil.isNotEmpty(str)).distinct().collect(Collectors.toList());
                if (CalculationUtil.compareTo(operNumber, String.valueOf(normsCodeList.size()), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) != 0) {
                    throw new CustomException(
                        String.format(Locale.ROOT, "商品【%s】【%s】的条形码数量与明细数量不一致，请确认", material.getName(), norms.getName()));
                }
                allCodeNum += normsCodeList.size();
                acceptChild.setNormsCodeList(normsCodeList);
                normsCodeList.forEach(normsCode -> {
                    normsCodeType.put(normsCode, acceptChild.getType());
                });
                allNormsCodeList.addAll(normsCodeList);
            }
        }
        return allCodeNum;
    }

    private static void mergeAcceptChild(MachinProcedureAccept entity, List<MachinProcedureAcceptChild> childList) {
        if (CollectionUtil.isNotEmpty(entity.getMachinProcedureAcceptChildList())) {
            entity.getMachinProcedureAcceptChildList().forEach(child -> {
                child.setType(MachinProcedureAcceptChildType.NORMAL.getKey());
                childList.add(child);
            });
        }
        if (CollectionUtil.isNotEmpty(entity.getMachinScrapProcedureAcceptChildList())) {
            entity.getMachinScrapProcedureAcceptChildList().forEach(child -> {
                child.setType(MachinProcedureAcceptChildType.SCRAP.getKey());
                childList.add(child);
            });
        }
    }

    @Override
    public String calcNumByMachinProcedureFarmId(String machinProcedureFarmId) {
        QueryWrapper<MachinProcedureAccept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAccept::getMachinProcedureFarmId), machinProcedureFarmId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Machin::getState), FlowableStateEnum.PASS.getKey());
        List<MachinProcedureAccept> machinList = list(queryWrapper);
        String allNum = machinList.stream()
            .map(accept -> StrUtil.isEmpty(accept.getQualifiedNum()) ? CommonNumConstants.NUM_ZERO.toString() : accept.getQualifiedNum())
            .reduce(CommonNumConstants.NUM_ZERO.toString(),
                (a, b) -> CalculationUtil.add(a, b, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP));
        return allNum;
    }

    @Override
    public String calcAllNumByMachinProcedureFarmId(String machinProcedureFarmId) {
        QueryWrapper<MachinProcedureAccept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAccept::getMachinProcedureFarmId), machinProcedureFarmId);
        List<MachinProcedureAccept> machinList = list(queryWrapper);
        String allNum = machinList.stream()
            .map(accept -> StrUtil.isEmpty(accept.getQualifiedNum()) ? CommonNumConstants.NUM_ZERO.toString() : accept.getQualifiedNum())
            .reduce(CommonNumConstants.NUM_ZERO.toString(),
                (a, b) -> CalculationUtil.add(a, b, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP));
        return allNum;
    }

    @Override
    public Map<String, List<MachinProcedureAccept>> queryMachinProcedureAcceptByMachinProcedureFarmId(String... machinProcedureFarmId) {
        List<String> machinProcedureFarmIdList = Arrays.asList(machinProcedureFarmId);
        if (CollectionUtil.isEmpty(machinProcedureFarmIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<MachinProcedureAccept> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MachinProcedureAccept::getMachinProcedureFarmId), machinProcedureFarmIdList);
        List<MachinProcedureAccept> machinList = list(queryWrapper);
        Map<String, List<MachinProcedureAccept>> map = machinList.stream()
            .collect(Collectors.groupingBy(MachinProcedureAccept::getMachinProcedureFarmId));
        return map;
    }

    @Override
    public List<MachinProcedureAccept> queryListByMachinProcedureId(String machinProcedureId) {
        if (StrUtil.isEmpty(machinProcedureId)) {
            return new ArrayList<>();
        }
        QueryWrapper<MachinProcedureAccept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAccept::getMachinProcedureId), machinProcedureId);
        List<MachinProcedureAccept> machinList = list(queryWrapper);
        return machinList;
    }

    @Override
    public List<MachinProcedureAccept> queryListByMachinProcedureIdList(List<String> machinProcedureIdList) {
        if (CollectionUtil.isEmpty(machinProcedureIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<MachinProcedureAccept> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MachinProcedureAccept::getMachinProcedureId), machinProcedureIdList);
        List<MachinProcedureAccept> machinList = list(queryWrapper);
        return machinList;
    }

    @Override
    public List<MachinProcedureAccept> queryProcedureAcceptByIds(List<String> procedureAcceptIdList) {
        if (CollectionUtil.isEmpty(procedureAcceptIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<MachinProcedureAccept> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, procedureAcceptIdList);
        return list(queryWrapper);
    }
}
