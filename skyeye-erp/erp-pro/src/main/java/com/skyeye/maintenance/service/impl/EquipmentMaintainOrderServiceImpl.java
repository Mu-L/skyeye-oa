/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.maintenance.entity.EquipmentMaintainOrder;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.maintenance.dao.EquipmentMaintainOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 设备保养单服务层
 * TODO: 保养单明细（EquipmentMaintainOrderItem）保存、查询、删除
 */
@Service
@SkyeyeService(name = "设备保养单", groupName = "设备保养", flowable = true)
public class EquipmentMaintainOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentMaintainOrderDao, EquipmentMaintainOrder>
    implements EquipmentMaintainOrderService {

    @Autowired
    private EquipmentService equipmentService;

    @Override
    public void validatorEntity(EquipmentMaintainOrder entity) {
        super.validatorEntity(entity);
        Equipment equipment = equipmentService.selectById(entity.getEquipmentId());
        if (equipment == null || StrUtil.isEmpty(equipment.getId())) {
            throw new CustomException("设备不存在: " + entity.getEquipmentId());
        }
    }

    @Override
    protected void validatorEntity(List<EquipmentMaintainOrder> entity) {
        super.validatorEntity(entity);
        Set<String> equipmentIds = entity.stream()
            .map(EquipmentMaintainOrder::getEquipmentId)
            .filter(StrUtil::isNotEmpty)
            .collect(Collectors.toSet());
        for (String equipmentId : equipmentIds) {
            Equipment equipment = equipmentService.selectById(equipmentId);
            if (equipment == null || StrUtil.isEmpty(equipment.getId())) {
                throw new CustomException("设备不存在: " + equipmentId);
            }
        }
    }

    @Override
    public void createPrepose(EquipmentMaintainOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    protected void createPrepose(List<EquipmentMaintainOrder> entity) {
        entity.forEach(this::createPrepose);
    }

}
