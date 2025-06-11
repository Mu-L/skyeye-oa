/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.eve.vehicle.dao.AccidentDao;
import com.skyeye.eve.vehicle.entity.Accident;
import com.skyeye.eve.vehicle.service.AccidentService;
import com.skyeye.eve.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AccidentServiceImpl
 * @Description: 车辆事故管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/17 21:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车辆事故管理", groupName = "车辆模块")
public class AccidentServiceImpl extends SkyeyeBusinessServiceImpl<AccidentDao, Accident> implements AccidentService {

    @Autowired
    private VehicleService vehicleService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        vehicleService.setMationForMap(beans, "vehicleId", "vehicleMation");
        // 设置驾驶员信息
        iAuthUserService.setMationForMap(beans, "driverId", "driverMation");
        return beans;
    }

    @Override
    public Accident selectById(String id) {
        Accident vehicleAccident = super.selectById(id);
        // 车辆信息
        vehicleService.setDataMation(vehicleAccident, Accident::getVehicleId);
        // 驾驶员信息
        iAuthUserService.setDataMation(vehicleAccident, Accident::getDriverId);
        return vehicleAccident;
    }

}
