/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.classenum.TenantAppBuyOrderPayState;
import com.skyeye.tenant.dao.TenantAppBuyOrderDao;
import com.skyeye.tenant.entity.*;
import com.skyeye.tenant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
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
public class TenantAppBuyOrderServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppBuyOrderDao, TenantAppBuyOrder> implements TenantAppBuyOrderService {

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
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
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
        // 默认待支付
        entity.setPayState(TenantAppBuyOrderPayState.UNPAID.getKey());
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
        if (ObjectUtil.isEmpty(tenantAppBuyOrder) || StrUtil.isEmpty(tenantAppBuyOrder.getId())) {
            throw new CustomException("订单不存在");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void payTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String payRemark = params.get("payRemark").toString();
        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        assertApprovedAndUnpaid(tenantAppBuyOrder);
        deliverOrderBenefits(tenantAppBuyOrder);
        UpdateWrapper<TenantAppBuyOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayState), TenantAppBuyOrderPayState.PAID.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayRemark), payRemark);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelPayTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String payRemark = params.get("payRemark").toString();
        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        assertApprovedAndUnpaid(tenantAppBuyOrder);
        UpdateWrapper<TenantAppBuyOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayState), TenantAppBuyOrderPayState.PAY_CANCELLED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayRemark), payRemark);
        update(updateWrapper);
        refreshCache(id);
    }

    private void assertApprovedAndUnpaid(TenantAppBuyOrder tenantAppBuyOrder) {
        if (ObjectUtil.isEmpty(tenantAppBuyOrder) || StrUtil.isEmpty(tenantAppBuyOrder.getId())) {
            throw new CustomException("订单不存在");
        }
        if (tenantAppBuyOrder.getPayState() == null) {
            throw new CustomException("该订单支付状态异常，请联系管理员处理");
        }
        if (!TenantAppBuyOrderPayState.UNPAID.getKey().equals(tenantAppBuyOrder.getPayState())) {
            throw new CustomException("当前订单不是待支付状态");
        }
    }

    private void deliverOrderBenefits(TenantAppBuyOrder tenantAppBuyOrder) {
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
        if (StrUtil.isNotEmpty(tenantAppBuyOrder.getBuyTenantId())) {
            tenantService.markHasPassedAppBuyOrder(tenantAppBuyOrder.getBuyTenantId());
        }
    }

    @Override
    @IgnoreTenant
    public long countActiveBuyOrdersByBuyTenantId(String buyTenantId) {
        QueryWrapper<TenantAppBuyOrder> orderQw = new QueryWrapper<>();
        orderQw.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getBuyTenantId), buyTenantId);
        orderQw.notIn(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getState),
            FlowableStateEnum.DRAFT.getKey(),
            FlowableStateEnum.INVALID.getKey());
        return count(orderQw);
    }

    @Override
    @IgnoreTenant
    public void queryTenantOrderStatistics(InputObject inputObject, OutputObject outputObject) {
        String tenantId = inputObject.getParams().get("tenantId").toString();
        // 1. 查询租户的订单信息--包括所有状态
        QueryWrapper<TenantAppBuyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getBuyTenantId), tenantId);
        List<TenantAppBuyOrder> tenantAppBuyOrderList = list(queryWrapper);
        // 2. 查询租户的总订单金额--包括审核通过的
        BigDecimal totalPrice = tenantAppBuyOrderList.stream()
            .filter(tenantAppBuyOrder -> tenantAppBuyOrder.getState().equals(FlowableStateEnum.PASS.getKey()))
            .map(bean -> new BigDecimal(bean.getAllPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 3. 查询租户的总订单数量--包括审核通过的
        long totalCount = tenantAppBuyOrderList.stream()
            .filter(tenantAppBuyOrder -> tenantAppBuyOrder.getState().equals(FlowableStateEnum.PASS.getKey()))
            .count();
        // 4. 查询租户购买的应用数量
        List<TenantAppLink> tenantAppLinks = tenantAppLinkService.selectByTenantId(tenantId);
        int appCount = tenantAppLinks.size();
        // 5. 封装数据
        Map<String, Object> data = new HashMap<>();
        data.put("totalPrice", totalPrice);
        data.put("totalCount", totalCount);
        data.put("appCount", appCount);
        outputObject.setBean(data);
        outputObject.setBeans(tenantAppBuyOrderList);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
