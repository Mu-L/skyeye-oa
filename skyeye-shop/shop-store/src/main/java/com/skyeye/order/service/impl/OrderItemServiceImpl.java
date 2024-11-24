package com.skyeye.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.order.dao.OrderItemDao;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "商品订单子单项管理", groupName = "商品订单子单项管理")
public class OrderItemServiceImpl extends SkyeyeBusinessServiceImpl<OrderItemDao, OrderItem> implements OrderItemService {

    @Autowired
    private IShopMaterialNormsService iShopMaterialNormsService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IMaterialService iMaterialService;

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
        shopStoreService.setDataMation(mapList, OrderItem::getStoreId);
        iMaterialNormsService.setDataMation(mapList, OrderItem::getNormsId);
        List<String> materialStoreIds = mapList.stream().map(OrderItem::getMaterialStoreId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);// erp-shop-material
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
}