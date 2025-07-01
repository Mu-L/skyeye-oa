/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.vehicle.dao.OilingDao;
import com.skyeye.eve.vehicle.entity.Oiling;
import com.skyeye.eve.vehicle.service.OilingService;
import com.skyeye.eve.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: OilingServiceImpl
 * @Description: 车辆加油服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "车辆加油管理", groupName = "车辆模块")
public class OilingServiceImpl extends SkyeyeBusinessServiceImpl<OilingDao, Oiling> implements OilingService {

    @Autowired
    private VehicleService vehicleService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        vehicleService.setMationForMap(beans, "vehicleId", "vehicleMation");
        return beans;
    }

    @Override
    public Oiling selectById(String id) {
        Oiling oiling = super.selectById(id);
        // 车辆信息
        vehicleService.setDataMation(oiling, Oiling::getVehicleId);
        return oiling;
    }

    @Override
    public void queryNoPageOilingList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        QueryWrapper<Oiling> queryWrapper = new QueryWrapper<>();
        //获取前三十天以内的日期
        String payMonth = DateUtil.getLastMonthDate();
        queryWrapper.like(MybatisPlusUtil.toColumns(Oiling::getCreateTime), payMonth);
        if (map.containsKey("tenantId") && StrUtil.isNotEmpty(map.get("tenantId").toString())) {
            queryWrapper.eq(CommonConstants.TENANT_ID, map.get("tenantId").toString());
        }
        List<Oiling> list = list(queryWrapper);
        List<Map<String, Object>> result = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        vehicleService.setMationForMap(result, "vehicleId", "vehicleMation");
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
