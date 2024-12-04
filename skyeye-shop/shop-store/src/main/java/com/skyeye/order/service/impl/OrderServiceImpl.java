/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.entity.CouponUse;
import com.skyeye.coupon.entity.CouponUseMaterial;
import com.skyeye.coupon.enums.PromotionDiscountType;
import com.skyeye.coupon.enums.PromotionMaterialScope;
import com.skyeye.coupon.service.CouponUseMaterialService;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.config.PayProperties;
import com.skyeye.order.dao.OrderDao;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.ShopOrderCancelType;
import com.skyeye.order.enums.ShopOrderCommentState;
import com.skyeye.order.enums.ShopOrderState;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.skyeye.rest.pay.service.IPayService;
import com.skyeye.store.entity.ShopAddress;
import com.skyeye.store.service.ShopAddressService;
import com.xxl.job.core.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: OrderServiceImpl
 * @Description: 商品订单管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品订单管理", groupName = "商品订单管理")
public class OrderServiceImpl extends SkyeyeBusinessServiceImpl<OrderDao, Order> implements OrderService {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private CouponUseService couponUseService;

    @Autowired
    private IPayService iPayService;

    @Autowired
    private ShopAddressService shopAddressService;

    @Autowired
    private PayProperties payProperties;

    @Autowired
    private CouponUseMaterialService couponUseMaterialService;

    @Autowired
    private IQuartzService iQuartzService;

    @Override
    public void createPrepose(Order order) {
        // 订单编号
        Map<String, Object> business = BeanUtil.beanToMap(order);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        order.setOddNumber(oddNumber);
        order.setCount(CommonNumConstants.NUM_ZERO);// 商品总数
        order.setCommentState(ShopOrderCommentState.UNFINISHED.getKey());// 评价状态
        order.setTotalPrice("0");
        order.setDiscountPrice("0");
        order.setDeliveryPrice("0");
        order.setPayPrice("0");
        // 收货人信息
        ShopAddress shopAddress = shopAddressService.selectById(order.getAddressId());
        order.setReceiverName(shopAddress.getName());
        order.setReceiverMobile(shopAddress.getMobile());
        // 调价
        order.setAdjustPrice("0");
        // 子单的优惠券操作
        checkAndSetItemCouponUse(order);
        // ip
        order.setUserIp(IpUtil.getLocalAddress().toString());
        order.setState(ShopOrderState.UNPAID.getKey());
        //  物流联通后，此项需要修改
        checkAndSetDeliveryPrice(order);
        // 积分操作方法， 此方法未进行任何操作，可对此方法进行任何操作
        checkAndSetVariable(order);
        // 活动信息及积分操作方法
        checkAndSetActive(order);
        // 总单的优惠券处理
        checkAndSetOrderCouponUse(order);
        refreshCache(order.getId());
    }

    private void checkAndSetItemCouponUse(Order order) {// 子单的优惠券操作
        List<OrderItem> orderItemList = order.getOrderItemList();
        checkCouponUseMaterial(order);//  将总单的couponUserId赋值到对应子单
        // 设置商品信息、商品规格信息和优惠券信息
        iMaterialNormsService.setDataMation(orderItemList, OrderItem::getNormsId);
        iMaterialService.setDataMation(orderItemList, OrderItem::getMaterialId);
        for (OrderItem orderItem : orderItemList) {
            // 获取子单单价  元 -> 分
            String salePrice = CalculationUtil.multiply(orderItem.getNormsMation().get("salePrice").toString(), "100");
            // 设置子单总价
            orderItem.setPrice(CalculationUtil.multiply(orderItem.getCount(), salePrice));
            if (StrUtil.isEmpty(orderItem.getCouponUseId())) {// 没有优惠券
                orderItem.setPayPrice(orderItem.getPrice());
                orderItem.setDiscountPrice("0");
                setLastValue(order, orderItem);// 总单商品数量、子单状态、总单原价、总单应付金额
                continue;
            }
            // 获取优惠券使用条件，即满多少金额可使用。
            String usePrice = orderItem.getCouponUseMation().get("usePrice").toString();
            if (CalculationUtil.getMax(orderItem.getPrice(), usePrice, CommonNumConstants.NUM_SIX).equals(usePrice)) {
                throw new CustomException("优惠券不满足使用金额");
            }
            // 获取折扣类型
            Integer discountType = (Integer) (orderItem.getCouponUseMation().get("discountType"));
            if (Objects.equals(PromotionDiscountType.PRICE.getKey(), discountType)) {// 满减
                // 取出折扣价格
                String discountPrice = orderItem.getCouponUseMation().get("discountPrice").toString();
                // 折后价
                String afterPrice = CalculationUtil.subtract(orderItem.getPrice(), discountPrice, CommonNumConstants.NUM_SIX);
                orderItem.setPayPrice(afterPrice);
                orderItem.setCouponPrice(discountPrice);
            } else {//百分比折扣
                // 取出折扣
                String discountPercentInt = orderItem.getCouponUseMation().get("discountPercent").toString();
                // 百分比的折后价
                String percentPrice = CalculationUtil.multiply(orderItem.getPrice(), discountPercentInt, CommonNumConstants.NUM_SIX);
                // 百分比折扣的优惠价格
                String percentDiscountPrice = CalculationUtil.subtract(orderItem.getPrice(), percentPrice, CommonNumConstants.NUM_SIX);
                // 折扣上限
                String discountLimitPrice = orderItem.getCouponUseMation().get("discountLimitPrice").toString();
                // 折扣上限的折后价
                String limitPrice = CalculationUtil.multiply(orderItem.getPrice(), discountLimitPrice, CommonNumConstants.NUM_SIX);
                // 是否超过折扣上限
                boolean priceCompare = CalculationUtil.getMax(percentDiscountPrice, discountLimitPrice, CommonNumConstants.NUM_SIX).equals(percentDiscountPrice);
                // 设置应支付���格和优惠价格
                if (priceCompare) { // 未超过优惠价
                    orderItem.setPayPrice(percentPrice);
                    orderItem.setCouponPrice(percentDiscountPrice);
                } else {// 超过优惠价
                    orderItem.setPayPrice(limitPrice);
                    orderItem.setCouponPrice(discountLimitPrice);
                }
            }
            setLastValue(order, orderItem);// 总单商品数量、子单状态、总单原价、总单应付金额
        }
    }

    public void setLastValue(Order order, OrderItem orderItem) {
        order.setCount(order.getCount() + orderItem.getCount());
        orderItem.setCommentState(ShopOrderCommentState.UNFINISHED.getKey());
        order.setTotalPrice(CalculationUtil.add(order.getTotalPrice(), orderItem.getPrice(), CommonNumConstants.NUM_SIX));
        order.setPayPrice(CalculationUtil.add(order.getPayPrice(), orderItem.getPayPrice(), CommonNumConstants.NUM_SIX));
    }

    private void checkAndSetOrderCouponUse(Order order) {// 总单的优惠券处理

    }

    private void checkAndSetDeliveryPrice(Order order) {
        order.setDeliveryPrice(StrUtil.isEmpty(order.getDeliveryPrice()) ? "0" : order.getDeliveryPrice());
    }

    private void checkAndSetVariable(Order order) {
    }

    private void checkAndSetActive(Order order) {
    }

    private void checkCouponUseMaterial(Order order) {
        String couponUseId = order.getCouponUseId();
        if (StrUtil.isEmpty(couponUseId)) {
            return;
        }
        List<OrderItem> orderItemList = order.getOrderItemList();
        CouponUse couponUse = couponUseService.selectById(couponUseId);
        OrderItem orderItem = null;
        if (Objects.equals(couponUse.getProductScope(), PromotionMaterialScope.ALL.getKey())) {// 全部商品
            if (Objects.equals(couponUse.getDiscountType(), PromotionDiscountType.PERCENT.getKey())) {// 百分比折扣
                orderItem = orderItemList.stream().max(Comparator.comparing(OrderItem::getPrice)).orElse(null);// 获取优惠券使用商品列表中，价格最高的商品
            } else {// 满减   将优惠券使用到第一个商品
                orderItemList.get(0).setCouponUseId(couponUseId);
                orderItemList.get(0).setCouponUseMation(JSONUtil.toBean(JSONUtil.toJsonStr(couponUse), null));// 设置mation方便后续计算价格
                return;
            }
        } else if (Objects.equals(couponUse.getProductScope(), PromotionMaterialScope.SPU.getKey())) {// 指定商品
            List<String> couponUseMaterialIds = couponUseMaterialService.queryListByCouponIds(Collections.singletonList(couponUseId))
                .stream().map(CouponUseMaterial::getMaterialId).collect(Collectors.toList());// 收集子单商品id
            List<OrderItem> newOrderItemList = new ArrayList<>();
            for (OrderItem item : orderItemList) {
                if (couponUseMaterialIds.contains(item.getMaterialId())) {
                    newOrderItemList.add(item);
                }
            }
            orderItem = newOrderItemList.stream().max(Comparator.comparing(OrderItem::getPrice)).orElse(null);// 获取优惠券使用商品列表中，价格最高的商品
        }
        if (orderItem == null) {
            throw new CustomException("优惠券没有匹配的商品");
        }
        for (OrderItem item : orderItemList) {
            if (item.getId().equals(orderItem.getId())) {
                item.setCouponUseId(couponUseId);
                item.setCouponUseMation(JSONUtil.toBean(JSONUtil.toJsonStr(couponUse), null));// 设置mation方便后续计算价格
            }
        }
    }

    @Override
    public void createPostpose(Order order, String userId) {
        orderItemService.setValueAndCreateEntity(order, userId);
        couponUseService.updateState(order.getCouponUseId());// 更新用户领取的优惠券状态
//        startUpTaskQuartz(order.getId(), order.getOddNumber(), DateUtil.getTimeAndToString());
    }

    private void startUpTaskQuartz(String name, String title, String delayedTime) {
        /// 处理日期  此处delayedTime为当前日期
        Date stringToDate = DateUtil.getPointTime(delayedTime, DateUtil.YYYY_MM_DD_HH_MM_SS);
        Date afterOneDay = DateUtil.getAfDate(stringToDate, 1, "d");
        DateFormat df = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
        String lastTime = df.format(afterOneDay);
        // 正式准备启动定时任务
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(lastTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.SHOP_ORDER_CREATE.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Integer> stateList = new ArrayList<>();
        switch (commonPageInfo.getType()) {
            case "1": // 未支付
                stateList = Arrays.asList(new Integer[]{ShopOrderState.UNPAID.getKey()});
                break;
            case "2": // 待收货
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.UNDELIVERED.getKey(),// 待发货
                    ShopOrderState.DELIVERED.getKey(), //  已发货
                    ShopOrderState.TRANSPORTING.getKey()});//运输中
                break;
            case "3":// 已完成
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.SIGN.getKey(),       // 已签收
                    ShopOrderState.COMPLETED.getKey(),  // 已完成
                    ShopOrderState.UNEVALUATE.getKey(), // 待评价
                    ShopOrderState.EVALUATED.getKey()});// 已评价
                break;
            case "4":// 已取消
                stateList = Arrays.asList(new Integer[]{ShopOrderState.CANCELED.getKey()});
                break;
            case "5":// 处理中
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.REFUNDING.getKey(),  // 退款中

                    ShopOrderState.SALESRETURNING.getKey(),//退货中

                    ShopOrderState.EXCHANGEING.getKey()});//换货中
                break;
            case "6": // 申请记录
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.REFUND.getKey(),     // 已退款
                    ShopOrderState.SALESRETURNED.getKey(),//已退货
                    ShopOrderState.EXCHANGED.getKey()});//已换货
        }
        QueryWrapper<Order> wrapper = super.getQueryWrapper(commonPageInfo);
        if (CollectionUtil.isNotEmpty(stateList)) { // 状态列表为空时，则查询全部订单
            wrapper.in(MybatisPlusUtil.toColumns(Order::getState), stateList);
        }
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(Order::getCreateTime));
        List<Order> list = list(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return CollectionUtil.newArrayList();
        }
        List<String> idList = list.stream().map(Order::getId).collect(Collectors.toList());
        Map<String, List<OrderItem>> mapByIds = orderItemService.queryListByParentId(idList);
        for (Order order : list) {
            order.setOrderItemList(mapByIds.containsKey(order.getId()) ? mapByIds.get(order.getId()) : new ArrayList<>());
            pennyToYuan(order);// 分 -> 元
        }
        iAreaService.setDataMation(list, Order::getProvinceId);
        iAreaService.setDataMation(list, Order::getCityId);
        iAreaService.setDataMation(list, Order::getAreaId);
        iAreaService.setDataMation(list, Order::getTownshipId);
        shopAddressService.setDataMation(list, Order::getAddressId);
        // 分页查询时获取数据
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }

    @Override
    public void queryOrderPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Integer> stateList = new ArrayList<>();
        switch (commonPageInfo.getType()) {
            case "1": // 未支付
                stateList = Arrays.asList(new Integer[]{ShopOrderState.UNPAID.getKey()});
                break;
            case "2": // 待收货
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.UNDELIVERED.getKey(),// 待发货
                    ShopOrderState.DELIVERED.getKey(), //  已发货
                    ShopOrderState.TRANSPORTING.getKey()});//运输中
                break;
            case "3":// 已完成
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.SIGN.getKey(),       // 已签收
                    ShopOrderState.COMPLETED.getKey(),  // 已完成
                    ShopOrderState.UNEVALUATE.getKey(), // 待评价
                    ShopOrderState.EVALUATED.getKey()});// 已评价
                break;
            case "4":// 已取消
                stateList = Arrays.asList(new Integer[]{ShopOrderState.CANCELED.getKey()});
                break;
            case "5":// 处理中
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.REFUNDING.getKey(),  // 退款中
                    ShopOrderState.SALESRETURNING.getKey(),//退货中
                    ShopOrderState.EXCHANGEING.getKey()});//换货中
                break;
            case "6": // 申请记录
                stateList = Arrays.asList(new Integer[]{
                    ShopOrderState.REFUND.getKey(),     // 已退款
                    ShopOrderState.SALESRETURNED.getKey(),//已退货
                    ShopOrderState.EXCHANGED.getKey()});//已换货
        }
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if (CollectionUtil.isNotEmpty(stateList)) { // 状态列表为空时，则查询全部订单
            wrapper.in(MybatisPlusUtil.toColumns(Order::getState), stateList);
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        wrapper.eq(MybatisPlusUtil.toColumns(Order::getCreateId), userId);// 查询自己的订单
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(Order::getCreateTime));
        List<Order> list = list(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> idList = list.stream().map(Order::getId).collect(Collectors.toList());
        Map<String, List<OrderItem>> mapByIds = orderItemService.queryListByParentId(idList);
        for (Order order : list) {
            order.setOrderItemList(mapByIds.containsKey(order.getId()) ? mapByIds.get(order.getId()) : new ArrayList<>());
            pennyToYuan(order);// 分 -> 元
        }
        iAreaService.setDataMation(list, Order::getProvinceId);
        iAreaService.setDataMation(list, Order::getCityId);
        iAreaService.setDataMation(list, Order::getAreaId);
        iAreaService.setDataMation(list, Order::getTownshipId);
        shopAddressService.setDataMation(list, Order::getAddressId);
        outputObject.setBeans(JSONUtil.toList(JSONUtil.toJsonStr(list), null));
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void changeOrderAdjustPrice(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        int adjustPrice = Integer.parseInt(params.get("adjustPrice").toString());
        if (adjustPrice < CommonNumConstants.NUM_ZERO) {
            throw new CustomException("所调价格不可为负数");
        }
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.get("id").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getAdjustPrice), adjustPrice);
        update(updateWrapper);
        refreshCache(params.get("id").toString());
    }

    @Override
    public void updateOrderToPayState(InputObject inputObject, OutputObject outputObject) {
        String orderId = inputObject.getParams().get("id").toString();
        if (StrUtil.isEmpty(orderId)) {
            return;
        }
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, orderId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.UNDELIVERED.getKey());
        refreshCache(orderId);
    }

    @Override
    public void deletePostpose(List<String> ids) {
        orderItemService.deleteByPerentIds(ids);
    }

    @Override
    public Order selectById(String id) {
        Order order = super.selectById(id);
        Map<String, List<OrderItem>> orderItemList = orderItemService.queryListByParentId(Collections.singletonList(id));
        order.setOrderItemList(orderItemList.get(order.getId()));
        iAreaService.setDataMation(order, Order::getProvinceId);
        iAreaService.setDataMation(order, Order::getCityId);
        iAreaService.setDataMation(order, Order::getAreaId);
        iAreaService.setDataMation(order, Order::getTownshipId);
        shopAddressService.setDataMation(order, Order::getAddressId);
        pennyToYuan(order);// 分 -> 元
        return order;
    }

    private void pennyToYuan(Order order) {// 分 -> 元
        if (ObjectUtil.isEmpty(order)) {
            return;
        }
        order.setTotalPrice(StrUtil.isEmpty(order.getTotalPrice()) ? "0" : CalculationUtil.divide(order.getTotalPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setDiscountPrice(StrUtil.isEmpty(order.getDiscountPrice()) ? "0" : CalculationUtil.divide(order.getDiscountPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setDeliveryPrice(StrUtil.isEmpty(order.getDeliveryPrice()) ? "0" : CalculationUtil.divide(order.getDeliveryPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setAdjustPrice(StrUtil.isEmpty(order.getAdjustPrice()) ? "0" : CalculationUtil.divide(order.getAdjustPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setPayPrice(StrUtil.isEmpty(order.getPayPrice()) ? "0" : CalculationUtil.divide(order.getPayPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setCouponPrice(StrUtil.isEmpty(order.getCouponPrice()) ? "0" : CalculationUtil.divide(order.getCouponPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setPointPrice(StrUtil.isEmpty(order.getPointPrice()) ? "0" : CalculationUtil.divide(order.getPointPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setVipPrice(StrUtil.isEmpty(order.getVipPrice()) ? "0" : CalculationUtil.divide(order.getVipPrice(), "100", CommonNumConstants.NUM_SIX));
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setPrice(StrUtil.isEmpty(orderItem.getPrice()) ? "0" : CalculationUtil.divide(orderItem.getPrice(), "100", CommonNumConstants.NUM_SIX));
            orderItem.setDiscountPrice(StrUtil.isEmpty(orderItem.getDiscountPrice()) ? "0" : CalculationUtil.divide(orderItem.getDiscountPrice(), "100", CommonNumConstants.NUM_SIX));
            orderItem.setDeliveryPrice(StrUtil.isEmpty(orderItem.getDeliveryPrice()) ? "0" : CalculationUtil.divide(orderItem.getDeliveryPrice(), "100", CommonNumConstants.NUM_SIX));
            orderItem.setAdjustPrice(StrUtil.isEmpty(orderItem.getAdjustPrice()) ? "0" : CalculationUtil.divide(orderItem.getAdjustPrice(), "100", CommonNumConstants.NUM_SIX));
            orderItem.setPayPrice(StrUtil.isEmpty(orderItem.getPayPrice()) ? "0" : CalculationUtil.divide(orderItem.getPayPrice(), "100", CommonNumConstants.NUM_SIX));
            orderItem.setCouponPrice(StrUtil.isEmpty(orderItem.getCouponPrice()) ? "0" : CalculationUtil.divide(orderItem.getCouponPrice(), "100", CommonNumConstants.NUM_SIX));
            orderItem.setPointPrice(StrUtil.isEmpty(orderItem.getPointPrice()) ? "0" : CalculationUtil.divide(orderItem.getPointPrice(), "100", CommonNumConstants.NUM_SIX));
            orderItem.setVipPrice(StrUtil.isEmpty(orderItem.getVipPrice()) ? "0" : CalculationUtil.divide(order.getVipPrice(), "100", CommonNumConstants.NUM_SIX));
        }
    }

    @Override
    public void cancelOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.get("id"));
        Order one = getOne(updateWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        // 可取消的订单状态：未提交(0)、已提交(1)、待支付(2)、待发货(5)
        if (Objects.equals(one.getState(), ShopOrderState.UNSUBMIT.getKey()) ||
            Objects.equals(one.getState(), ShopOrderState.SUBMIT.getKey()) ||
            Objects.equals(one.getState(), ShopOrderState.UNPAID.getKey()) ||
            Objects.equals(one.getState(), ShopOrderState.UNDELIVERED.getKey())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.CANCELED.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelType), params.get("cancelType"));
            updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            refreshCache(params.get("id").toString());
        } else {
            throw new CustomException("订单不可取消");
        }
    }

    @Override
    public void finishOrder(InputObject inputObject, OutputObject outputObject) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, inputObject.getParams().get("id"));
        Order one = getOne(updateWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        if (one.getState() < ShopOrderState.SIGN.getKey() && one.getState() > ShopOrderState.REFUNDING.getKey()) {
            throw new CustomException("不可完成订单。");
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.COMPLETED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getFinishTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getReceiveTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        refreshCache(one.getId());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void payOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String channelCode = params.get("channelCode").toString();
        String channelExtras = params.get("channelExtras").toString();
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        Order one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        if (!Objects.equals(one.getState(), ShopOrderState.UNPAID.getKey())) {
            throw new CustomException("该订单不可支付。");
        }
        Map<String, Object> payRresult = iPayService.payment(BeanUtil.beanToMap(one), channelCode, "", channelExtras, payProperties.getOrderNotifyUrl()).getBean();
        Map<String, Object> payChannel = JSONUtil.toBean(payRresult.get("payChannel").toString(), null);
        Map<String, Object> payOrderRespDTO = JSONUtil.toBean(payRresult.get("payOrderRespDTO").toString(), null);
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.UNDELIVERED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayType), channelCode);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayTime), payOrderRespDTO.get("successTime").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getChannelFeeRate), payChannel.get("feeRate").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getChannelFeePrice), CalculationUtil.multiply(
            one.getPayPrice(), payChannel.get("feeRate").toString()));
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getExtensionId), payOrderRespDTO.get("id").toString());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getExtensionNo), payOrderRespDTO.get("no").toString());
        update(updateWrapper);
        refreshCache(id);
        iQuartzService.stopAndDeleteTaskQuartz(id);// 删除定时任务
    }

    @Override
    public void deliverGoodsByOrderId(InputObject inputObject, OutputObject outputObject) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, inputObject.getParams().get("id"));
        Order one = getOne(updateWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        if (!Objects.equals(one.getState(), ShopOrderState.UNDELIVERED.getKey())) {
            throw new CustomException("该订单当前不可发货。");
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.DELIVERED.getKey());
        update(updateWrapper);
        refreshCache(one.getId());
    }

    @Override
    public void updateCommonState(String id, Integer state) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCommentState), state);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void generatePayOrderRrCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String channelCode = params.get("channelCode").toString();
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        Order one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        if (!Objects.equals(one.getState(), ShopOrderState.UNPAID.getKey())) {
            throw new CustomException("该订单不可支付。");
        }
        Map<String, Object> qrCodeResult = iPayService.generatePayRrCode(BeanUtil.beanToMap(one), channelCode, IpUtil.getLocalAddress().toString(), payProperties.getOrderNotifyUrl());
        outputObject.setBean(qrCodeResult);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void setOrderCancle(String orderId) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, orderId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.CANCELED.getKey())
            .set(MybatisPlusUtil.toColumns(Order::getCancelType), ShopOrderCancelType.PAY_TIMEOUT.getKey())
            .set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
        refreshCache(orderId);
    }
}