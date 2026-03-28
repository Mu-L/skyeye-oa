/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.coupon.service.CouponService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.order.service.OrderService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: ShopXxlJob
 * @Description: 优惠券过期记录
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/11 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
public class ShopXxlJob {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUseService couponUseService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private IQuartzService iQuartzService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @XxlJob("setShopCouponStateService")
    public void setShopCouponStateService() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        String couponId = paramMap.get("objectId");// 优惠券id
        try {
            log.info("优惠券id(couponId)" + couponId + "---修改优惠券的状态---开始");
            couponService.setStateByCoupon(couponId);// 修改优惠券的状态
            log.info("优惠券id(couponId)" + couponId + "---修改优惠券的状态---结束");
            log.info("优惠券id(couponId)" + couponId + "---修改领取的优惠券的状态---开始");
            couponUseService.setCouponUseStateByDate(couponId);// 修改领取的优惠券的状态
            log.info("优惠券id(couponId)" + couponId + "---修改领取的优惠券的状态---结束");
        } finally {
            log.info("优惠券id(couponId)" + couponId + "---删除任务---开始");
            iQuartzService.stopAndDeleteTaskQuartz(couponId);// 删除任务
            log.info("优惠券id(couponId)" + couponId + "---删除任务---结束");
        }
    }

    @XxlJob("setShopCouponUseStateService")
    public void setShopCouponUseStateService() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String userId = paramMap.get("userId");
        String couponUseId = paramMap.get("objectId");// 领取的优惠券id
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            log.info("领取优惠券的id(couponUseId)" + couponUseId + "---修改领取的优惠券的状态---开始");
            couponUseService.setCouponUseStateByTerm(userId, couponUseId);// 修改领取的优惠券的状态}
            log.info("领取优惠券的id(couponUseId)" + couponUseId + "---修改领取的优惠券的状态---结束");
        } finally {
            log.info("领取优惠券的id(couponUseId)" + couponUseId + "---删除任务---开始");
            iQuartzService.stopAndDeleteTaskQuartz(couponUseId);// 删除任务
            log.info("领取优惠券的id(couponUseId)" + couponUseId + "---删除任务---结束");
        }
    }

    @XxlJob("createOrderNotPay")
    public void createOrderNotPay() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String orderId = paramMap.get("objectId");// 订单的主键id
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            log.info("订单的主键id(orderId)" + orderId + "---修改订单的状态为取消---开始");
            orderService.setOrderCancle(orderId);// 修改订单的状态为取消
            log.info("订单的主键id(orderId)" + orderId + "---修改订单的状态为取消---结束");
        } finally {
            log.info("订单的主键id(orderId)" + orderId + "---删除任务---开始");
            iQuartzService.stopAndDeleteTaskQuartz(orderId);// 删除任务
            log.info("订单的主键id(orderId)" + orderId + "---删除任务---结束");
        }
    }
}
