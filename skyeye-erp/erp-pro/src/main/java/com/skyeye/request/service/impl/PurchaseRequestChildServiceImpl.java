/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.request.dao.PurchaseRequestChildDao;
import com.skyeye.request.entity.PurchaseRequestChild;
import com.skyeye.request.service.PurchaseRequestChildService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: PurchaseRequestChildServiceImpl
 * @Description: 采购申请-子单据服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "采购申请-子单据", groupName = "采购申请", manageShow = false)
public class PurchaseRequestChildServiceImpl extends SkyeyeBusinessServiceImpl<PurchaseRequestChildDao, PurchaseRequestChild> implements PurchaseRequestChildService {

    @Override
    public void saveList(String parentId, List<PurchaseRequestChild> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (PurchaseRequestChild purchaseRequestChild : beans) {
                purchaseRequestChild.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<PurchaseRequestChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseRequestChild::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public List<PurchaseRequestChild> selectByParentId(String parentId) {
        QueryWrapper<PurchaseRequestChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PurchaseRequestChild::getParentId), parentId);
        List<PurchaseRequestChild> list = list(queryWrapper);
        return list;
    }

    @Override
    public String calcOrderAllTotalPrice(List<PurchaseRequestChild> purchaseRequestChildList) {
        String totalPrice = "0";
        for (PurchaseRequestChild purchaseRequestChild : purchaseRequestChildList) {
            // 计算子单据总价：单价 * 数量
            BigDecimal itemAllPrice = new BigDecimal(purchaseRequestChild.getUnitPrice());
            itemAllPrice = itemAllPrice.multiply(new BigDecimal(purchaseRequestChild.getOperNumber()));
            purchaseRequestChild.setAllPrice(itemAllPrice.toString());

            // 计算子单据价税合计：含税单价 * 数量
            BigDecimal taxUnitPrice = new BigDecimal(purchaseRequestChild.getTaxUnitPrice());
            taxUnitPrice = taxUnitPrice.multiply(new BigDecimal(purchaseRequestChild.getOperNumber()));
            purchaseRequestChild.setTaxLastMoney(taxUnitPrice.toString());
            totalPrice = CalculationUtil.add(totalPrice, purchaseRequestChild.getTaxLastMoney());
        }
        return totalPrice;
    }
}
