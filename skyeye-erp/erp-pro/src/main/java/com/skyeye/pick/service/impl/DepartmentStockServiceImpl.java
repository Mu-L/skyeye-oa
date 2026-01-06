/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.classenum.MaterialNormsStockType;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.dao.DepartmentStockDao;
import com.skyeye.pick.entity.DepartmentStock;
import com.skyeye.pick.service.DepartmentStockService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DepartmentStockServiceImpl
 * @Description: 部门物料库存信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/31 16:58
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "部门/车间物料库存信息", groupName = "部门/车间物料库存", manageShow = false)
public class DepartmentStockServiceImpl extends SkyeyeBusinessServiceImpl<DepartmentStockDao, DepartmentStock> implements DepartmentStockService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private FarmService farmService;

    @Override
    @IgnoreTenant
    public void queryDepartmentStockList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());

        // 1. 先查询 MaterialNorms 作为主表，DepartmentStock 作为 left join
        QueryWrapper<DepartmentStock> wrapper = new QueryWrapper<>();

        // 添加查询条件（部门或车间）
        if (StrUtil.equals(commonPageInfo.getType(), "department")) {
            String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
            wrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        } else if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
            if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
                commonPageInfo.setObjectId("-");
            }
            wrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), commonPageInfo.getObjectId());
        }

        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            wrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
        }

        wrapper.groupBy(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId));
        // 查询 MaterialNorms 列表
        List<DepartmentStock> stocks = list(wrapper);

        // 获取 normsIds
        List<String> normsIds = stocks.stream()
            .map(DepartmentStock::getNormsId)
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());

        // 查询 ORDER_STOCK 数据（单独查询以确保数据完整）
        Map<String, DepartmentStock> orderStockDataMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(normsIds)) {
            QueryWrapper<DepartmentStock> orderStockWrapper = new QueryWrapper<>();
            orderStockWrapper.in(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsIds)
                .eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), MaterialNormsStockType.ORDER_STOCK.getKey());
            if (StrUtil.equals(commonPageInfo.getType(), "department")) {
                String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
                orderStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
            } else if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
                orderStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), commonPageInfo.getObjectId());
            }
            if (tenantEnable) {
                String tenantId = TenantContext.getTenantId();
                orderStockWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
            }
            List<DepartmentStock> orderStockList = list(orderStockWrapper);
            orderStockDataMap = orderStockList.stream()
                .collect(Collectors.toMap(DepartmentStock::getNormsId, stock -> stock, (v1, v2) -> v1));
        }

        Map<String, DepartmentStock> inTransitStockMap = new HashMap<>();
        Map<String, DepartmentStock> allocatedStockMap = new HashMap<>();

        if (CollectionUtil.isNotEmpty(normsIds)) {
            // 查询 IN_TRANSIT_STOCK
            QueryWrapper<DepartmentStock> inTransitWrapper = new QueryWrapper<>();
            inTransitWrapper.in(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsIds)
                .eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), MaterialNormsStockType.IN_TRANSIT_STOCK.getKey());
            if (StrUtil.equals(commonPageInfo.getType(), "department")) {
                String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
                inTransitWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
            } else if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
                inTransitWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), commonPageInfo.getObjectId());
            }
            if (tenantEnable) {
                String tenantId = TenantContext.getTenantId();
                inTransitWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
            }
            List<DepartmentStock> inTransitStockList = list(inTransitWrapper);
            inTransitStockMap = inTransitStockList.stream()
                .collect(Collectors.toMap(DepartmentStock::getNormsId, stock -> stock, (v1, v2) -> v1));

            // 查询 ALLOCATED_STOCK
            QueryWrapper<DepartmentStock> allocatedWrapper = new QueryWrapper<>();
            allocatedWrapper.in(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsIds)
                .eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), MaterialNormsStockType.ALLOCATED_STOCK.getKey());
            if (StrUtil.equals(commonPageInfo.getType(), "department")) {
                String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
                allocatedWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
            } else if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
                allocatedWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), commonPageInfo.getObjectId());
            }
            if (tenantEnable) {
                String tenantId = TenantContext.getTenantId();
                allocatedWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
            }
            List<DepartmentStock> allocatedStockList = list(allocatedWrapper);
            allocatedStockMap = allocatedStockList.stream()
                .collect(Collectors.toMap(DepartmentStock::getNormsId, stock -> stock, (v1, v2) -> v1));
        }

        // 3. 合并数据
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (DepartmentStock departmentStock : stocks) {
            Map<String, Object> resultMap = new HashMap<>();
            String normsId = departmentStock.getNormsId();

            // 复制 MaterialNorms 的基本信息
            resultMap.put("id", normsId);
            resultMap.put("materialId", departmentStock.getMaterialId());
            resultMap.put("normsId", normsId);
            resultMap.put("departmentId", departmentStock.getDepartmentId());
            resultMap.put("farmId", departmentStock.getFarmId());

            // 当前库存
            DepartmentStock orderStock = orderStockDataMap.get(normsId);
            if (orderStock != null) {
                resultMap.put("orderStock", orderStock.getStock());
            } else {
                resultMap.put("orderStock", CommonNumConstants.NUM_ZERO.toString());
            }

            // 在途物料
            DepartmentStock inTransitStock = normsId != null ? inTransitStockMap.get(normsId) : null;
            if (inTransitStock != null) {
                resultMap.put("inTransitStock", inTransitStock.getStock());
            } else {
                resultMap.put("inTransitStock", CommonNumConstants.NUM_ZERO.toString());
            }

            // 已分配库存
            DepartmentStock allocatedStock = normsId != null ? allocatedStockMap.get(normsId) : null;
            if (allocatedStock != null) {
                resultMap.put("allocatedStock", allocatedStock.getStock());
            } else {
                resultMap.put("allocatedStock", CommonNumConstants.NUM_ZERO.toString());
            }

            resultList.add(resultMap);
        }

        // 4. 设置关联信息
        materialService.setMationForMap(resultList, "materialId", "materialMation");
        materialNormsService.setMationForMap(resultList, "normsId", "normsMation");
        iDepmentService.setMationForMap(resultList, "departmentId", "departmentMation");
        farmService.setMationForMap(resultList, "farmId", "farmMation");

        outputObject.setBeans(resultList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void updateDepartmentStock(String departmentId, String farmId, String materialId, String normsId, String operNumber, int type, int stockType) {
        DepartmentStock departmentStock = queryDepartmentStock(departmentId, farmId, normsId, stockType);
        // 如果该规格在指定部门中已经有存储数据，则直接做修改
        if (ObjectUtil.isNotEmpty(departmentStock)) {
            String stock = StrUtil.isEmpty(departmentStock.getStock()) ? CommonNumConstants.NUM_ZERO.toString() : departmentStock.getStock();
            if (type == DepotPutOutType.PUT.getKey()) {
                // 入库
                stock = CalculationUtil.add(ErpConstants.NUM_AFTER_DOT, stock, StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber);
            } else if (type == DepotPutOutType.OUT.getKey()) {
                // 出库
                stock = CalculationUtil.subtract(stock, StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber, ErpConstants.NUM_AFTER_DOT);
            }
            stock = checkStockNum(stockType, stock);
            editDepartmentStock(departmentId, farmId, normsId, stock, stockType);
        } else {
            String stockNum = CommonNumConstants.NUM_ZERO.toString();
            if (type == DepotPutOutType.PUT.getKey()) {
                // 入库
                stockNum = StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber;
            } else if (type == DepotPutOutType.OUT.getKey()) {
                // 出库
                stockNum = CalculationUtil.subtract(CommonNumConstants.NUM_ZERO.toString(), StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber, ErpConstants.NUM_AFTER_DOT);
            }
            stockNum = checkStockNum(stockType, stockNum);
            saveDepartmentStock(departmentId, farmId, materialId, normsId, stockNum, stockType);
        }
    }

    @NotNull
    private static String checkStockNum(int stockType, String stockNum) {
        if (CalculationUtil.compareTo(stockNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
            if (stockType == MaterialNormsStockType.ORDER_STOCK.getKey()) {
                // 只有实际库存类型的出入库才做这个判断
                throw new CustomException("部门库存存量不足.");
            } else if (stockType == MaterialNormsStockType.IN_TRANSIT_STOCK.getKey() || stockType == MaterialNormsStockType.ALLOCATED_STOCK.getKey()) {
                // 在途物料 || 已分配物料 小于0时，设置为0
                stockNum = CommonNumConstants.NUM_ZERO.toString();
            }
        }
        return stockNum;
    }

    @Override
    public DepartmentStock queryDepartmentStock(String departmentId, String farmId, String normsId, int stockType) {
        QueryWrapper<DepartmentStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        if (StrUtil.isNotEmpty(farmId)) {
            // 如果车间id不为空，则说明修改的是车间的库存
            queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
        } else {
            // 如果车间id为空，则说明修改的是部门的库存
            String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
            queryWrapper.and(Wrapper -> {
                Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
            });
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), stockType);
        return getOne(queryWrapper);
    }

    private void editDepartmentStock(String departmentId, String farmId, String normsId, String stock, int stockType) {
        UpdateWrapper<DepartmentStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        if (StrUtil.isNotEmpty(farmId)) {
            // 如果车间id不为空，则说明修改的是车间的库存
            updateWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
        } else {
            // 如果车间id为空，则说明修改的是部门的库存
            String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
            updateWrapper.and(Wrapper -> {
                Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
            });
        }
        updateWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), stockType);
        updateWrapper.set(MybatisPlusUtil.toColumns(DepartmentStock::getStock), stock);
        updateWrapper.set(MybatisPlusUtil.toColumns(DepartmentStock::getLastUpdateTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
    }

    private void saveDepartmentStock(String departmentId, String farmId, String materialId, String normsId, String stock, int stockType) {
        DepartmentStock departmentStock = new DepartmentStock();
        departmentStock.setDepartmentId(departmentId);
        departmentStock.setFarmId(farmId);
        departmentStock.setMaterialId(materialId);
        departmentStock.setNormsId(normsId);
        departmentStock.setStock(stock);
        departmentStock.setType(stockType);
        String createTime = DateUtil.getTimeAndToString();
        departmentStock.setCreateTime(createTime);
        departmentStock.setLastUpdateTime(createTime);
        save(departmentStock);
    }

    @Override
    public Map<String, String> queryNormsDepartmentStock(String departmentId, String farmId, List<String> normsIds) {
        // 默认不包含在途库存，保持向后兼容
        return queryNormsDepartmentStock(departmentId, farmId, normsIds, true);
    }

    @Override
    public Map<String, String> queryNormsDepartmentStock(String departmentId, String farmId, List<String> normsIds, boolean includeInTransitStock) {
        if (CollectionUtil.isEmpty(normsIds)) {
            return new HashMap<>();
        }

        // 1. 查询 ORDER_STOCK（现有库存）
        QueryWrapper<DepartmentStock> orderStockWrapper = new QueryWrapper<>();
        orderStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        if (StrUtil.isNotEmpty(farmId)) {
            orderStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
        } else {
            String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
            orderStockWrapper.and(Wrapper -> {
                Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
            });
        }
        orderStockWrapper.in(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsIds)
            .eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), MaterialNormsStockType.ORDER_STOCK.getKey());
        List<DepartmentStock> orderStockList = list(orderStockWrapper);
        Map<String, String> orderStockMap = orderStockList.stream()
            .collect(Collectors.toMap(
                DepartmentStock::getNormsId,
                stock -> StrUtil.isEmpty(stock.getStock()) ? CommonNumConstants.NUM_ZERO.toString() : stock.getStock(),
                (v1, v2) -> v1
            ));

        // 2. 查询 IN_TRANSIT_STOCK（在途物料/在制物料）- 根据参数决定是否查询
        Map<String, String> inTransitStockMap = new HashMap<>();
        if (includeInTransitStock) {
            QueryWrapper<DepartmentStock> inTransitStockWrapper = new QueryWrapper<>();
            inTransitStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
            if (StrUtil.isNotEmpty(farmId)) {
                inTransitStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
            } else {
                String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
                inTransitStockWrapper.and(Wrapper -> {
                    Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
                });
            }
            inTransitStockWrapper.in(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsIds)
                .eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), MaterialNormsStockType.IN_TRANSIT_STOCK.getKey());
            List<DepartmentStock> inTransitStockList = list(inTransitStockWrapper);
            inTransitStockMap = inTransitStockList.stream()
                .collect(Collectors.toMap(
                    DepartmentStock::getNormsId,
                    stock -> StrUtil.isEmpty(stock.getStock()) ? CommonNumConstants.NUM_ZERO.toString() : stock.getStock(),
                    (v1, v2) -> v1
                ));
        }

        // 3. 查询 ALLOCATED_STOCK（已分配量）
        QueryWrapper<DepartmentStock> allocatedStockWrapper = new QueryWrapper<>();
        allocatedStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        if (StrUtil.isNotEmpty(farmId)) {
            allocatedStockWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
        } else {
            String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
            allocatedStockWrapper.and(Wrapper -> {
                Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
            });
        }
        allocatedStockWrapper.in(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsIds)
            .eq(MybatisPlusUtil.toColumns(DepartmentStock::getType), MaterialNormsStockType.ALLOCATED_STOCK.getKey());
        List<DepartmentStock> allocatedStockList = list(allocatedStockWrapper);
        Map<String, String> allocatedStockMap = allocatedStockList.stream()
            .collect(Collectors.toMap(
                DepartmentStock::getNormsId,
                stock -> StrUtil.isEmpty(stock.getStock()) ? CommonNumConstants.NUM_ZERO.toString() : stock.getStock(),
                (v1, v2) -> v1
            ));

        // 4. 计算可用库存
        Map<String, String> stockMap = new HashMap<>();
        for (String normsId : normsIds) {
            String orderStock = orderStockMap.getOrDefault(normsId, CommonNumConstants.NUM_ZERO.toString());
            String allocatedStock = allocatedStockMap.getOrDefault(normsId, CommonNumConstants.NUM_ZERO.toString());
            String availableStock;

            if (includeInTransitStock) {
                // MRP计算：可用库存 = 现有库存 + 在途库存 - 已分配量
                String inTransitStock = inTransitStockMap.getOrDefault(normsId, CommonNumConstants.NUM_ZERO.toString());
                // 先计算：现有库存 + 在途库存
                String totalStock = CalculationUtil.add(orderStock, inTransitStock, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
                // 再计算：总库存 - 已分配量 = 可用库存
                availableStock = CalculationUtil.subtract(totalStock, allocatedStock, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            } else {
                // 简单计算：可用库存 = 现有库存 - 已分配量
                availableStock = CalculationUtil.subtract(orderStock, allocatedStock, ErpConstants.NUM_AFTER_DOT, RoundingMode.UP);
            }

            // 如果计算结果小于0，则返回0
            if (CalculationUtil.compareTo(availableStock, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                availableStock = CommonNumConstants.NUM_ZERO.toString();
            }

            stockMap.put(normsId, availableStock);
        }

        return stockMap;
    }

}
