/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.repair.entity.EquipmentScrapOrder;

/**
 * @ClassName: EquipmentScrapOrderService
 * @Description: 设备报废单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface EquipmentScrapOrderService extends SkyeyeBusinessService<EquipmentScrapOrder> {

    void queryAllEquipmentScrapOrderList(InputObject inputObject, OutputObject outputObject);

}
