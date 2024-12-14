/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.dao.CouponUseDao;
import com.skyeye.coupon.entity.Coupon;
import com.skyeye.coupon.entity.CouponMaterial;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.entity.CouponUseMaterial;
import com.skyeye.coupon.enums.CouponUseState;
import com.skyeye.coupon.enums.CouponValidityType;
import com.skyeye.coupon.enums.PromotionDiscountType;
import com.skyeye.coupon.service.CouponService;
import com.skyeye.coupon.service.CouponUseMaterialService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: CouponUseServiceImpl
 * @Description: 优惠券领取信息管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/23 10:43
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "优惠券领取信息管理", groupName = "优惠券领取信息管理")
public class CouponUseServiceImpl extends SkyeyeBusinessServiceImpl<CouponUseDao, CouponUse> implements CouponUseService {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUseMaterialService couponUseMaterialService;

    @Autowired
    private IQuartzService iQuartzService;

    private void check(Coupon coupon) {
        if (ObjectUtil.isEmpty(coupon)) {
            throw new CustomException("优惠券不存在");
        }
        if (Objects.equals(coupon.getEnabled(), WhetherEnum.DISABLE_USING.getKey())) {
            throw new CustomException("优惠券已过期");
        }
        if (coupon.getTotalCount() != -1) {
            // 优惠券数量限制, -1表示不限制, 其他正数表示数量限制
            if (coupon.getTakeCount() >= coupon.getTotalCount()) {
                throw new CustomException("优惠券已被领完.");
            }
        }
        // 领取限制, -1表示不限制
        if (coupon.getTakeLimitCount() == -1) {
            return;
        }
        // 个人领取该优惠券的数量限制查询
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCouponId), coupon.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        if (count(queryWrapper) >= coupon.getTakeLimitCount()) {
            throw new CustomException("超出领取数量限制");
        }
    }

    @Override
    public void createPrepose(CouponUse couponUse) {
        Coupon coupon = couponService.selectById(couponUse.getCouponId());
        check(coupon);
        // 设置适用对象
        List<CouponUseMaterial> couponUseMaterialList = couponUse.getCouponUseMaterialList();
        for (CouponMaterial couponMaterial : coupon.getCouponMaterialList()) {
            CouponUseMaterial couponUseMaterial = new CouponUseMaterial();
            couponUseMaterial.setCouponId(coupon.getId());
            couponUseMaterial.setMaterialId(couponMaterial.getMaterialId());
            couponUseMaterialList.add(couponUseMaterial);
        }
        // 状态
        couponUse.setState(CouponUseState.UNUSED.getKey());
        //满减
        couponUse.setUsePrice(coupon.getUsePrice());
        //使用范围
        couponUse.setProductScope(coupon.getProductScope());
        //生效时间
        if (Objects.equals(CouponValidityType.DATE.getKey(), coupon.getValidityType())) {
            couponUse.setValidStartTime(coupon.getValidStartTime());
            couponUse.setValidEndTime(coupon.getValidEndTime());
        } else {
            DateFormat df = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
            couponUse.setValidStartTime(df.format(DateUtil.getAfDate(LocalDate.now().toDate(), coupon.getFixedStartTime(), "d")));
            couponUse.setValidEndTime(df.format(DateUtil.getAfDate(LocalDate.now().toDate(), coupon.getFixedEndTime(), "d")));
        }
        // 领取非固定类型优惠券时，借助couponMation成员变量存储优惠券信息，便于后置执行新增定时任务
        couponUse.setCouponMation(coupon);
        //折扣类型
        couponUse.setDiscountType(coupon.getDiscountType());
        //折扣值
        if (Objects.equals(PromotionDiscountType.PERCENT.getKey(), coupon.getDiscountType())) {
            couponUse.setDiscountPercent(coupon.getDiscountPercent());
        } else {
            couponUse.setDiscountPrice(coupon.getDiscountPrice());
        }
        //折扣上限
        couponUse.setDiscountLimitPrice(coupon.getDiscountLimitPrice());
    }

    @Override
    public void createPostpose(CouponUse couponUse, String userId) {
        // 更新优惠券领取数量
        couponService.updateTakeCount(couponUse.getCouponId(), couponUse.getCouponMation().getTakeCount() + 1);
        Integer useCount = couponService.getUseCount(couponUse.getCouponId());
        couponUse.setUsageCount(useCount);
        // 新增优惠券可使用的商品信息
        couponUseMaterialService.createEntity(couponUse.getCouponUseMaterialList(), userId);
        // 定时任务
        Coupon couponMation = couponUse.getCouponMation();
        if (Objects.equals(couponMation.getValidityType(), CouponValidityType.TERM.getKey())) {
            startUpTaskQuartz(couponUse.getId(), couponMation.getName(), couponUse.getValidEndTime());
        }
    }

    private void startUpTaskQuartz(String name, String title, String delayedTime) {
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(delayedTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.SHOP_COUPON_USE.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    @Override
    public void writePostpose(CouponUse couponUse, String userId) {
        if (ObjectUtil.isNotEmpty(couponUse.getCouponUseMaterialList())) {
            couponUse.getCouponUseMaterialList().forEach(couponMaterial -> couponMaterial.setCouponId(couponUse.getId()));
            couponUseMaterialService.createEntity(couponUse.getCouponUseMaterialList(), userId);
        }
    }

    @Override
    public void updatePrepose(CouponUse couponUse) {
        if (StrUtil.isNotEmpty(couponUse.getUseOrderId())) {
            couponUse.setUseTime(DateUtil.getTimeAndToString());
            couponUse.setState(CouponUseState.USED.getKey());
        }
    }

    @Override
    public void updatePostpose(CouponUse couponUse, String userId) {
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCouponId), couponUse.getCouponId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.USED.getKey());
        Coupon coupon = couponService.selectById(couponUse.getCouponId());
        if (ObjectUtil.isNotEmpty(coupon) && Objects.equals(coupon.getValidityType(), CouponValidityType.TERM.getKey())) {
            iQuartzService.stopAndDeleteTaskQuartz(couponUse.getId());// 删除任务
        }
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> mapList = super.queryPageDataList(inputObject);
        couponService.setMationForMap(mapList, "couponId", "couponMation");
        return mapList;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        Map<String, Object> params = inputObject.getParams();
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), inputObject.getLogParams().get("id").toString());
        if (params.containsKey("state")) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), params.get("state").toString());
        }
        // 查询时获取数据
        List<CouponUse> list = list(queryWrapper);
        couponService.setDataMation(list, CouponUse::getCouponId);
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }

    @Override
    public Map<String, Integer> queryIdTotalMapByCouponId(List<String> couponIdList) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isEmpty(userToken)) {
            return new HashMap<>();
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(MybatisPlusUtil.toColumns(CouponUse::getCouponId), "count(id) as total");
        queryWrapper.in(MybatisPlusUtil.toColumns(CouponUse::getCouponId), couponIdList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), userId);
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(CouponUse::getCouponId));
        List<Map<String, Object>> mapList = listMaps(queryWrapper);
        return CollectionUtil.isEmpty(mapList) ? new HashMap<>()
            : mapList.stream().collect(Collectors.toMap(map -> map.get("coupon_id").toString(), map -> Integer.parseInt(map.get("total").toString())));
    }

    /**
     * xxlJob任务管理器定时修改过期优惠券的状态
     */
    @Override
    public void setCouponUseStateByDate(String couponId) {
        UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
        // 取优未使用的优惠券
        updateWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.UNUSED.getKey());
        updateWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCouponId), couponId);
        // 更改状态为过期
        updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.EXPIRE.getKey());
        update(updateWrapper);
    }

    @Override
    public void setCouponUseStateByTerm(String userId, String couponUseId) {
        UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, couponUseId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getCreateId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.EXPIRE.getKey());
        update(updateWrapper);
    }

    @Override
    public void updateState(String couponUseId) {
        UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, couponUseId);
        updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.USED.getKey());
        update(updateWrapper);
    }

    @Override
    public void UpdateUsedCount(String couponUseId) {
        CouponUse couponUse = selectById(couponUseId);
        if (couponUse.getUsedCount() < couponUse.getUsageCount()) {
            UpdateWrapper<CouponUse> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, couponUseId);
            updateWrapper.set(MybatisPlusUtil.toColumns(CouponUse::getUsedCount), couponUse.getUsedCount() + 1);
            update(updateWrapper);
        } else {
            throw new CustomException("优惠券使用次数已达到上限");
        }
    }

    @Override
    public void deleteByCouponIds(List<String> ids) {
        QueryWrapper<CouponUse> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CouponUse::getCouponId), ids);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CouponUse::getState), CouponUseState.UNUSED.getKey());
        remove(queryWrapper);
    }
}