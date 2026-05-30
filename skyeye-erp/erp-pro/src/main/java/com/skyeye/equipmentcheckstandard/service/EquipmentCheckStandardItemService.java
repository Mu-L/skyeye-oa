package com.skyeye.equipmentcheckstandard.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.equipmentcheckstandard.entity.EquipmentCheckStandardItem;

import java.util.List;

/**
 * @ClassName: EquipmentCheckStandardItemService
 * @Description: 设备点检标准明细服务接口层
 */
public interface EquipmentCheckStandardItemService extends SkyeyeBusinessService<EquipmentCheckStandardItem> {

    void saveList(String parentId, List<EquipmentCheckStandardItem> beans);

    void deleteByParentId(String parentId);

    List<EquipmentCheckStandardItem> selectByParentId(String parentId);
}

