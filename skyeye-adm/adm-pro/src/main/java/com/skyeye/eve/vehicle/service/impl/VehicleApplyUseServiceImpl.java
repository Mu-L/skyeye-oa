/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.vehicle.dao.VehicleApplyUseDao;
import com.skyeye.eve.vehicle.entity.VehicleUse;
import com.skyeye.eve.vehicle.service.VehicleApplyUseService;
import com.skyeye.eve.vehicle.service.VehicleService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VehicleApplyUseServiceImpl
 * @Description: 用车申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:49
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用车申请", groupName = "车辆模块", flowable = true)
public class VehicleApplyUseServiceImpl extends SkyeyeFlowableServiceImpl<VehicleApplyUseDao, VehicleUse> implements VehicleApplyUseService {

    @Autowired
    private VehicleService vehicleService;

    @Override
    public void validatorEntity(VehicleUse entity) {
        super.validatorEntity(entity);
        if (DateUtil.compare(entity.getReturnTime(), entity.getDepartureTime())) {
            // 返回时间不能小于出发时间
            throw new CustomException("返回时间不能小于出发时间");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        vehicleService.setMationForMap(beans, "vehicleId", "vehicleMation");
        // 设置驾驶员信息
        iAuthUserService.setMationForMap(beans, "driverId", "driverMation");
        return beans;
    }

    @Override
    protected QueryWrapper<VehicleUse> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<VehicleUse> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VehicleUse::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public VehicleUse selectById(String id) {
        VehicleUse vehicleUse = super.selectById(id);
        // 车辆信息
        vehicleService.setDataMation(vehicleUse, VehicleUse::getVehicleId);
        // 驾驶员信息
        iAuthUserService.setDataMation(vehicleUse, VehicleUse::getDriverId);
        return vehicleUse;
    }

}
