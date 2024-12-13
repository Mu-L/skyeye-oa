package com.skyeye.coupon.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.coupon.entity.CouponStore;

import java.util.List;

public interface CouponStoreService extends SkyeyeBusinessService<CouponStore> {
    void createEntity(String couponId, List<String> storeIdList);

    List<CouponStore> queryListByStoreId(String storeId);
}
