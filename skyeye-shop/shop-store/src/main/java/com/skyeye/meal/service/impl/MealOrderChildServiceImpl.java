/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.meal.classenum.ShopMealOrderChildState;
import com.skyeye.meal.classenum.ShopMealUseType;
import com.skyeye.meal.dao.MealOrderChildDao;
import com.skyeye.meal.entity.MealOrderChild;
import com.skyeye.meal.entity.ShopMeal;
import com.skyeye.meal.service.MealOrderChildService;
import com.skyeye.meal.service.MealRefundOrderService;
import com.skyeye.meal.service.ShopMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MealOrderChildServiceImpl
 * @Description: 套餐订单所选套餐服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 9:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "套餐订单所选套餐", groupName = "套餐订单管理")
public class MealOrderChildServiceImpl extends SkyeyeBusinessServiceImpl<MealOrderChildDao, MealOrderChild> implements MealOrderChildService {

    @Autowired
    private ShopMealService shopMealService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private MealRefundOrderService mealRefundOrderService;

    @Override
    public String calculationTotalPrice(String objectId, String objectKey, List<MealOrderChild> mealOrderChildList) {
        String payablePrice = CommonNumConstants.NUM_ZERO.toString();
        List<String> mealIds = mealOrderChildList.stream().map(MealOrderChild::getMealId).distinct().collect(Collectors.toList());
        Map<String, ShopMeal> shopMealMap = shopMealService.selectMapByIds(mealIds);
        for (MealOrderChild bean : mealOrderChildList) {
            bean.setObjectId(objectId);
            bean.setObjectKey(objectKey);
            ShopMeal shopMeal = shopMealMap.get(bean.getMealId());
            if (ObjectUtil.isEmpty(shopMeal) || StrUtil.isEmpty(shopMeal.getId())) {
                throw new CustomException("套餐不存在，请刷新后重试.");
            }
            bean.setMealPrice(shopMeal.getShowPrice());
            payablePrice = CalculationUtil.add(payablePrice, shopMeal.getShowPrice(), CommonNumConstants.NUM_TWO);
        }
        return payablePrice;
    }

    @Override
    public void deleteByOrderId(String orderId) {
        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getOrderId), orderId);
        remove(queryWrapper);
    }

    @Override
    public List<MealOrderChild> selectByOrderId(String orderId) {
        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getOrderId), orderId);
        return list(queryWrapper);
    }

    @Override
    public void saveList(String orderId, List<MealOrderChild> mealOrderChildList) {
        deleteByOrderId(orderId);
        if (CollectionUtil.isNotEmpty(mealOrderChildList)) {
            for (MealOrderChild mealOrderChild : mealOrderChildList) {
                mealOrderChild.setOrderId(orderId);
                // 子订单初始为待支付
                mealOrderChild.setState(ShopMealOrderChildState.WAIT_PAY.getKey());
                mealOrderChild.setUseNum(0);
            }
            createEntity(mealOrderChildList, StrUtil.EMPTY);
        }
    }

    @Override
    public void queryMealMationByObjectId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());

        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(MealOrderChild::getStartTime));
        List<MealOrderChild> mealOrderChildList = list(queryWrapper);
        shopMealService.setDataMation(mealOrderChildList, MealOrderChild::getMealId);
        // 产品信息
        iMaterialService.setDataMation(mealOrderChildList, MealOrderChild::getMaterialId);
        // 规格信息
        iMaterialNormsService.setDataMation(mealOrderChildList, MealOrderChild::getNormsId);
        // 设置是否下达退款单
        mealRefundOrderService.setWhetherMealRefundOrder(mealOrderChildList);
        outputObject.setBeans(mealOrderChildList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void updateStateISUseByOrderId(String orderId) {
        UpdateWrapper<MealOrderChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getOrderId), orderId);
        // 支付完成后，子订单改为“已支付，可使用”
        updateWrapper.set(MybatisPlusUtil.toColumns(MealOrderChild::getState), ShopMealOrderChildState.CAN_USE.getKey());
        update(updateWrapper);
    }

    @Override
    public void updateStateISNotUseById(String id) {
        UpdateWrapper<MealOrderChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        // 使用完当前子订单
        updateWrapper.set(MybatisPlusUtil.toColumns(MealOrderChild::getState), ShopMealOrderChildState.USED_UP.getKey());
        update(updateWrapper);
    }

    @Override
    public void queryMealMationByMaterial(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String materialId = params.get("materialId").toString();
        String normsId = params.get("normsId").toString();
        String codeNum = params.get("codeNum").toString();

        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getMaterialId), materialId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getNormsId), normsId);
        if (StrUtil.isNotEmpty(codeNum)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getCodeNum), codeNum);
        }
        // 只查询“已支付，可使用”的子订单
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getState), ShopMealOrderChildState.CAN_USE.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(MealOrderChild::getStartTime));
        List<MealOrderChild> mealOrderChildList = list(queryWrapper);
        shopMealService.setDataMation(mealOrderChildList, MealOrderChild::getMealId);
        // 产品信息
        iMaterialService.setDataMation(mealOrderChildList, MealOrderChild::getMaterialId);
        // 规格信息
        iMaterialNormsService.setDataMation(mealOrderChildList, MealOrderChild::getNormsId);

        // 按套餐使用方式（次数 / 年限）过滤可用记录
        List<MealOrderChild> result = mealOrderChildList.stream().filter(bean -> {
            ShopMeal meal = bean.getMealMation();
            if (ObjectUtil.isEmpty(meal) || meal.getUseType() == null) {
                return true;
            }
            if (ObjectUtil.equal(meal.getUseType(), ShopMealUseType.BY_YEAR.getKey())) {
                // 按年限：开始/结束时间以“yyyy-MM-dd”存储，按日期比较
                String today = DateUtil.getYmdTimeAndToString();
                String start = bean.getStartTime();
                String end = bean.getEndTime();
                if (StrUtil.isNotEmpty(start) && today.compareTo(start) < 0) {
                    return false;
                }
                if (StrUtil.isNotEmpty(end) && today.compareTo(end) > 0) {
                    return false;
                }
            }
            // 按次数（BY_NUM）暂时只依赖 state=ENABLE_USING，由前端在实际核销时调用 updateStateISNotUseById 控制
            return true;
        }).collect(Collectors.toList());

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void updateStateRefundById(String id) {
        UpdateWrapper<MealOrderChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(MealOrderChild::getState), ShopMealOrderChildState.REFUNDED.getKey());
        update(updateWrapper);
    }

    /**
     * 批量将已过期的“按年限计算”的套餐子订单置为已过期（不可再用）
     */
    @Override
    public void expireYearLimitMealOrders() {
        // 只处理按年限的套餐子订单
        QueryWrapper<ShopMeal> mealQw = new QueryWrapper<>();
        mealQw.eq(MybatisPlusUtil.toColumns(ShopMeal::getUseType), ShopMealUseType.BY_YEAR.getKey());
        List<ShopMeal> yearMeals = shopMealService.list(mealQw);
        if (CollectionUtil.isEmpty(yearMeals)) {
            return;
        }
        List<String> mealIds = yearMeals.stream().map(ShopMeal::getId).collect(Collectors.toList());
        // 当前日期（yyyy-MM-dd），和 start_time / end_time 格式一致
        String today = DateUtil.getYmdTimeAndToString();

        UpdateWrapper<MealOrderChild> uw = new UpdateWrapper<>();
        uw.in(MybatisPlusUtil.toColumns(MealOrderChild::getMealId), mealIds);
        uw.eq(MybatisPlusUtil.toColumns(MealOrderChild::getState), ShopMealOrderChildState.CAN_USE.getKey());
        uw.isNotNull(MybatisPlusUtil.toColumns(MealOrderChild::getEndTime));
        // 结束日期小于今天的认为已过期（end < today）
        uw.lt(MybatisPlusUtil.toColumns(MealOrderChild::getEndTime), today);
        // 状态改为已过期
        uw.set(MybatisPlusUtil.toColumns(MealOrderChild::getState), ShopMealOrderChildState.EXPIRED.getKey());
        update(uw);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void consumeMealOrderChild(String id, Integer useNum) {
        if (StrUtil.isEmpty(id)) {
            throw new CustomException("套餐子订单id不能为空");
        }
        int inc = useNum == null ? 1 : useNum;
        if (inc <= 0) {
            throw new CustomException("使用次数必须大于0");
        }
        MealOrderChild child = selectById(id);
        if (ObjectUtil.isEmpty(child) || StrUtil.isEmpty(child.getId())) {
            throw new CustomException("套餐子订单不存在");
        }
        if (!ObjectUtil.equal(child.getState(), ShopMealOrderChildState.CAN_USE.getKey())) {
            throw new CustomException("该套餐子订单状态不可用，无法核销");
        }

        ShopMeal meal = shopMealService.selectById(child.getMealId());
        if (ObjectUtil.isEmpty(meal) || StrUtil.isEmpty(meal.getId())) {
            throw new CustomException("套餐不存在");
        }
        // 按年限：校验未过期（日期 yyyy-MM-dd）
        if (ObjectUtil.equal(meal.getUseType(), ShopMealUseType.BY_YEAR.getKey())) {
            String today = DateUtil.getYmdTimeAndToString();
            if (StrUtil.isNotEmpty(child.getStartTime()) && today.compareTo(child.getStartTime()) < 0) {
                throw new CustomException("套餐尚未到开始日期，无法核销");
            }
            if (StrUtil.isNotEmpty(child.getEndTime()) && today.compareTo(child.getEndTime()) > 0) {
                throw new CustomException("套餐已过期，无法核销");
            }
            // 年限套餐也记录使用次数（可选）
        }

        Integer currentUse = child.getUseNum() == null ? 0 : child.getUseNum();
        int nextUse = currentUse + inc;

        // 按次数：不能超过套餐可用次数
        if (ObjectUtil.equal(meal.getUseType(), ShopMealUseType.BY_NUM.getKey())) {
            Integer limit = meal.getMealNum() == null ? 0 : meal.getMealNum();
            if (limit <= 0) {
                throw new CustomException("套餐可用次数配置错误");
            }
            if (nextUse > limit) {
                throw new CustomException("套餐可用次数不足，无法核销");
            }
        }

        UpdateWrapper<MealOrderChild> uw = new UpdateWrapper<>();
        uw.eq(CommonConstants.ID, id);
        uw.set(MybatisPlusUtil.toColumns(MealOrderChild::getUseNum), nextUse);

        // 按次数：用完则置为已用完
        if (ObjectUtil.equal(meal.getUseType(), ShopMealUseType.BY_NUM.getKey())
            && meal.getMealNum() != null
            && nextUse >= meal.getMealNum()) {
            uw.set(MybatisPlusUtil.toColumns(MealOrderChild::getState), ShopMealOrderChildState.USED_UP.getKey());
        }
        update(uw);
    }

    @Override
    public void checkMealOrderChildCanConsume(String id, String objectId, String materialId, String normsId, String codeNum, Integer useNum) {
        if (StrUtil.isEmpty(id)) {
            throw new CustomException("套餐子订单id不能为空");
        }
        int inc = useNum == null ? 1 : useNum;
        if (inc <= 0) {
            throw new CustomException("使用次数必须大于0");
        }
        MealOrderChild child = selectById(id);
        if (ObjectUtil.isEmpty(child) || StrUtil.isEmpty(child.getId())) {
            throw new CustomException("套餐子订单不存在");
        }
        if (!ObjectUtil.equal(child.getState(), ShopMealOrderChildState.CAN_USE.getKey())) {
            throw new CustomException("该套餐子订单状态不可用");
        }
        if (StrUtil.isNotEmpty(objectId) && !StrUtil.equals(objectId, child.getObjectId())) {
            throw new CustomException("该套餐不属于当前会员");
        }
        if (StrUtil.isNotEmpty(materialId) && !StrUtil.equals(materialId, child.getMaterialId())) {
            throw new CustomException("该套餐不适用于当前商品");
        }
        if (StrUtil.isNotEmpty(normsId) && !StrUtil.equals(normsId, child.getNormsId())) {
            throw new CustomException("该套餐不适用于当前规格");
        }
        if (StrUtil.isNotEmpty(codeNum) && !StrUtil.equals(codeNum, child.getCodeNum())) {
            throw new CustomException("该套餐不适用于当前条码");
        }

        ShopMeal meal = shopMealService.selectById(child.getMealId());
        if (ObjectUtil.isEmpty(meal) || StrUtil.isEmpty(meal.getId())) {
            throw new CustomException("套餐不存在");
        }

        // 按年限：校验未过期（日期 yyyy-MM-dd）
        if (ObjectUtil.equal(meal.getUseType(), ShopMealUseType.BY_YEAR.getKey())) {
            String today = DateUtil.getYmdTimeAndToString();
            if (StrUtil.isNotEmpty(child.getStartTime()) && today.compareTo(child.getStartTime()) < 0) {
                throw new CustomException("套餐尚未到开始日期");
            }
            if (StrUtil.isNotEmpty(child.getEndTime()) && today.compareTo(child.getEndTime()) > 0) {
                throw new CustomException("套餐已过期");
            }
            return;
        }

        // 按次数：校验剩余次数
        if (ObjectUtil.equal(meal.getUseType(), ShopMealUseType.BY_NUM.getKey())) {
            Integer limit = meal.getMealNum() == null ? 0 : meal.getMealNum();
            if (limit <= 0) {
                throw new CustomException("套餐可用次数配置错误");
            }
            Integer currentUse = child.getUseNum() == null ? 0 : child.getUseNum();
            if (currentUse + inc > limit) {
                throw new CustomException("套餐可用次数不足");
            }
        }
    }

}
