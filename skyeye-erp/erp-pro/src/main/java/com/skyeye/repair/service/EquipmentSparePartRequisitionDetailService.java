/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;

import java.util.List;

/**
 * 备件领用明细
 */
public interface EquipmentSparePartRequisitionDetailService extends SkyeyeBusinessService<EquipmentSparePartRequisitionDetail> {

    void saveList(String parentId, List<EquipmentSparePartRequisitionDetail> beans);

    void deleteByParentId(String parentId);

    List<EquipmentSparePartRequisitionDetail> selectByParentId(String parentId);

    String calcOrderAllTotalPrice(List<EquipmentSparePartRequisitionDetail> detailList);
}
