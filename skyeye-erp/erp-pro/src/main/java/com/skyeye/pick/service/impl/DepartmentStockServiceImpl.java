/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.ErpConstants;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.pick.dao.DepartmentStockDao;
import com.skyeye.pick.entity.DepartmentStock;
import com.skyeye.pick.service.DepartmentStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
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
    public QueryWrapper<DepartmentStock> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DepartmentStock> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "department")) {
            // 我所在部门
            String departmentId = InputObject.getLogParamsStatic().get("departmentId").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        } else if (StrUtil.equals(commonPageInfo.getType(), "farm")) {
            // 指定车间
            if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
                commonPageInfo.setObjectId("-");
            }
            queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        farmService.setMationForMap(beans, "farmId", "farmMation");
        return beans;
    }

    @Override
    public void updateDepartmentStock(String departmentId, String farmId, String materialId, String normsId, String operNumber, int type) {
        DepartmentStock departmentStock = queryDepartmentStock(departmentId, farmId, normsId);
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
            editDepartmentStock(departmentId, farmId, normsId, stock);
        } else {
            String stockNum = CommonNumConstants.NUM_ZERO.toString();
            if (type == DepotPutOutType.PUT.getKey()) {
                // 入库
                stockNum = StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber;
            } else if (type == DepotPutOutType.OUT.getKey()) {
                // 出库
                stockNum = CalculationUtil.subtract(CommonNumConstants.NUM_ZERO.toString(), StrUtil.isEmpty(operNumber) ? CommonNumConstants.NUM_ZERO.toString() : operNumber, ErpConstants.NUM_AFTER_DOT);
            }
            if (CalculationUtil.compareTo(stockNum, CommonNumConstants.NUM_ZERO.toString(), ErpConstants.NUM_AFTER_DOT, RoundingMode.UP) < 0) {
                throw new CustomException("部门库存存量不足.");
            }
            saveDepartmentStock(departmentId, farmId, materialId, normsId, stockNum);
        }
    }

    @Override
    public DepartmentStock queryDepartmentStock(String departmentId, String farmId, String normsId) {
        QueryWrapper<DepartmentStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        if (StrUtil.isNotEmpty(farmId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
        } else {
            String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
            queryWrapper.and(Wrapper -> {
                Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
            });
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsId);
        return getOne(queryWrapper);
    }

    private void editDepartmentStock(String departmentId, String farmId, String normsId, String stock) {
        UpdateWrapper<DepartmentStock> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        if (StrUtil.isNotEmpty(farmId)) {
            updateWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
        } else {
            String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
            updateWrapper.and(Wrapper -> {
                Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
            });
        }
        updateWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsId);
        updateWrapper.set(MybatisPlusUtil.toColumns(DepartmentStock::getStock), stock);
        update(updateWrapper);
    }

    private void saveDepartmentStock(String departmentId, String farmId, String materialId, String normsId, String stock) {
        DepartmentStock departmentStock = new DepartmentStock();
        departmentStock.setDepartmentId(departmentId);
        departmentStock.setFarmId(farmId);
        departmentStock.setMaterialId(materialId);
        departmentStock.setNormsId(normsId);
        departmentStock.setStock(stock);
        save(departmentStock);
    }

    @Override
    public Map<String, String> queryNormsDepartmentStock(String departmentId, String farmId, List<String> normsIds) {
        QueryWrapper<DepartmentStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getDepartmentId), departmentId);
        if (StrUtil.isNotEmpty(farmId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DepartmentStock::getFarmId), farmId);
        } else {
            String farmIdKey = MybatisPlusUtil.toColumns(DepartmentStock::getFarmId);
            queryWrapper.and(Wrapper -> {
                Wrapper.isNull(farmIdKey).or().eq(farmIdKey, StrUtil.EMPTY);
            });
        }
        queryWrapper.in(MybatisPlusUtil.toColumns(DepartmentStock::getNormsId), normsIds);
        List<DepartmentStock> departmentStockList = list(queryWrapper);

        Map<String, String> stockMap = departmentStockList.stream()
            .collect(Collectors.toMap(
                DepartmentStock::getNormsId,
                stock -> StrUtil.isEmpty(stock.getStock()) ? CommonNumConstants.NUM_ZERO.toString() : stock.getStock()
            ));
        normsIds.forEach(normsId -> {
            if (!stockMap.containsKey(normsId)) {
                stockMap.put(normsId, CommonNumConstants.NUM_ZERO.toString());
            }
        });
        return stockMap;
    }

}
