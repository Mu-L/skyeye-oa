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
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.equipment.service.EquipmentService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialService;
import com.skyeye.material.service.MaterialNormsService;
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
    private ErpDepotService erpDepotService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;


    @Override
    public EquipmentRepairOrder getDataFromDb(String id) {
        EquipmentRepairOrder order = super.getDataFromDb(id);
        List<EquipmentSparePartRequisition> sparePartRequisitionList = equipmentSparePartRequisitionService.selectByPId(id);
        if (CollectionUtil.isNotEmpty(sparePartRequisitionList)) {
            List<String> requisitionIds = sparePartRequisitionList.stream()
                    .map(EquipmentSparePartRequisition::getId)
                    .collect(java.util.stream.Collectors.toList());
            List<EquipmentSparePartRequisitionDetail> allDetails = equipmentSparePartRequisitionDetailService.selectByPIds(requisitionIds);
            java.util.Map<String, List<EquipmentSparePartRequisitionDetail>> detailsMap = allDetails.stream()
                    .collect(java.util.stream.Collectors.groupingBy(EquipmentSparePartRequisitionDetail::getParentId));
            sparePartRequisitionList.forEach(bean -> {
                List<EquipmentSparePartRequisitionDetail> detailList = detailsMap.getOrDefault(bean.getId(), new java.util.ArrayList<>());
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
        iAuthUserService.setDataMation(order, EquipmentRepairOrder::getUserId);
        if (StrUtil.isNotEmpty(order.getStaffId())) {
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(
                Collections.singletonList(order.getStaffId()));
            order.setStaffMation(staffMap.get(order.getStaffId()));
        }
        if (CollectionUtil.isEmpty(order.getSparePartRequisitionList())) {
            return order;
        }
        erpDepotService.setDataMation(order.getSparePartRequisitionList(), EquipmentSparePartRequisition::getDepotId);
        iAuthUserService.setDataMation(order.getSparePartRequisitionList(), EquipmentSparePartRequisition::getUserId);

        // 收集所有明细中的物料ID，去重后批量查询
        List<EquipmentSparePartRequisitionDetail> allDetailList = order.getSparePartRequisitionList().stream()
                .filter(bean -> CollectionUtil.isNotEmpty(bean.getDetailList()))
                .flatMap(bean -> bean.getDetailList().stream())
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(allDetailList)) {
            materialService.setDataMation(allDetailList, EquipmentSparePartRequisitionDetail::getMaterialId);
            materialNormsService.setDataMation(allDetailList, EquipmentSparePartRequisitionDetail::getNormsId);
        }
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
        iAuthUserService.setMationForMap(beans, "userId", "userMation");
        List<String> staffIds = beans.stream()
            .map(bean -> bean.get("staffId"))
            .filter(Objects::nonNull)
            .map(Object::toString)
            .filter(StrUtil::isNotEmpty)
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(staffIds)) {
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
            beans.forEach(bean -> {
                Object staffId = bean.get("staffId");
                if (staffId != null && StrUtil.isNotEmpty(staffId.toString())) {
                    bean.put("staffMation", staffMap.get(staffId.toString()));
                }
            });
        }
        return beans;
    }

    @Override
    public void queryAllEquipmentRepairOrderList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<EquipmentRepairOrder> queryWrapper = new QueryWrapper<>();
        List<EquipmentRepairOrder> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

}
