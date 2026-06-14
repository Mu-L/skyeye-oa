/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.repair.dao.EquipmentSparePartRequisitionDao;
import com.skyeye.repair.entity.EquipmentSparePartRequisition;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionDetailService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 备件领用单
 */
@Service
@SkyeyeService(name = "备件领用单", groupName = "设备维修")
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

    @Override
    public void saveLinkList(String pId, List<EquipmentSparePartRequisition> beans) {
        if (beans == null) {
            beans = new ArrayList<>();
        }
        if (CollectionUtil.isNotEmpty(beans)) {
            beans.forEach(bean -> {
                bean.setRepairOrderId(pId);
                if (StrUtil.isEmpty(bean.getOddNumber())) {
                    bean.setOddNumber(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), BeanUtil.beanToMap(bean)));
                }
                if (CollectionUtil.isNotEmpty(bean.getDetailList())) {
                    String allPrice = equipmentSparePartRequisitionDetailService.calcOrderAllTotalPrice(bean.getDetailList());
                    bean.setTotalAmount(new BigDecimal(allPrice));
                }
            });
        }
        super.saveLinkList(pId, beans);
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        for (EquipmentSparePartRequisition bean : beans) {
            if (CollectionUtil.isNotEmpty(bean.getDetailList())) {
                equipmentSparePartRequisitionDetailService.saveLinkList(bean.getId(), bean.getDetailList());
            }
        }
    }

    @Override
    public void deleteByPId(String pId) {
        List<EquipmentSparePartRequisition> requisitionList = selectByPId(pId);
        if (CollectionUtil.isNotEmpty(requisitionList)) {
            List<String> requisitionIds = requisitionList.stream()
                .map(EquipmentSparePartRequisition::getId)
                .collect(Collectors.toList());
            QueryWrapper<EquipmentSparePartRequisitionDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentSparePartRequisitionDetail::getParentId), requisitionIds);
            equipmentSparePartRequisitionDetailService.remove(queryWrapper);
        }
        super.deleteByPId(pId);
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
    public void writePostpose(EquipmentSparePartRequisition entity, String userId) {
        equipmentSparePartRequisitionDetailService.saveLinkList(entity.getId(), entity.getDetailList());
        super.writePostpose(entity, userId);
    }

    @Override
    public EquipmentSparePartRequisition getDataFromDb(String id) {
        EquipmentSparePartRequisition bean = super.getDataFromDb(id);
        bean.setDetailList(equipmentSparePartRequisitionDetailService.selectByPId(bean.getId()));
        return bean;
    }

    @Override
    public void createPrepose(EquipmentSparePartRequisition entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public EquipmentSparePartRequisition selectById(String id) {
        EquipmentSparePartRequisition entity = super.selectById(id);
        if (entity == null) {
            return null;
        }
        if (StrUtil.isNotBlank(entity.getRepairOrderId())) {
            equipmentRepairOrderService.setDataMation(entity, EquipmentSparePartRequisition::getRepairOrderId);
            if (entity.getRepairOrderMation() != null) {
                entity.getRepairOrderMation().setSparePartRequisitionList(null);
            }
        }
        erpDepotService.setDataMation(entity, EquipmentSparePartRequisition::getDepotId);
        iAuthUserService.setDataMation(entity, EquipmentSparePartRequisition::getUserId);
        materialService.setDataMation(entity.getDetailList(), EquipmentSparePartRequisitionDetail::getMaterialId);
        materialNormsService.setDataMation(entity.getDetailList(), EquipmentSparePartRequisitionDetail::getNormsId);
        return entity;
    }

    @Override
    public void deletePreExecution(EquipmentSparePartRequisition entity) {
        equipmentSparePartRequisitionDetailService.deleteByPId(entity.getId());
    }

}
