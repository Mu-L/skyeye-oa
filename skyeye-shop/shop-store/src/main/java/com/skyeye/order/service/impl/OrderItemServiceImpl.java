package com.skyeye.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.order.dao.OrderItemDao;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.rest.shopmaterialnorms.sevice.IShopMaterialNormsService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public void deleteByPerentIds(List<String> ids) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(OrderItem::getParentId), ids);
        remove(queryWrapper);
    }

//    @Override
//    public List<OrderItem> selectByParentId(String parentId) {
//        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), parentId);
//        List<Map<String, Object>> mapList = listMaps(queryWrapper);
//        Map<String, Map<String, Object>> collect = mapList.stream().collect(Collectors.toMap(map -> map.get("id").toString(), map -> map));
//        List<String> materialStoreIds = mapList.stream().map(map -> map.get("materialStoreId").toString()).collect(Collectors.toList());
//        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);
//        for (Map<String, Object> map : materialByIds) {
//            collect.get(map.get("id").toString()).put("materialMation", map);
//        }
//        return BeanUtil.copyToList(mapList, OrderItem.class);
//    }

    @Override
    public List<OrderItem> queryListByStateAndOrderId(String orderId, Integer state) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getParentId), orderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderItem::getCommentState), state);
        List<OrderItem> list = list(queryWrapper);
        return CollectionUtil.isEmpty(list) ? new ArrayList<>() : list;
    }

    @Override
    public Map<String, List<OrderItem>> queryListByParentId(String... idList) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(OrderItem::getId), Arrays.asList(idList));
        List<OrderItem> mapList = list(queryWrapper);
        shopStoreService.setDataMation(mapList, OrderItem::getStoreId);
        List<String> materialStoreIds = mapList.stream().map(OrderItem::getMaterialStoreId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> materialByIds = iShopMaterialNormsService.queryShopMaterialByIds(materialStoreIds);
        Map<String, Map<String, Object>> materialMap = materialByIds.stream().collect(Collectors.toMap(map -> map.get("id").toString(), map -> map));
        mapList.forEach(map -> {
            map.setMaterialMation(materialMap.get(map.getMaterialStoreId()));
        });
        Map<String, List<OrderItem>> result = mapList.stream().collect(Collectors.groupingBy(OrderItem::getParentId));
        return result;
    }
}