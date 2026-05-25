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
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.farm.service.FarmService;
import com.skyeye.material.service.MaterialService;
import com.skyeye.repair.dao.EquipmentRepairOrderDao;
import com.skyeye.repair.entity.EquipmentRepairOrder;
import com.skyeye.repair.entity.EquipmentSparePartRequisition;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionDetailService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: EquipmentRepairOrderServiceImpl
 * @Description: 设备维修单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "设备维修单", groupName = "设备维修", flowable = true)
public class EquipmentRepairOrderServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentRepairOrderDao, EquipmentRepairOrder> implements EquipmentRepairOrderService {

    @Autowired
    private EquipmentSparePartRequisitionService equipmentSparePartRequisitionService;

    @Autowired
    private EquipmentSparePartRequisitionDetailService equipmentSparePartRequisitionDetailService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private FarmService farmService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public EquipmentRepairOrder getDataFromDb(String id) {
        EquipmentRepairOrder order = super.getDataFromDb(id);
        List<EquipmentSparePartRequisition> sparePartRequisitionList = equipmentSparePartRequisitionService.selectByPId(id);
        if (CollectionUtil.isNotEmpty(sparePartRequisitionList)) {
            sparePartRequisitionList.forEach(bean -> {
                List<EquipmentSparePartRequisitionDetail> detailList = equipmentSparePartRequisitionDetailService.selectByPId(bean.getId());
                bean.setDetailList(detailList);
            });
        }
        order.setSparePartRequisitionList(sparePartRequisitionList);
        return order;
    }

    @Override
    public EquipmentRepairOrder selectById(String id) {
        EquipmentRepairOrder order = super.selectById(id);
        if (order == null) {
            return null;
        }
        equipmentService.setDataMation(order, EquipmentRepairOrder::getEquipmentId);
        if (CollectionUtil.isEmpty(order.getSparePartRequisitionList())) {
            return order;
        }
        erpDepotService.setDataMation(order.getSparePartRequisitionList(), EquipmentSparePartRequisition::getDepotId);
        order.getSparePartRequisitionList().forEach(bean -> {
            if (CollectionUtil.isNotEmpty(bean.getDetailList())) {
                materialService.setDataMation(bean.getDetailList(), EquipmentSparePartRequisitionDetail::getMaterialId);
            }
        });
        List<String> staffIds = order.getSparePartRequisitionList().stream()
            .map(EquipmentSparePartRequisition::getStaffId)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        order.getSparePartRequisitionList().forEach(bean -> bean.setStaffMation(staffMap.get(bean.getStaffId())));
        return order;
    }

    @Override
    public void createPrepose(EquipmentRepairOrder entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        if (StrUtil.isEmpty(entity.getDispatchTime())) {
            entity.setDispatchTime(DateUtil.getTimeAndToString());
        }
    }

    @Override
    public void validatorEntity(EquipmentRepairOrder entity) {
        super.validatorEntity(entity);
        // 判断equipmentId是否为空，如果为空，则抛出异常
        if (StrUtil.isEmpty(entity.getEquipmentId())) {
            throw new CustomException("请选择设备");
        }
        // 判断equipmentId是否存在
        if (StrUtil.isNotEmpty(entity.getEquipmentId())) {
            Equipment equipment = equipmentService.selectById(entity.getEquipmentId());
            // 判断equipmentId是否为空，如果为空，则抛出异常
            if (equipment == null || equipment.getId() == null) {
                throw new CustomException("设备不存在: " + entity.getEquipmentId());
            }
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        equipmentService.setMationForMap(beans, "equipmentId", "equipmentMation");
        List<Map<String, Object>> equipmentMationList = beans.stream()
            .map(bean -> (Map<String, Object>) bean.get("equipmentMation"))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(equipmentMationList)) {
            farmService.setMationForMap(equipmentMationList, "farmId", "farmMation");
        }
        return beans;
    }

    @Override
    public QueryWrapper<EquipmentRepairOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentRepairOrder::getEquipmentStatus), commonPageInfo.getState());
        }
        return queryWrapper;
    }

    @Override
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();

        List<EquipmentRepairOrder> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

}
