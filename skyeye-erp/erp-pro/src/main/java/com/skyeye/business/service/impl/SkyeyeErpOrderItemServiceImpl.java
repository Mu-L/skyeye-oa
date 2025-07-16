/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.business.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.business.service.SkyeyeErpOrderItemService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dao.ErpOrderItemDao;
import com.skyeye.depot.classenum.DepotIdKeyEnum;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.entity.TransmitObject;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName: SkyeyeErpOrderItemServiceImpl
 * @Description: ERP单据关联的商品的service服务
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/24 20:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SkyeyeErpOrderItemServiceImpl extends SkyeyeLinkDataServiceImpl<ErpOrderItemDao, ErpOrderItem> implements SkyeyeErpOrderItemService {

    @Override
    protected void checkLinkList(String pId, List<ErpOrderItem> beans) {
        beans.forEach(erpOrderItem -> {
            if (StrUtil.isNotEmpty(erpOrderItem.getDepotId())
                    && StrUtil.isNotEmpty(erpOrderItem.getAnotherDepotId())) {
                if (StrUtil.equals(erpOrderItem.getDepotId(), erpOrderItem.getAnotherDepotId())) {
                    throw new CustomException("出入库仓库不能一样。");
                }
            }
        });
    }

    /**
     * 计算单据信息的总价
     *
     * @param object
     * @param erpOrderItemList
     * @return
     */
    @Override
    public List<ErpOrderItem> calcOrderAllTotalPrice(TransmitObject object, List<ErpOrderItem> erpOrderItemList) {
        erpOrderItemList.forEach(erpOrderItem -> {
            // 计算子单据总价：单价 * 数量
            BigDecimal itemAllPrice = new BigDecimal(erpOrderItem.getUnitPrice());
            itemAllPrice = itemAllPrice.multiply(new BigDecimal(erpOrderItem.getOperNumber()));
            erpOrderItem.setAllPrice(itemAllPrice.toString());

            // 计算子单据价税合计：含税单价 * 数量
            BigDecimal taxUnitPrice = new BigDecimal(erpOrderItem.getTaxUnitPrice());
            taxUnitPrice = taxUnitPrice.multiply(new BigDecimal(erpOrderItem.getOperNumber()));
            erpOrderItem.setTaxLastMoney(taxUnitPrice.toString());
            // 计算主单总价
            object.setTaxLastMoneyPrice(CalculationUtil.add(object.getTaxLastMoneyPrice(), erpOrderItem.getTaxLastMoney()));
        });
        return erpOrderItemList;
    }

    @Override
    public List<ErpOrderItem> queryErpOrderItemByPIds(List<String> pIds) {
        QueryWrapper<ErpOrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderItem::getParentId), pIds);
        return list(queryWrapper);
    }

    @Override
    @IgnoreTenant
    public List<ErpOrderItem> queryHolderOutPutNormsList(String holderKey,String type) {
        MPJLambdaWrapper<ErpOrderItem> mpjLambdaWrapper = JoinWrappers.lambda("i", ErpOrderItem.class)
                .innerJoin(DepotPut.class, "t", DepotPut::getId, ErpOrderItem::getParentId);
        mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(DepotPut::getHolderKey), holderKey);
        mpjLambdaWrapper.eq("t." + MybatisPlusUtil.toColumns(DepotPut::getState), FlowableStateEnum.PASS.getKey());
        if (Integer.parseInt(type) == DepotIdKeyEnum.DEPOT_OUT.getKey()) {
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(DepotPut::getIdKey), DepotIdKeyEnum.DEPOT_OUT.getIdKey());
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(DepotPut::getFromTypeId), DepotOutFromType.LOANOUT.getKey());
        } else {
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(DepotPut::getIdKey), DepotIdKeyEnum.DEPOT_PUT.getIdKey());
            mpjLambdaWrapper.eq(MybatisPlusUtil.toColumns(DepotPut::getFromTypeId), DepotPutFromType.LOANIN.getKey());
        }
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            mpjLambdaWrapper.eq("t." + CommonConstants.TENANT_ID_FIELD, tenantId);
            mpjLambdaWrapper.eq("i." + CommonConstants.TENANT_ID_FIELD, tenantId);
        }
        List<ErpOrderItem> erpOrderItemList = skyeyeBaseMapper.selectJoinList(ErpOrderItem.class, mpjLambdaWrapper);
        return erpOrderItemList;
    }

}
