/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.conference.entity.ConferenceRoom;
import com.skyeye.eve.vehicle.classenum.VehicleState;
import com.skyeye.eve.vehicle.dao.VehicleDao;
import com.skyeye.eve.vehicle.entity.Vehicle;
import com.skyeye.eve.vehicle.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VehicleServiceImpl
 * @Description: 车辆管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "车辆管理", groupName = "车辆模块")
public class VehicleServiceImpl extends SkyeyeBusinessServiceImpl<VehicleDao, Vehicle> implements VehicleService {

    @Override
    public void createPrepose(Vehicle entity) {
        entity.setState(VehicleState.NORMAL.getKey());
    }

    @Override
    public Vehicle selectById(String id) {
        Vehicle vehicle = super.selectById(id);
        vehicle.setVehicleAdminMation(iAuthUserService.queryDataMationById(vehicle.getVehicleAdmin()));
        return vehicle;
    }

    /**
     * 车辆恢复正常
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void normalVehicleById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Vehicle vehicle = selectById(id);
        if (vehicle.getState().equals(VehicleState.REPAIR.getKey()) || vehicle.getState().equals(VehicleState.SCRAP.getKey())) {
            // 维修或者报废可以恢复正常
            UpdateWrapper<Vehicle> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(ConferenceRoom::getState), VehicleState.NORMAL.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    /**
     * 车辆维修
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void repairVehicleById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Vehicle vehicle = selectById(id);
        if (vehicle.getState().equals(VehicleState.NORMAL.getKey()) || vehicle.getState().equals(VehicleState.SCRAP.getKey())) {
            // 正常或者报废可以维修
            UpdateWrapper<Vehicle> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(ConferenceRoom::getState), VehicleState.REPAIR.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    /**
     * 车辆报废
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void scrapVehicleById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Vehicle vehicle = selectById(id);
        if (vehicle.getState().equals(VehicleState.NORMAL.getKey()) || vehicle.getState().equals(VehicleState.REPAIR.getKey())) {
            // 正常或者维修可以报废
            UpdateWrapper<Vehicle> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(ConferenceRoom::getState), VehicleState.SCRAP.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    @Override
    public void queryAllNormalVehicleList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<Vehicle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Vehicle::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Vehicle::getState), VehicleState.NORMAL.getKey());

        List<Vehicle> vehicleList = list(queryWrapper);
        outputObject.setBeans(vehicleList);
        outputObject.settotal(vehicleList.size());
    }

}
