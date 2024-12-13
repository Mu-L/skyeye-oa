/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.json.JSONUtil;
import com.skyeye.coupon.service.CouponService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.order.service.OrderService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
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

    @XxlJob("setShopCouponStateService")
    public void setShopCouponStateService() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String couponId = paramMap.get("objectId");// 优惠券id
        try {
            couponService.setStateByCoupon(couponId);// 修改优惠券的状态
            couponUseService.setCouponUseStateByDate(couponId);// 修改领取的优惠券的状态
        } finally {
            iQuartzService.stopAndDeleteTaskQuartz(couponId);// 删除任务
        }
    }

    @XxlJob("setShopCouponUseStateService")
    public void setShopCouponUseStateService() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String userId = paramMap.get("userId");
        String couponUseId = paramMap.get("objectId");// 领取的优惠券id
        try {
            couponUseService.setCouponUseStateByTerm(userId, couponUseId);// 修改领取的优惠券的状态}
        } finally {
            iQuartzService.stopAndDeleteTaskQuartz(couponUseId);// 删除任务
        }
    }

    @XxlJob("createOrderNotPay")
    public void createOrderNotPay() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String orderId = paramMap.get("objectId");// 订单的主键id
        try {
            orderService.setOrderCancle(orderId);// 修改订单的状态为取消
        } finally {
            iQuartzService.stopAndDeleteTaskQuartz(orderId);// 删除任务
        }
    }
}
