/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.repair.dao.EquipmentScrapOrderDao;
import com.skyeye.repair.entity.EquipmentScrapOrder;
import com.skyeye.repair.service.EquipmentScrapOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentScrapOrderServiceImpl
 * @Description: 设备报废单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "设备报废单", groupName = "设备维修", flowable = true)
public class EquipmentScrapOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentScrapOrderDao, EquipmentScrapOrder> implements EquipmentScrapOrderService {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public EquipmentScrapOrder selectById(String id) {
        EquipmentScrapOrder entity = super.selectById(id);
        if (entity == null) {
            return null;
        }
        equipmentService.setDataMation(entity, EquipmentScrapOrder::getEquipmentId);
        if (StrUtil.isNotEmpty(entity.getStaffId())) {
            List<String> staffIds = new ArrayList<>();
            staffIds.add(entity.getStaffId());
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
            entity.setStaffMation(staffMap.get(entity.getStaffId()));
        }
        return entity;
    }

    @Override
    public void createPrepose(EquipmentScrapOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        if (StrUtil.isEmpty(entity.getApplicationDate())) {
            entity.setApplicationDate(DateUtil.getYmdTimeAndToString());
        }
    }

    @Override
    public void validatorEntity(EquipmentScrapOrder entity) {
        super.validatorEntity(entity);
        if (StrUtil.isEmpty(entity.getEquipmentId())) {
            throw new CustomException("请选择设备");
        }
        Equipment equipment = equipmentService.selectById(entity.getEquipmentId());
        if (equipment == null || StrUtil.isEmpty(equipment.getId())) {
            throw new CustomException("设备不存在: " + entity.getEquipmentId());
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString())
            .filter(StrUtil::isNotEmpty).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> bean.put("staffMation", staffMap.get(bean.get("staffId").toString())));
        return beans;
    }

    @Override
    public void queryAllEquipmentScrapOrderList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<EquipmentScrapOrder> queryWrapper = new QueryWrapper<>();
        List<EquipmentScrapOrder> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
