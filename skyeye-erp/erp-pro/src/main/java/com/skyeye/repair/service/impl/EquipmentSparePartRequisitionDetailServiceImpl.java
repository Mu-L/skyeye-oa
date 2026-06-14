/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 备件领用明细
 */
@Service
@SkyeyeService(name = "备件领用明细", groupName = "设备维修", manageShow = false)
public class EquipmentSparePartRequisitionDetailServiceImpl extends SkyeyeLinkDataServiceImpl<EquipmentSparePartRequisitionDetailDao, EquipmentSparePartRequisitionDetail>
    implements EquipmentSparePartRequisitionDetailService {

    @Autowired
    private MaterialNormsService materialNormsService;

    @Override
    protected void checkLinkList(String pId, List<EquipmentSparePartRequisitionDetail> beans) {
        if (CollectionUtil.isEmpty(beans)) {
            return;
        }
        if (StrUtil.isBlank(pId)) {
            throw new CustomException("领用单ID不能为空.");
        }
        List<String> materialIds = beans.stream()
            .map(bean -> bean == null ? "" : String.format(Locale.ROOT, "%s", bean.getMaterialId()))
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.toList());
        if (materialIds.size() != beans.size()) {
            throw new CustomException("领用明细中存在未选择备件的行.");
        }
        List<String> checkIds = materialIds.stream().distinct().collect(Collectors.toList());
        if (checkIds.size() != materialIds.size()) {
            throw new CustomException("同一领用单中不允许重复选择同一备件");
        }
        boolean missingOperNumber = beans.stream().anyMatch(b -> b == null || b.getOperNumber() == null);
        if (missingOperNumber) {
            throw new CustomException("请为每条明细填写领用数量");
        }
        calcOrderAllTotalPrice(beans);
    }

    @Override
    public String calcOrderAllTotalPrice(List<EquipmentSparePartRequisitionDetail> detailList) {
        List<String> materialIds = detailList.stream()
            .map(EquipmentSparePartRequisitionDetail::getMaterialId)
            .collect(Collectors.toList());
        Map<String, List<MaterialNorms>> normsMap = materialNormsService.queryMaterialNormsList(StrUtil.EMPTY, materialIds.toArray(new String[]{}));
        String allPrice = CommonNumConstants.NUM_ZERO.toString();
        for (EquipmentSparePartRequisitionDetail detail : detailList) {
            // 子单据总价：领用数量 * 零售价
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
            // 计算主单总价
            allPrice = CalculationUtil.add(rowAllPrice, allPrice);
        }
        return allPrice;
    }

    @Override
    public List<EquipmentSparePartRequisitionDetail> selectByPIds(List<String> pIds) {
        if (CollectionUtil.isEmpty(pIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<EquipmentSparePartRequisitionDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(EquipmentSparePartRequisitionDetail::getParentId), pIds);
        return list(queryWrapper);
    }

}
