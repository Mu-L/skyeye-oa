/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.depot.classenum.DepotPutOutType;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.dao.EquipmentSparePartUsageDetailDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentSparePartUsageDetailService;
import com.skyeye.rest.sealservice.service.IServiceUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 维修工单备件使用明细
 */
@Service
@SkyeyeService(name = "维修工单备件使用明细", groupName = "设备维修", manageShow = false)
public class EquipmentSparePartUsageDetailServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartUsageDetailDao, EquipmentSparePartUsageDetail>
    implements EquipmentSparePartUsageDetailService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IServiceUserStockService iServiceUserStockService;

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveByRepairOrderId(String repairOrderId, List<EquipmentSparePartUsageDetail> detailList) {
        EquipmentRepairOrder repairOrder = equipmentRepairOrderService.selectById(repairOrderId);
        checkRepairOrder(repairOrder);
        String stockUserId = repairOrder.getServiceUserId();

        List<EquipmentSparePartUsageDetail> oldList = selectByPId(repairOrderId);
        changeUserStock(stockUserId, oldList, DepotPutOutType.PUT.getKey());

        if (CollectionUtil.isEmpty(detailList)) {
            deleteByPId(repairOrderId);
            equipmentRepairOrderService.refreshCache(repairOrderId);
            return;
        }

        calcDetailPrice(detailList);
        validateUserStock(stockUserId, detailList);
        saveLinkList(repairOrderId, detailList);
        changeUserStock(stockUserId, detailList, DepotPutOutType.OUT.getKey());
        equipmentRepairOrderService.refreshCache(repairOrderId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void revertAndDeleteByRepairOrderId(String repairOrderId, String stockUserId) {
        List<EquipmentSparePartUsageDetail> oldList = selectByPId(repairOrderId);
        if (CollectionUtil.isEmpty(oldList)) {
            return;
        }
        changeUserStock(stockUserId, oldList, DepotPutOutType.PUT.getKey());
        deleteByPId(repairOrderId);
        equipmentRepairOrderService.refreshCache(repairOrderId);
    }

    @Override
    public void calcDetailPrice(List<EquipmentSparePartUsageDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        List<String> materialIds = detailList.stream()
            .map(EquipmentSparePartUsageDetail::getMaterialId)
            .collect(Collectors.toList());
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        for (EquipmentSparePartUsageDetail detail : detailList) {
            List<MaterialNorms> normsList = normsMap.get(detail.getMaterialId());
            if (CollectionUtil.isEmpty(normsList)) {
                throw new CustomException("数据中包含不存在的备件规格信息.");
            }
            MaterialNorms matchedNorms = normsList.stream()
                .filter(norms -> StrUtil.equals(norms.getId(), detail.getNormsId()))
                .findFirst()
                .orElseThrow(() -> new CustomException("数据中包含不存在的备件规格信息."));
            if (StrUtil.isBlank(matchedNorms.getRetailPrice())) {
                throw new CustomException("备件规格未维护零售价.");
            }
            String unitPrice = matchedNorms.getRetailPrice();
            String rowAllPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, detail.getOperNumber(), unitPrice);
            detail.setUnitPrice(unitPrice);
            detail.setAllPrice(rowAllPrice);
        }
    }

    @Override
    protected void checkLinkList(String pId, List<EquipmentSparePartUsageDetail> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        if (StrUtil.isBlank(pId)) {
            throw new CustomException("维修单ID不能为空.");
        }
        boolean missingMaterialId = beans.stream().anyMatch(bean -> bean == null || StrUtil.isBlank(bean.getMaterialId()));
        if (missingMaterialId) {
            throw new CustomException("请为每条明细选择备件");
        }
        List<String> normsIds = beans.stream()
            .map(EquipmentSparePartUsageDetail::getNormsId)
            .distinct()
            .collect(Collectors.toList());
        if (normsIds.size() != beans.size()) {
            throw new CustomException("备件使用明细中存在未选择规格的行，或存在重复规格.");
        }
        boolean missingOperNumber = beans.stream().anyMatch(bean -> bean == null || StrUtil.isBlank(bean.getOperNumber())
            || CalculationUtil.compareTo(bean.getOperNumber(), "0", CommonNumConstants.NUM_TWO, RoundingMode.UP) <= 0);
        if (missingOperNumber) {
            throw new CustomException("请为每条明细填写有效的使用数量");
        }
    }

    private void validateUserStock(String userId, List<EquipmentSparePartUsageDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return;
        }
        List<String> normsIds = detailList.stream()
            .map(EquipmentSparePartUsageDetail::getNormsId)
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> userStockMap = iServiceUserStockService.queryUserStock(userId, normsIds);
        for (EquipmentSparePartUsageDetail detail : detailList) {
            Map<String, Object> stockMation = userStockMap.get(detail.getNormsId());
            if (ObjectUtil.isEmpty(stockMation) || stockMation.get("stock") == null) {
                throw new CustomException("部分配件库存不足，请重新选择配件！");
            }
            String stockStr = stockMation.get("stock").toString();
            if (CalculationUtil.compareTo(detail.getOperNumber(), stockStr, CommonNumConstants.NUM_TWO, RoundingMode.UP) > 0) {
                throw new CustomException("部分配件库存不足，请重新选择配件！");
            }
        }
    }

    private void checkRepairOrder(EquipmentRepairOrder repairOrder) {
        if (repairOrder == null) {
            throw new CustomException("维修单不存在");
        }
        if (!ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.BE_COMPLETED.getKey())) {
            throw new CustomException("只有待完工状态的维修单可以维护备件使用明细");
        }
        if (StrUtil.isEmpty(repairOrder.getServiceUserId())) {
            throw new CustomException("维修单尚未指定维修负责人，请先接单");
        }
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        if (!StrUtil.equals(currentUserId, repairOrder.getServiceUserId())) {
            throw new CustomException("只有维修负责人可以维护备件使用明细");
        }
    }

    private void changeUserStock(String stockUserId, List<EquipmentSparePartUsageDetail> detailList, int type) {
        if (CollectionUtil.isEmpty(detailList) || StrUtil.isEmpty(stockUserId)) {
            return;
        }
        detailList.forEach(detail -> iServiceUserStockService.editMaterialNormsUserStock(
            stockUserId,
            detail.getMaterialId(),
            detail.getNormsId(),
            detail.getOperNumber(),
            type));
    }

}
