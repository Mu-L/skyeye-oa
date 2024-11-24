/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.coupon.entity.CouponUseMaterial;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CouponUseMaterialService
 * @Description: 优惠券领取的优惠券适用商品对象管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CouponUseMaterialService extends SkyeyeBusinessService<CouponUseMaterial> {
    List<CouponUseMaterial> queryListByCouponIds(List<String> couponIds);
}
