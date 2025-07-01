/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.equipment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipment.dao.EquipmentDao;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.farm.service.FarmService;
import com.skyeye.whole.entity.WholeOrderOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EquipmentServiceImpl
 * @Description: 设备管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/17 21:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "设备管理", groupName = "设备管理")
public class EquipmentServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentDao, Equipment> implements EquipmentService {

    @Autowired
    private FarmService farmService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        farmService.setMationForMap(beans, "farmId", "farmMation");
        return beans;
    }

    @Override
    protected void validatorEntity(Equipment entity) {

    }

    @Override
    public Equipment selectById(String id) {
        Equipment equipment = super.selectById(id);
        farmService.setDataMation(equipment, Equipment::getFarmId);
        return equipment;
    }

    @Override
    public void queryAllEquipmentList(InputObject inputObject, OutputObject outputObject) {
        List<Equipment> equipmentList = list();
        outputObject.setBeans(equipmentList);
        outputObject.settotal(equipmentList.size());
    }

    @Override
    public void queryNoPageAllEquipmentList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        QueryWrapper<Equipment> queryWrapper = new QueryWrapper<>();
        //获取前三十天以内的日期
        String payMonth = DateUtil.getLastMonthDate();
        queryWrapper.like(MybatisPlusUtil.toColumns(WholeOrderOut::getCreateTime), payMonth);
        if (map.containsKey("tenantId") && StrUtil.isNotEmpty(map.get("tenantId").toString())) {
            queryWrapper.eq(CommonConstants.TENANT_ID, map.get("tenantId").toString());
        }
        List<Equipment> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    public String getBeforeOrFutureDay(int num) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, num);
        Date m = c.getTime();
        return format.format(m);
    }
}
