/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.repair.entity.EquipmentSparePartUsageDetail;

import java.util.List;

/**
 * 维修工单备件使用明细
 */
public interface EquipmentSparePartUsageDetailService extends SkyeyeLinkDataService<EquipmentSparePartUsageDetail> {

    void saveByRepairOrderId(String repairOrderId, List<EquipmentSparePartUsageDetail> detailList);

    void revertAndDeleteByRepairOrderId(String repairOrderId, String stockUserId);

    void calcDetailPrice(List<EquipmentSparePartUsageDetail> detailList);

}
