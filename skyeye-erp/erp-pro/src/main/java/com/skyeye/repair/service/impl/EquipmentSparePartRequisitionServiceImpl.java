/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.repair.dao.EquipmentSparePartRequisitionDao;
import com.skyeye.repair.entity.EquipmentSparePartRequisition;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.service.EquipmentSparePartRequisitionDetailService;
import com.skyeye.repair.service.EquipmentSparePartRequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "备件领用单", groupName = "设备维修", manageShow = false)
public class EquipmentSparePartRequisitionServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartRequisitionDao, EquipmentSparePartRequisition>
        implements EquipmentSparePartRequisitionService {

    @Autowired
    private EquipmentSparePartRequisitionDetailService equipmentSparePartRequisitionDetailService;

    @Override
    public void saveLinkList(String pId, List<EquipmentSparePartRequisition> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            deleteByPId(pId);
            return;
        }
        super.saveLinkList(pId, beans);
        List<EquipmentSparePartRequisitionDetail> detailList = new ArrayList<>();
        beans.forEach(bean -> {
            if (CollectionUtil.isNotEmpty(bean.getDetailList())) {
                bean.getDetailList().forEach(detail -> {
                    detail.setRequisitionId(bean.getId());
                    detailList.add(detail);
                });
            }
        });
        if (CollectionUtil.isNotEmpty(detailList)) {
            equipmentSparePartRequisitionDetailService.saveList(pId, detailList);
        }
    }

    @Override
    public void deleteByPId(String pId) {
        super.deleteByPId(pId);
        equipmentSparePartRequisitionDetailService.deleteByParentId(pId);
    }

    @Override
    public List<EquipmentSparePartRequisition> selectByPId(String pId) {
        List<EquipmentSparePartRequisition> requisitionList = super.selectByPId(pId);
        List<EquipmentSparePartRequisitionDetail> detailList = equipmentSparePartRequisitionDetailService.selectByParentId(pId);
        if (CollectionUtil.isNotEmpty(detailList)) {
            Map<String, List<EquipmentSparePartRequisitionDetail>> collect = detailList.stream()
                    .collect(Collectors.groupingBy(EquipmentSparePartRequisitionDetail::getRequisitionId));
            requisitionList.forEach(bean -> {
                List<EquipmentSparePartRequisitionDetail> details = collect.get(bean.getId());
                if (CollectionUtil.isNotEmpty(details)) {
                    bean.setDetailList(details);
                }
            });
        }
        return requisitionList;
    }

}