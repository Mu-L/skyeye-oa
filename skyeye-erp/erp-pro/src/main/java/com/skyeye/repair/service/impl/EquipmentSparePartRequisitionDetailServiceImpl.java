/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.repair.dao.EquipmentSparePartRequisitionDetailDao;
import com.skyeye.repair.entity.EquipmentSparePartRequisitionDetail;
import com.skyeye.repair.service.EquipmentSparePartRequisitionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentSparePartRequisitionDetailServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentSparePartRequisitionDetailDao, EquipmentSparePartRequisitionDetail>
        implements EquipmentSparePartRequisitionDetailService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Override
    public void saveList(String parentId, List<EquipmentSparePartRequisitionDetail> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (EquipmentSparePartRequisitionDetail detail : beans) {
                detail.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<EquipmentSparePartRequisitionDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentSparePartRequisitionDetail::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<EquipmentSparePartRequisitionDetail> selectByParentId(String parentId) {
        QueryWrapper<EquipmentSparePartRequisitionDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentSparePartRequisitionDetail::getParentId), parentId);
        return list(queryWrapper);
    }

    @Override
    public String calcOrderAllTotalPrice(List<EquipmentSparePartRequisitionDetail> detailList) {
        List<String> materialIds = detailList.stream()
                .map(EquipmentSparePartRequisitionDetail::getMaterialId)
                .collect(Collectors.toList());
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        String allPrice = CommonNumConstants.NUM_ZERO.toString();
        for (EquipmentSparePartRequisitionDetail detail : detailList) {
            List<MaterialNorms> normsList = normsMap.get(detail.getMaterialId());
            if (CollectionUtil.isEmpty(normsList)) {
                throw new CustomException("数据中包含不存在的备件规格信息.");
            }
            MaterialNorms firstNorms = normsList.stream()
                    .filter(norms -> StrUtil.isNotBlank(norms.getRetailPrice()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException("备件规格未维护零售价."));
            String unitPrice = firstNorms.getRetailPrice();
            String rowAllPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(detail.getOperNumber()), unitPrice);
            detail.setUnitPrice(new BigDecimal(unitPrice));
            detail.setAllPrice(new BigDecimal(rowAllPrice));
            allPrice = CalculationUtil.add(rowAllPrice, allPrice);
        }
        return allPrice;
    }

}
