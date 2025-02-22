/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.vehicle.dao.VehicleDriversDao;
import com.skyeye.eve.vehicle.entity.VehicleDrivers;
import com.skyeye.eve.vehicle.service.VehicleDriversService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: VehicleDriversServiceImpl
 * @Description: 车辆驾驶员信息服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/22 10:42
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车辆驾驶员", groupName = "车辆模块")
public class VehicleDriversServiceImpl extends SkyeyeBusinessServiceImpl<VehicleDriversDao, VehicleDrivers> implements VehicleDriversService {

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setMationForMap(beans, "userId", "userMation");
        return beans;
    }

    @Override
    public void validatorEntity(VehicleDrivers entity) {
        super.validatorEntity(entity);
        QueryWrapper<VehicleDrivers> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VehicleDrivers::getUserId), entity.getUserId());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        VehicleDrivers one = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(one)) {
            throw new CustomException("该账号已为驾驶员账号.");
        }
    }

    @Override
    public VehicleDrivers selectById(String id) {
        VehicleDrivers vehicleDrivers = super.selectById(id);
        iAuthUserService.setDataMation(vehicleDrivers, VehicleDrivers::getUserId);
        if (CollectionUtil.isNotEmpty(vehicleDrivers.getUserMation())) {
            vehicleDrivers.setName(vehicleDrivers.getUserMation().get("name").toString());
        }
        return vehicleDrivers;
    }

    @Override
    public void queryAllVehicleDriversList(InputObject inputObject, OutputObject outputObject) {
        List<VehicleDrivers> vehicleDriversList = list();
        List<String> userIds = vehicleDriversList.stream().map(VehicleDrivers::getUserId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userIds)) {
            return;
        }
        String ids = Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds);
        List<Map<String, Object>> userList = iAuthUserService.queryDataMationByIds(ids);
        outputObject.setBeans(userList);
        outputObject.settotal(userList.size());
    }
}
