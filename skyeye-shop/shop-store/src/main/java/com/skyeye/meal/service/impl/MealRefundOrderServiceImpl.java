/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.flowable.classenum.FormSubType;
import com.skyeye.exception.CustomException;
import com.skyeye.meal.dao.MealRefundOrderDao;
import com.skyeye.meal.entity.MealOrderChild;
import com.skyeye.meal.entity.MealRefundOrder;
import com.skyeye.meal.service.MealOrderChildService;
import com.skyeye.meal.service.MealRefundOrderService;
import com.skyeye.service.MemberService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MealRefundOrderServiceImpl
 * @Description: 套餐退款订单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/12 9:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "套餐退款订单管理", groupName = "套餐退款订单管理", flowable = true)
public class MealRefundOrderServiceImpl extends SkyeyeBusinessServiceImpl<MealRefundOrderDao, MealRefundOrder> implements MealRefundOrderService {

    @Autowired
    private MealOrderChildService mealOrderChildService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private MemberService memberService;

    @Override
    public QueryWrapper<MealRefundOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MealRefundOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "Store")) {
            // 门店下的退款订单
            queryWrapper.eq(MybatisPlusUtil.toColumns(MealRefundOrder::getStoreId), commonPageInfo.getHolderId());
        } else if (StrUtil.equals(commonPageInfo.getType(), "My")) {
            // 我的退款订单
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(MealRefundOrder::getObjectId), userId);
        } else if (StrUtil.equals(commonPageInfo.getType(), "All")) {
            // 所有退款订单
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setNameForMap(beans, "refundReasonId", "refundReasonName");
        shopStoreService.setMationForMap(beans, "storeId", "storeMation");
        memberService.setMationForMap(beans, "objectId", "objectMation");
        return beans;
    }

    @Override
    public MealRefundOrder selectById(String id) {
        MealRefundOrder mealRefundOrder = super.selectById(id);
        iSysDictDataService.setDataMation(mealRefundOrder, MealRefundOrder::getRefundReasonId);
        // 会员信息
        memberService.setDataMation(mealRefundOrder, MealRefundOrder::getObjectId);
        // 门店信息
        shopStoreService.setDataMation(mealRefundOrder, MealRefundOrder::getStoreId);
        return mealRefundOrder;
    }

    /**
     * 会员套餐退款申请操作
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void refundMealOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mealOrderChildId = params.get("mealOrderChildId").toString();
        String mealRefundReasonId = params.get("mealRefundReasonId").toString();
        String refundPrice = params.get("refundPrice").toString();
        String storeId = params.get("storeId").toString();

        List<String> stateList = Arrays.asList(FlowableStateEnum.IN_EXAMINE.getKey(), FlowableStateEnum.PASS.getKey());
        QueryWrapper<MealRefundOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MealRefundOrder::getState), stateList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealRefundOrder::getMealOrderChildId), mealOrderChildId);
        List<MealRefundOrder> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new CustomException("该套餐存在待审核或已审核的退款申请，请勿重复申请.");
        }
        // 获取套餐订单子单据信息
        MealOrderChild mealOrderChild = mealOrderChildService.selectById(mealOrderChildId);
        if (ObjectUtil.isEmpty(mealOrderChild) || StrUtil.isEmpty(mealOrderChild.getId())) {
            outputObject.setreturnMessage("套餐单据信息为空，请确认.");
            return;
        }
        MealRefundOrder mealRefundOrder = new MealRefundOrder();
        mealRefundOrder.setMealOrderChildId(mealOrderChildId);
        mealRefundOrder.setRefundReasonId(mealRefundReasonId);
        mealRefundOrder.setRefundPrice(refundPrice);
        mealRefundOrder.setMealSinglePrice(CalculationUtil.subtract(mealOrderChild.getMealPrice(), refundPrice, CommonNumConstants.NUM_TWO));
        mealRefundOrder.setStoreId(storeId);
        mealRefundOrder.setFormSubType(FormSubType.DRAFT.getKey());
        mealRefundOrder.setObjectId(mealOrderChild.getObjectId());
        mealRefundOrder.setObjectKey(mealOrderChild.getObjectKey());
        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(mealRefundOrder, userId);
    }

    @Override
    public void setWhetherMealRefundOrder(List<MealOrderChild> mealOrderChildList) {
        if (CollectionUtil.isEmpty(mealOrderChildList)) {
            return;
        }
        List<String> mealOrderChildIds = mealOrderChildList.stream().map(MealOrderChild::getId).collect(Collectors.toList());
        QueryWrapper<MealRefundOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MealRefundOrder::getMealOrderChildId), mealOrderChildIds);
        List<MealRefundOrder> mealRefundOrderList = list(queryWrapper);
        if (CollectionUtil.isEmpty(mealRefundOrderList)) {
            return;
        }
        Map<String, MealRefundOrder> mealRefundOrderMap = mealRefundOrderList.stream().collect(Collectors.toMap(MealRefundOrder::getMealOrderChildId, e -> e));
        for (MealOrderChild mealOrderChild : mealOrderChildList) {
            if (ObjectUtil.isNotEmpty(mealRefundOrderMap.get(mealOrderChild.getId()))) {
                mealOrderChild.setIsRefund(true);
                mealOrderChild.setMealRefundOrder(mealRefundOrderMap.get(mealOrderChild.getId()));
            }
        }
    }

    @Override
    public void approvalEndIsSuccess(MealRefundOrder entity) {
        // 设置套餐订单子单据状态为不可使用
        mealOrderChildService.updateStateISNotUseById(entity.getMealOrderChildId());
    }

}
