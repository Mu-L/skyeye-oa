package com.skyeye.equipmentcheck.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrderItem;

import java.util.List;

/**
 * @ClassName: EquipmentCheckOrderItemService
 * @Description: 设备点检单明细服务接口层
 */
public interface EquipmentCheckOrderItemService extends SkyeyeBusinessService<EquipmentCheckOrderItem> {

    void saveList(String parentId, List<EquipmentCheckOrderItem> beans);

    void deleteByParentId(String parentId);

    List<EquipmentCheckOrderItem> selectByParentId(String parentId);
}

