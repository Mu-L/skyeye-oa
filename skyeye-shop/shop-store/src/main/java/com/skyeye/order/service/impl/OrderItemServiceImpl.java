/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.OrderItemDao;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderComment;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.OrderCommentType;
import com.skyeye.order.service.OrderCommentService;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OrderItemServiceImpl
 * @Description: 商品订单子单项管理--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品订单子单项管理", groupName = "商品订单子单项管理", tenant = TenantEnum.NO_ISOLATION)
public class OrderItemServiceImpl extends SkyeyeBusinessServiceImpl<OrderItemDao, OrderItem> implements OrderItemService {

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private OrderCommentService orderCommentService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private OrderService orderService;

    @Override
    public void deleteByPerentIds(List<String> ids) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(OrderItem::getParentId), ids);
        remove(queryWrapper);
    }

    @Override
    public List<OrderItem> queryListByStateAndOrderId(String orderId, Integer state) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), orderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getCommentState), state);
        List<OrderItem> list = list(queryWrapper);
        return CollectionUtil.isEmpty(list) ? new ArrayList<>() : list;
    }

    @Override
    public Map<String, List<OrderItem>> queryListByParentId(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return new HashMap<>();
        }
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(OrderItem::getParentId), idList);
        List<OrderItem> mapList = list(queryWrapper);
        if (CollectionUtil.isEmpty(mapList)) {
            return new HashMap<>();
        }
        List<String> orderItemIds = mapList.stream().map(OrderItem::getId).collect(Collectors.toList());
        List<OrderComment> orderCommentList = orderCommentService.queryListByOrderItemIdAndType(orderItemIds, OrderCommentType.CUSTOMERLATER.getKey());
        List<String> commentIdList = orderCommentList.stream().map(OrderComment::getOrderItemId).collect(Collectors.toList());
        for (OrderItem map : mapList) {
            if (commentIdList.contains(map.getId())) {
                map.setIsAdditionalReview(true);
            } else {
                map.setIsAdditionalReview(false);
            }
        }
        shopStoreService.setDataMation(mapList, OrderItem::getStoreId);
        iMaterialNormsService.setDataMation(mapList, OrderItem::getNormsId);
        List<String> materialStoreIds = mapList.stream().map(OrderItem::getMaterialStoreId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);// erp-shop-material 拿价钱logo
        Map<String, Map<String, Object>> materialStoreMap = materialByIds.stream()
            .distinct().collect(Collectors.toMap(map -> {
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                return shopMaterialStore.get("id").toString();
            }, map -> map));
        mapList.forEach(map -> {
            map.setShopMaterial(materialStoreMap.containsKey(map.getMaterialStoreId()) ? materialStoreMap.get(map.getMaterialStoreId()) : new HashMap<>());
        });
        Map<String, List<OrderItem>> result = mapList.stream().collect(Collectors.groupingBy(OrderItem::getParentId));
        return result;
    }

    @Override
    public void setValueAndCreateEntity(Order order, String userId) {
        List<String> materialStoreIds = order.getOrderItemList().stream().map(OrderItem::getMaterialStoreId).distinct().collect(Collectors.toList());
        // shopMaterial -> shopMaterialStore -> storeId
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);// erp-shop-material
        Map<String, String> materialStoreMap = materialByIds.stream()
            .distinct().collect(Collectors.toMap(map -> {
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                return shopMaterialStore.get("id").toString();
            }, map -> {
                Map<String, Object> shopMaterialStore = JSONUtil.toBean(map.get("shopMaterialStore").toString(), null);
                return shopMaterialStore.get("storeId").toString();
            }));
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setCommentState(WhetherEnum.DISABLE_USING.getKey());
            orderItem.setParentId(order.getId());
            orderItem.setStoreId(materialStoreMap.containsKey(orderItem.getMaterialStoreId()) ? materialStoreMap.get(orderItem.getMaterialStoreId()) : "");
        }
        super.createEntity(order.getOrderItemList(), userId);
    }

    @Override
    public void updateCommentStateById(String id) {
        UpdateWrapper<OrderItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id)
            .set(MybatisPlusUtil.toColumns(OrderItem::getCommentState), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public List<OrderItem> queryOrderItemByParentId(String orderId) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), orderId);
        return list(queryWrapper);
    }

    @Override
    public void UpdateOrderItemState(String orderItemId) {
        OrderItem orderItem = selectById(orderItemId);
        if (orderItem.getOrderItemState() == CommonNumConstants.NUM_TWO) {
            throw new CustomException("该订单已收货");
        }
        UpdateWrapper<OrderItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, orderItemId);
        updateWrapper.set(MybatisPlusUtil.toColumns(OrderItem::getOrderItemState), CommonNumConstants.NUM_TWO);
        update(updateWrapper);
    }
}