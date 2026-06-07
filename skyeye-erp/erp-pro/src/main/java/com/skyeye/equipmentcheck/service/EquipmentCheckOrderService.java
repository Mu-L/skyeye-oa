package com.skyeye.equipmentcheck.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrder;

/**
 * @ClassName: EquipmentCheckOrderService
 * @Description: 设备点检单服务接口层
 */
public interface EquipmentCheckOrderService extends SkyeyeBusinessService<EquipmentCheckOrder> {

    void queryStatistics(InputObject inputObject, OutputObject outputObject);
}

