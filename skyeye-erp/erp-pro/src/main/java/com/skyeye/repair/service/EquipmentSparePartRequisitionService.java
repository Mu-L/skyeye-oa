/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.repair.entity.EquipmentSparePartRequisition;

import java.util.List;

/**
 * 备件领用单
 */
public interface EquipmentSparePartRequisitionService extends SkyeyeLinkDataService<EquipmentSparePartRequisition> {

    List<EquipmentSparePartRequisition> selectByPId(String pId);
}
