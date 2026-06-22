/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.repair.classenum.EquipmentRepairOrderState;
import com.skyeye.repair.dao.EquipmentSparePartRequisitionDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentSparePartRequisition;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionDetailService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionService;
import com.skyeye.rest.sealservice.service.IServiceUserStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 维修工单备件领用单（关联设备维修单，参考售后工单故障配件模式）
 */
@Service
@SkyeyeService(name = "维修工单备件领用单", groupName = "设备维修")
public class EquipmentSparePartRequisitionServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartRequisitionDao, EquipmentSparePartRequisition>
    implements EquipmentSparePartRequisitionService {

    @Autowired
    private EquipmentSparePartRequisitionDetailService equipmentSparePartRequisitionDetailService;

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private IServiceUserStockService iServiceUserStockService;

    @Override
    public QueryWrapper<EquipmentSparePartRequisition> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentSparePartRequisition> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentSparePartRequisition::getParentId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public void validatorEntity(EquipmentSparePartRequisition entity) {
        if (CollectionUtil.isEmpty(entity.getDetailList())) {
            throw new CustomException("请至少填写一条领用明细");
        }
        String allPrice = equipmentSparePartRequisitionDetailService.calcOrderAllTotalPrice(entity.getDetailList());
        entity.setTotalAmount(new BigDecimal(allPrice));
    }

    @Override
    public void createPrepose(EquipmentSparePartRequisition entity) {
        check(entity);
        entity.setParentId(entity.getSourceOrderId());
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public void updatePrepose(EquipmentSparePartRequisition entity) {
        check(entity);
        entity.setParentId(entity.getSourceOrderId());
        revertUserStock(entity.getId());
    }

    @Override
    public void writePostpose(EquipmentSparePartRequisition entity, String userId) {
        if (CollectionUtil.isNotEmpty(entity.getDetailList())) {
            equipmentSparePartRequisitionDetailService.saveLinkList(entity.getId(), entity.getDetailList());
            changeUserStock(entity, IServiceUserStockService.USER_STOCK_OUT);
        }
        super.writePostpose(entity, userId);
    }

    @Override
    public EquipmentSparePartRequisition getDataFromDb(String id) {
        EquipmentSparePartRequisition bean = super.getDataFromDb(id);
        bean.setDetailList(equipmentSparePartRequisitionDetailService.selectByPId(bean.getId()));
        return bean;
    }

    @Override
    public EquipmentSparePartRequisition selectById(String id) {
        EquipmentSparePartRequisition entity = super.selectById(id);
        if (entity == null) {
            return null;
        }
        entity.setSourceOrderMation(BeanUtil.beanToMap(equipmentRepairOrderService.selectById(entity.getSourceOrderId())));
        if (entity.getSourceOrderMation() != null) {
            entity.getSourceOrderMation().remove("sparePartRequisitionList");
        }
        erpDepotService.setDataMation(entity, EquipmentSparePartRequisition::getDepotId);
        iAuthUserService.setDataMation(entity, EquipmentSparePartRequisition::getUserId);
        if (CollectionUtil.isNotEmpty(entity.getDetailList())) {
            materialService.setDataMation(entity.getDetailList(), EquipmentSparePartRequisitionDetail::getMaterialId);
            materialNormsService.setDataMation(entity.getDetailList(), EquipmentSparePartRequisitionDetail::getNormsId);
        }
        return entity;
    }

    @Override
    public void deletePreExecution(EquipmentSparePartRequisition entity) {
        revertUserStock(entity.getId());
        equipmentSparePartRequisitionDetailService.deleteByPId(entity.getId());
    }

    @Override
    public List<EquipmentSparePartRequisition> selectByPId(String pId) {
        List<EquipmentSparePartRequisition> requisitionList = super.selectByPId(pId);
        if (CollectionUtil.isEmpty(requisitionList)) {
            return requisitionList;
        }
        List<String> requisitionIds = requisitionList.stream()
            .map(EquipmentSparePartRequisition::getId)
            .collect(Collectors.toList());
        List<EquipmentSparePartRequisitionDetail> allDetails = equipmentSparePartRequisitionDetailService.selectByPIds(requisitionIds);
        Map<String, List<EquipmentSparePartRequisitionDetail>> detailsMap = allDetails.stream()
            .collect(Collectors.groupingBy(EquipmentSparePartRequisitionDetail::getParentId));
        requisitionList.forEach(bean -> bean.setDetailList(detailsMap.getOrDefault(bean.getId(), new ArrayList<>())));
        return requisitionList;
    }

    private void check(EquipmentSparePartRequisition entity) {
        EquipmentRepairOrder repairOrder = equipmentRepairOrderService.selectById(entity.getSourceOrderId());
        if (repairOrder == null) {
            throw new CustomException("关联的维修单不存在");
        }
        if (!ObjectUtil.equal(repairOrder.getState(), EquipmentRepairOrderState.BE_COMPLETED.getKey())) {
            throw new CustomException("只有待完工状态的维修单可以维护备件更换");
        }
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        String repairServiceUserId = resolveRepairServiceUserId(repairOrder);
        if (!StrUtil.equals(currentUserId, repairServiceUserId)) {
            throw new CustomException("只有维修负责人可以维护备件领用单");
        }
        entity.setUserId(repairServiceUserId);
        if (CollectionUtil.isNotEmpty(entity.getDetailList())) {
            List<String> normsIds = entity.getDetailList().stream()
                .map(EquipmentSparePartRequisitionDetail::getNormsId)
                .distinct()
                .collect(Collectors.toList());
            if (entity.getDetailList().size() != normsIds.size()) {
                throw new CustomException("单据中不允许存在重复的产品规格信息");
            }
            equipmentSparePartRequisitionDetailService.validateUserStock(entity.getUserId(), entity.getDetailList());
        }
    }

    private String resolveRepairServiceUserId(EquipmentRepairOrder repairOrder) {
        if (StrUtil.isNotEmpty(repairOrder.getServiceUserId())) {
            return repairOrder.getServiceUserId();
        }
        throw new CustomException("维修单尚未指定维修负责人，请先接单");
    }

    private void changeUserStock(EquipmentSparePartRequisition entity, int type) {
        if (CollectionUtil.isEmpty(entity.getDetailList())) {
            return;
        }
        String stockUserId = entity.getUserId();
        if (StrUtil.isEmpty(stockUserId)) {
            EquipmentRepairOrder repairOrder = equipmentRepairOrderService.selectById(entity.getSourceOrderId());
            if (repairOrder == null) {
                throw new CustomException("关联的维修单不存在");
            }
            stockUserId = resolveRepairServiceUserId(repairOrder);
        }
        String finalStockUserId = stockUserId;
        entity.getDetailList().forEach(detail -> iServiceUserStockService.editMaterialNormsUserStock(
            finalStockUserId,
            detail.getMaterialId(),
            detail.getNormsId(),
            String.valueOf(detail.getOperNumber()),
            type));
    }

    private void revertUserStock(String requisitionId) {
        EquipmentSparePartRequisition dbRequisition = getDataFromDb(requisitionId);
        if (dbRequisition == null || CollectionUtil.isEmpty(dbRequisition.getDetailList())) {
            return;
        }
        changeUserStock(dbRequisition, IServiceUserStockService.USER_STOCK_PUT);
    }

}
