/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.vehicle.entity.VehicleDrivers;

/**
 * @ClassName: VehicleDriversService
 * @Description: 车辆驾驶员信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/22 10:41
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface VehicleDriversService extends SkyeyeBusinessService<VehicleDrivers> {

    void queryAllVehicleDriversList(InputObject inputObject, OutputObject outputObject);

}
