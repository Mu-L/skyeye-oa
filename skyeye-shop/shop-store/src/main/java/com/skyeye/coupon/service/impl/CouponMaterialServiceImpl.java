/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponMaterialDao;
import com.skyeye.coupon.entity.CouponMaterial;
import com.skyeye.coupon.service.CouponMaterialService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: CouponMaterialServiceImpl
 * @Description: 优惠券/模版适用商品对象管理服务实现类--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:37
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "优惠券/模版适用商品对象管理", groupName = "优惠券/模版适用商品对象管理", manageShow = false, tenant = TenantEnum.NO_ISOLATION)
public class CouponMaterialServiceImpl extends SkyeyeBusinessServiceImpl<CouponMaterialDao, CouponMaterial> implements CouponMaterialService {

    @Override
    public List<CouponMaterial> queryListByCouponId(String couponId) {
        QueryWrapper<CouponMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponMaterial::getCouponId), couponId);
        return list(queryWrapper);
    }

    @Override
    public void deleteByCouponId(String id) {
        QueryWrapper<CouponMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponMaterial::getCouponId), id);
        remove(queryWrapper);
    }

    @Override
    public void deleteByCouponId(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        QueryWrapper<CouponMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CouponMaterial::getCouponId), ids);
        remove(queryWrapper);
    }

    @Override
    public void insertCouponMaterial(String couponId, List<CouponMaterial> couponMaterialList, String userId) {
        // 删除原本的适用商品信息
        deleteByCouponId(couponId);
        // 批量新增
        if (CollectionUtil.isNotEmpty(couponMaterialList)) {
            couponMaterialList.forEach(couponMaterial -> {
                couponMaterial.setCouponId(couponId);
            });
            createEntity(couponMaterialList, userId);
        }
    }
}
