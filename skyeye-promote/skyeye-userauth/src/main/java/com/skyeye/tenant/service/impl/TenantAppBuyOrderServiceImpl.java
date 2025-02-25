/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.dao.TenantAppBuyOrderDao;
import com.skyeye.tenant.entity.TenantApp;
import com.skyeye.tenant.entity.TenantAppBuyOrder;
import com.skyeye.tenant.entity.TenantAppBuyOrderNum;
import com.skyeye.tenant.entity.TenantAppBuyOrderYear;
import com.skyeye.tenant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantAppBuyOrderServiceImpl
 * @Description: 订单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/30 16:25
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "订单管理", groupName = "租户管理", flowable = true, tenant = TenantEnum.PLATE)
public class TenantAppBuyOrderServiceImpl extends SkyeyeFlowableServiceImpl<TenantAppBuyOrderDao, TenantAppBuyOrder> implements TenantAppBuyOrderService {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantAppService tenantAppService;

    @Autowired
    private TenantAppBuyOrderNumService tenantAppBuyOrderNumService;

    @Autowired
    private TenantAppBuyOrderYearService tenantAppBuyOrderYearService;

    @Autowired
    private TenantAppLinkService tenantAppLinkService;

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageData(inputObject);
        tenantService.setMationForMap(beans, "buyTenantId", "buyTenantMation");
        return beans;
    }

    @Override
    public void validatorEntity(TenantAppBuyOrder entity) {
        if (CollectionUtil.isEmpty(entity.getTenantAppBuyOrderNumList()) && CollectionUtil.isEmpty(entity.getTenantAppBuyOrderYearList())) {
            throw new CustomException("订单信息不能为空.");
        }
        String totalPrice = "0";
        if (CollectionUtil.isNotEmpty(entity.getTenantAppBuyOrderNumList())) {
            for (TenantAppBuyOrderNum tenantAppBuyOrderNum : entity.getTenantAppBuyOrderNumList()) {
                String allPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(tenantAppBuyOrderNum.getAccountNum()), tenantAppBuyOrderNum.getUnitPrice());
                tenantAppBuyOrderNum.setAllPrice(allPrice);
                totalPrice = CalculationUtil.add(totalPrice, allPrice);
            }
        }
        if (CollectionUtil.isNotEmpty(entity.getTenantAppBuyOrderYearList())) {
            for (TenantAppBuyOrderYear tenantAppBuyOrderYear : entity.getTenantAppBuyOrderYearList()) {
                String allPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(tenantAppBuyOrderYear.getAccountYear()), tenantAppBuyOrderYear.getUnitPrice());
                tenantAppBuyOrderYear.setAllPrice(allPrice);
                totalPrice = CalculationUtil.add(totalPrice, allPrice);
            }
        }
        entity.setAllPrice(totalPrice);
    }

    @Override
    public void writePostpose(TenantAppBuyOrder entity, String userId) {
        tenantAppBuyOrderNumService.saveList(entity.getId(), entity.getTenantAppBuyOrderNumList());
        tenantAppBuyOrderYearService.saveList(entity.getId(), entity.getTenantAppBuyOrderYearList());
        super.writePostpose(entity, userId);
    }

    @Override
    public TenantAppBuyOrder getDataFromDb(String id) {
        TenantAppBuyOrder tenantAppBuyOrder = super.getDataFromDb(id);
        tenantAppBuyOrder.setTenantAppBuyOrderNumList(tenantAppBuyOrderNumService.selectByParentId(id));
        tenantAppBuyOrder.setTenantAppBuyOrderYearList(tenantAppBuyOrderYearService.selectByParentId(id));
        return tenantAppBuyOrder;
    }

    @Override
    public TenantAppBuyOrder selectById(String id) {
        TenantAppBuyOrder tenantAppBuyOrder = super.selectById(id);
        tenantService.setDataMation(tenantAppBuyOrder, TenantAppBuyOrder::getBuyTenantId);
        if (CollectionUtil.isNotEmpty(tenantAppBuyOrder.getTenantAppBuyOrderYearList())) {
            List<String> appIds = tenantAppBuyOrder.getTenantAppBuyOrderYearList().stream().map(TenantAppBuyOrderYear::getAppId).collect(Collectors.toList());
            Map<String, TenantApp> tenantAppMap = tenantAppService.queryTenantAppByAppId(appIds.toArray(new String[]{}));
            tenantAppBuyOrder.getTenantAppBuyOrderYearList().forEach(tenantAppBuyOrderYear -> {
                tenantAppBuyOrderYear.setAppMation(tenantAppMap.get(tenantAppBuyOrderYear.getAppId()));
            });
        }
        return tenantAppBuyOrder;
    }

    @Override
    public void approvalEndIsSuccess(TenantAppBuyOrder entity) {
        TenantAppBuyOrder tenantAppBuyOrder = selectById(entity.getId());
        if (CollectionUtil.isNotEmpty(tenantAppBuyOrder.getTenantAppBuyOrderNumList())) {
            tenantAppBuyOrder.getTenantAppBuyOrderNumList().forEach(tenantAppBuyOrderNum -> {
                tenantService.editTenantAccountNumber(tenantAppBuyOrder.getBuyTenantId(), tenantAppBuyOrderNum.getAccountNum());
            });
        }

        if (CollectionUtil.isNotEmpty(tenantAppBuyOrder.getTenantAppBuyOrderYearList())) {
            tenantAppBuyOrder.getTenantAppBuyOrderYearList().forEach(tenantAppBuyOrderYear -> {
                tenantAppLinkService.saveTenantAppLink(tenantAppBuyOrder.getBuyTenantId(), tenantAppBuyOrderYear.getAppId(), tenantAppBuyOrderYear.getAccountYear());
            });
        }
    }
}
