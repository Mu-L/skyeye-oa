/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.repair.entity.EquipmentRepairOrder;

/**
 * 设备维修单服务接口层
 */
public interface EquipmentRepairOrderService extends SkyeyeBusinessService<EquipmentRepairOrder> {

    void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject);

    void insertEquipmentRepairOrder(InputObject inputObject, OutputObject outputObject);

    void editEquipmentRepairFaultReport(InputObject inputObject, OutputObject outputObject);

    void editEquipmentRepairAuditDispatch(InputObject inputObject, OutputObject outputObject);

    void editEquipmentRepairWaitToWorkMation(InputObject inputObject, OutputObject outputObject);

    void editEquipmentRepairResult(InputObject inputObject, OutputObject outputObject);

    void editEquipmentRepairEvaluate(InputObject inputObject, OutputObject outputObject);

    void editEquipmentRepairAcceptance(InputObject inputObject, OutputObject outputObject);

    void receivingEquipmentRepairOrderById(InputObject inputObject, OutputObject outputObject);

    void updateStateById(String id, Integer state);
}
