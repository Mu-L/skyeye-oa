/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.dao.ErpPageDao;
import com.skyeye.purchase.service.impl.PurchaseOrderServiceImpl;
import com.skyeye.retail.service.impl.RetailOutLetServiceImpl;
import com.skyeye.seal.service.impl.SalesOrderServiceImpl;
import com.skyeye.service.ErpPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ErpPageServiceImpl
 * @Description: ERP统计模块服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/2 11:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpPageServiceImpl implements ErpPageService {

    @Autowired
    private ErpPageDao erpPageDao;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    @IgnoreTenant
    public void queryFourTypeMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 1.获取本月累计销售，当前月已审核通过的销售订单金额
        String salesMoney = erpPageDao.queryThisMonthErpOrder(SalesOrderServiceImpl.class.getName(), states, tenantId);
        // 2.获取本月累计零售，当前月已审核通过的零售订单金额
        String retailMoney = erpPageDao.queryThisMonthErpOrder(RetailOutLetServiceImpl.class.getName(), states, tenantId);
        // 3.获取本月累计采购，当前月已审核通过的采购订单金额
        String purchaseMoney = erpPageDao.queryThisMonthErpOrder(PurchaseOrderServiceImpl.class.getName(), states, tenantId);
        // 4.本月利润（已审核通过），零售订单金额 + 销售订单金额 - 采购订单金额
        String profitMoney = CalculationUtil.subtract(CalculationUtil.add(salesMoney, retailMoney), purchaseMoney);
        Map<String, Object> map = new HashMap<>();
        map.put("salesMoney", salesMoney);
        map.put("retailMoney", retailMoney);
        map.put("purchaseMoney", purchaseMoney);
        map.put("profitMoney", profitMoney);
        outputObject.setBean(map);
    }

    @Override
    @IgnoreTenant
    public void querySixMonthPurchaseMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = erpPageDao.querySixMonthOrderMoneyList(PurchaseOrderServiceImpl.class.getName(), states, tenantId);
        outputObject.setBeans(beans);
    }

    @Override
    @IgnoreTenant
    public void querySixMonthSealsMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = erpPageDao.querySixMonthOrderMoneyList(SalesOrderServiceImpl.class.getName(), states, tenantId);
        outputObject.setBeans(beans);
    }

    @Override
    @IgnoreTenant
    public void queryTwelveMonthProfitMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        List<String> idKeys = Arrays.asList(SalesOrderServiceImpl.class.getName(),
            PurchaseOrderServiceImpl.class.getName(),
            RetailOutLetServiceImpl.class.getName());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = erpPageDao.queryTwelveMonthProfitMoneyList(idKeys, states, tenantId);
        outputObject.setBeans(beans);
    }

}
