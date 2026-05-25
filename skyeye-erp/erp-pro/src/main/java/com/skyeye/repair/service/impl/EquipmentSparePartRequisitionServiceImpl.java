/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.material.service.MaterialService;
import com.skyeye.repair.dao.EquipmentSparePartRequisitionDao;
import com.skyeye.repair.entity.EquipmentSparePartRequisition;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.service.EquipmentRepairOrderService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionDetailService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 备件领用单：与售后工单、巡检任务等模块一致，主表关联用 {@code setDataMation} / 列表用 {@code setMationForMap}；明细单价来自 ERP 物料。
 */
@Service
@SkyeyeService(name = "备件领用单", groupName = "设备维修", flowable = true)
public class EquipmentSparePartRequisitionServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartRequisitionDao, EquipmentSparePartRequisition>
    implements EquipmentSparePartRequisitionService {

    @Autowired
    private EquipmentSparePartRequisitionDetailService equipmentSparePartRequisitionDetailService;

    @Autowired
    private EquipmentRepairOrderService equipmentRepairOrderService;

    @Autowired
    private ErpDepotService erpDepotService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private MaterialService materialService;

    @Override
    public void validatorEntity(EquipmentSparePartRequisition entity) {
        if (CollectionUtil.isEmpty(entity.getDetailList())) {
            throw new CustomException("请至少填写一条领用明细");
        }
        for (EquipmentSparePartRequisitionDetail row : entity.getDetailList()) {
            // 判断备件明细表id是否为空，如果为空，则抛出异常
            if (StrUtil.isBlank(row.getMaterialId())) {
                throw new CustomException("请选择备件明细");
            }
            // 判断备件明细是否存在
            if (StrUtil.isNotBlank(row.getMaterialId())) {
                if (materialService.selectById(row.getMaterialId()) == null) {
                    throw new CustomException("备件明细不存在: " + row.getMaterialId());
                }
            }
        }
        String allPrice = equipmentSparePartRequisitionDetailService.calcOrderAllTotalPrice(entity.getDetailList());
        entity.setTotalAmount(new BigDecimal(allPrice));
    }

    @Override
    public void writePostpose(EquipmentSparePartRequisition entity, String userId) {
        equipmentSparePartRequisitionDetailService.saveLinkList(entity.getId(), entity.getDetailList());
        super.writePostpose(entity, userId);
    }

    @Override
    public EquipmentSparePartRequisition getDataFromDb(String id) {
        EquipmentSparePartRequisition bean = super.getDataFromDb(id);
        List<EquipmentSparePartRequisitionDetail> details = equipmentSparePartRequisitionDetailService.selectByPId(bean.getId());
        bean.setDetailList(details);
        return bean;
    }

    @Override
    public EquipmentSparePartRequisition selectById(String id) {
        EquipmentSparePartRequisition entity = super.selectById(id);
        if (entity == null) {
            return null;
        }
        if (StrUtil.isNotBlank(entity.getRepairOrderId())) {
            equipmentRepairOrderService.setDataMation(entity, EquipmentSparePartRequisition::getRepairOrderId);
            if (entity.getRepairOrderMation() != null) {
                // 详情页不需要在维修单信息内再次嵌套备件领用单列表，避免前端出现重复列表结构。
                entity.getRepairOrderMation().setSparePartRequisitionList(null);
            }
        }
        erpDepotService.setDataMation(entity, EquipmentSparePartRequisition::getDepotId);
        if (StrUtil.isNotEmpty(entity.getStaffId())) {
            List<String> staffIds = new ArrayList<>();
            staffIds.add(entity.getStaffId());
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
            entity.setStaffMation(staffMap.get(entity.getStaffId()));
        }
        materialService.setDataMation(entity.getDetailList(), EquipmentSparePartRequisitionDetail::getMaterialId);
        return entity;
    }

    @Override
    public void deletePreExecution(EquipmentSparePartRequisition entity) {
        equipmentSparePartRequisitionDetailService.deleteByPId(entity.getId());
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        erpDepotService.setMationForMap(beans, "depotId", "depotMation");
        // 设置员工信息
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString())
            .filter(staffId -> StrUtil.isNotEmpty(staffId)).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String staffId = bean.get("staffId").toString();
            Map<String, Object> staffMation = staffMap.get(staffId);
            bean.put("staffMation", staffMation);
        });
        return beans;
    }

    @Override
    public QueryWrapper<EquipmentSparePartRequisition> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentSparePartRequisition> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentSparePartRequisition::getState), commonPageInfo.getState());
        }
        return queryWrapper;
    }

}
