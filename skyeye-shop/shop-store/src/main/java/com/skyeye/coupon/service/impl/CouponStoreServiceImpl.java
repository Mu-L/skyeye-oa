package com.skyeye.coupon.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponStoreDao;
import com.skyeye.coupon.entity.CouponStore;
import com.skyeye.coupon.service.CouponStoreService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SkyeyeService(name = "门店与优惠券关联表信息管理", groupName = "门店与优惠券关联表信息管理")
public class CouponStoreServiceImpl extends SkyeyeBusinessServiceImpl<CouponStoreDao, CouponStore> implements CouponStoreService {

    @Override
    public void createEntity(String couponId, List<String> storeIdList) {
        List<CouponStore> list = super.queryAllData();
        for (String s : storeIdList) {
            CouponStore couponStore = new CouponStore();
            couponStore.setStoreId(s);
            couponStore.setCouponId(couponId);
            list.add(couponStore);
        }
        super.createEntity(list, StrUtil.EMPTY);
    }

    @Override
    public List<CouponStore> queryListByStoreId(String storeId) {
        QueryWrapper<CouponStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponStore::getStoreId), storeId);
        return CollectionUtil.isEmpty(list(queryWrapper)) ? new ArrayList<>() : list(queryWrapper);
    }
}