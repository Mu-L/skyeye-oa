/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.maintenance.dao.MaintenanceStandardDao;
import com.skyeye.maintenance.entity.EquipmentMaintainOrder;
import com.skyeye.maintenance.entity.MaintenancePlan;
import com.skyeye.maintenance.entity.MaintenanceStandard;
import com.skyeye.maintenance.service.EquipmentMaintainOrderService;
import com.skyeye.maintenance.service.MaintenancePlanService;
import com.skyeye.maintenance.service.MaintenanceStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 保养标准服务层
 */
@Service
@SkyeyeService(name = "保养标准", groupName = "设备保养")
public class MaintenanceStandardServiceImpl extends SkyeyeBusinessServiceImpl<MaintenanceStandardDao, MaintenanceStandard>
    implements MaintenanceStandardService {

    @Autowired
    @Lazy
    private MaintenancePlanService maintenancePlanService;

    @Autowired
    @Lazy
    private EquipmentMaintainOrderService equipmentMaintainOrderService;

    @Override
    public void deletePreExecution(String id) {
        QueryWrapper<MaintenancePlan> planQueryWrapper = new QueryWrapper<>();
        planQueryWrapper.eq(MybatisPlusUtil.toColumns(MaintenancePlan::getMaintenanceStandardId), id);
        if (maintenancePlanService.count(planQueryWrapper) > 0) {
            throw new CustomException("该保养标准已被保养计划引用，无法删除.");
        }
        QueryWrapper<EquipmentMaintainOrder> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentMaintainOrder::getMaintenanceStandardId), id);
        if (equipmentMaintainOrderService.count(orderQueryWrapper) > 0) {
            throw new CustomException("该保养标准已被设备保养单引用，无法删除.");
        }
    }

    @Override
    public void queryAllMaintenanceStandardList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<MaintenanceStandard> queryWrapper = new QueryWrapper<>();
        List<MaintenanceStandard> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

}
