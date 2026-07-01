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
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.flowable.classenum.FormSubType;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.classenum.TenantAppBuyOrderPayState;
import com.skyeye.tenant.classenum.TenantAppBuyOrderSource;
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

    @Autowired
    private PlatformBaseSettingService platformBaseSettingService;

    @Override
    protected void createPrepose(TenantAppBuyOrder entity) {
        if (entity.getOrderSource() == null) {
            entity.setOrderSource(TenantAppBuyOrderSource.PLATFORM.getKey());
        }
    }

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
        validateBuyOrderSeatNum(entity);
        validateBuyOrderAppYear(entity);
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

    /**
     * 校验购买席位数是否满足组织类型对应的最低购买数量
     */
    private void validateBuyOrderSeatNum(TenantAppBuyOrder entity) {
        if (CollectionUtil.isEmpty(entity.getTenantAppBuyOrderNumList())) {
            return;
        }
        Tenant buyTenant = tenantService.selectById(entity.getBuyTenantId());
        if (ObjectUtil.isEmpty(buyTenant) || buyTenant.getOrgType() == null) {
            throw new CustomException("购买租户信息不完整，无法校验席位购买数量");
        }
        Integer minBuyAccountNum = platformBaseSettingService.getMinBuyAccountNum(buyTenant.getOrgType());
        String platformUnitPrice = platformBaseSettingService.getAccountUnitPrice();
        for (TenantAppBuyOrderNum tenantAppBuyOrderNum : entity.getTenantAppBuyOrderNumList()) {
            if (tenantAppBuyOrderNum.getAccountNum() == null || tenantAppBuyOrderNum.getAccountNum() < minBuyAccountNum) {
                throw new CustomException("购买席位数不能低于" + minBuyAccountNum + "个");
            }
            if (StrUtil.isBlank(tenantAppBuyOrderNum.getUnitPrice())) {
                tenantAppBuyOrderNum.setUnitPrice(platformUnitPrice);
            }
        }
    }

    /**
     * 校验应用购买行并补全单价
     */
    private void validateBuyOrderAppYear(TenantAppBuyOrder entity) {
        if (CollectionUtil.isEmpty(entity.getTenantAppBuyOrderYearList())) {
            return;
        }
        List<String> appIds = entity.getTenantAppBuyOrderYearList().stream()
            .map(TenantAppBuyOrderYear::getAppId).collect(Collectors.toList());
        Map<String, TenantApp> tenantAppMap = tenantAppService.queryTenantAppByAppId(appIds.toArray(new String[]{}));
        for (TenantAppBuyOrderYear tenantAppBuyOrderYear : entity.getTenantAppBuyOrderYearList()) {
            if (tenantAppBuyOrderYear.getAccountYear() < CommonNumConstants.NUM_ONE) {
                throw new CustomException("购买应用年限不能小于1年");
            }
            TenantApp tenantApp = tenantAppMap.get(tenantAppBuyOrderYear.getAppId());
            if (ObjectUtil.isEmpty(tenantApp)) {
                throw new CustomException("购买的应用不存在");
            }
        }
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void submitTenantSelfPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        TenantAppBuyOrder entity = inputObject.getParams(TenantAppBuyOrder.class);
        entity.setBuyTenantId(TenantContext.getTenantId());
        entity.setOrderSource(TenantAppBuyOrderSource.TENANT.getKey());
        entity.setFormSubType(FormSubType.DRAFT.getKey());
        String userId = inputObject.getLogParams().get("id").toString();
        TenantContext.setTenantId(TenantTypeEnum.PLATFORM.getCode());
        String orderId = createEntity(entity, userId);
        autoApprovalPass(orderId);
        outputObject.setBean(selectById(orderId));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void updatePayState(String id, Integer payState, String payRemark) {
        UpdateWrapper<TenantAppBuyOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayState), payState);
        if (TenantAppBuyOrderPayState.PAID.getKey().equals(payState)) {
            updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayTime), DateUtil.getTimeAndToString());
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getPayRemark), payRemark);
        update(updateWrapper);
        refreshCache(id);
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
        if (StrUtil.isNotEmpty(tenantAppBuyOrder.getBuyTenantId())) {
            tenantService.markHasPassedAppBuyOrder(tenantAppBuyOrder.getBuyTenantId());
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
        updatePayState(id, TenantAppBuyOrderPayState.PAID.getKey(), payRemark);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelPayTenantAppBuyOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String payRemark = params.get("payRemark").toString();
        TenantAppBuyOrder tenantAppBuyOrder = selectById(id);
        assertApprovedAndUnpaid(tenantAppBuyOrder);
        updatePayState(id, TenantAppBuyOrderPayState.PAY_CANCELLED.getKey(), payRemark);
    }

    private void assertApprovedAndUnpaid(TenantAppBuyOrder tenantAppBuyOrder) {
        if (ObjectUtil.isEmpty(tenantAppBuyOrder) || StrUtil.isEmpty(tenantAppBuyOrder.getId())) {
            throw new CustomException("订单不存在");
        }
        if (!isApprovedFlowableEntity(tenantAppBuyOrder)) {
            throw new CustomException("当前订单未审批通过，无法操作");
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
        return list(orderQw).stream().filter(order -> !isInactiveFlowableEntity(order)).count();
    }

    @Override
    @IgnoreTenant
    public void queryTenantOrderStatistics(InputObject inputObject, OutputObject outputObject) {
        String tenantId = inputObject.getParams().get("tenantId").toString();
        // 1. 查询租户的订单信息--包括所有状态
        QueryWrapper<TenantAppBuyOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrder::getBuyTenantId), tenantId);
        List<TenantAppBuyOrder> tenantAppBuyOrderList = list(queryWrapper);
        // 2. 查询租户的总订单金额--审核通过的订单
        BigDecimal totalPrice = tenantAppBuyOrderList.stream()
            .filter(this::isApprovedFlowableEntity)
            .map(bean -> new BigDecimal(bean.getAllPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 3. 查询租户的总订单数量--审核通过的订单
        long totalCount = tenantAppBuyOrderList.stream()
            .filter(this::isApprovedFlowableEntity)
            .count();
        // 4. 已支付订单金额与数量
        BigDecimal paidTotalPrice = tenantAppBuyOrderList.stream()
            .filter(this::isPaidBuyOrder)
            .map(bean -> new BigDecimal(bean.getAllPrice()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long paidCount = tenantAppBuyOrderList.stream()
            .filter(this::isPaidBuyOrder)
            .count();
        // 5. 查询租户购买的应用数量
        List<TenantAppLink> tenantAppLinks = tenantAppLinkService.selectByTenantId(tenantId);
        int appCount = tenantAppLinks.size();
        // 6. 封装数据
        Map<String, Object> data = new HashMap<>();
        data.put("totalPrice", totalPrice);
        data.put("totalCount", totalCount);
        data.put("paidTotalPrice", paidTotalPrice);
        data.put("paidCount", paidCount);
        data.put("appCount", appCount);
        outputObject.setBean(data);
        outputObject.setBeans(tenantAppBuyOrderList);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 是否已支付
     */
    private boolean isPaidBuyOrder(TenantAppBuyOrder order) {
        return order != null
            && TenantAppBuyOrderPayState.PAID.getKey().equals(order.getPayState());
    }

}
