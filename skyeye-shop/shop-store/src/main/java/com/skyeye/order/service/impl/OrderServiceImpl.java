package com.skyeye.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.coupon.enums.CouponUseState;
import com.skyeye.coupon.enums.PromotionDiscountType;
import com.skyeye.coupon.service.CouponUseService;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.eve.service.IAreaService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.OrderDao;
import com.skyeye.order.entity.Order;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.ShopOrderCommentState;
import com.skyeye.order.enums.ShopOrderState;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.xxl.job.core.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public void createPrepose(Order order) {
        // 订单编号
        Map<String, Object> business = BeanUtil.beanToMap(order);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        order.setOddNumber(oddNumber);
        order.setCount(CommonNumConstants.NUM_ZERO);
        order.setCommentState(ShopOrderCommentState.UNFINISHED.getKey());
        order.setTotalPrice("0");
        order.setDiscountPrice("0");
        order.setDeliveryPrice("0");
        order.setPayPrice("0");
        // 调价
        order.setAdjustPrice(StrUtil.isEmpty(order.getAdjustPrice()) ? "0" : order.getAdjustPrice());
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
    }

    private void checkAndSetItemCouponUse(Order order) {// 子单的优惠券操作
        List<OrderItem> orderItemList = order.getOrderItemList();
        // 设置商品信息、商品规格信息和优惠券信息
        iMaterialNormsService.setDataMation(orderItemList, OrderItem::getNormsId);
        iMaterialService.setDataMation(orderItemList, OrderItem::getMaterialId);
        couponUseService.setDataMation(orderItemList, OrderItem::getCouponId);
        boolean b = orderItemList.stream().filter(orderItem -> StrUtil.isNotEmpty(orderItem.getCouponId()))
            .allMatch(orderItem -> orderItem.getCouponMation().get("state").equals(CouponUseState.UNUSED.getKey()));
        if (!b) {
            throw new CustomException("存在不可用优惠券");
        }
        for (OrderItem orderItem : orderItemList) {
            // 获取子单单价
            String salePrice = orderItem.getNormsMation().get("salePrice").toString();
            // 设置子单总价
            orderItem.setPrice(CalculationUtil.multiply(orderItem.getCount(), salePrice));
            if (StrUtil.isEmpty(orderItem.getCouponId())) {// 没有优惠券
                orderItem.setPayPrice(orderItem.getPrice());
                orderItem.setDiscountPrice("0");
                continue;
            }
            // 获取优惠券使用条件，即满多少金额可使用。
            String usePrice = orderItem.getCouponMation().get("usePrice").toString();
            if (CalculationUtil.getMax(orderItem.getPrice(), usePrice, CommonNumConstants.NUM_SIX).equals(usePrice)) {
                throw new CustomException("优惠券不满足使用金额");
            }
            // 获取折扣类型
            Integer discountType = (Integer) (orderItem.getCouponMation().get("discountType"));
            if (Objects.equals(PromotionDiscountType.PRICE.getKey(), discountType)) {// 满减
                // 取出折扣价格
                String discountPrice = orderItem.getCouponMation().get("discountPrice").toString();
                // 折后价
                String afterPrice = CalculationUtil.subtract(orderItem.getPrice(), discountPrice, CommonNumConstants.NUM_SIX);
                orderItem.setPayPrice(afterPrice);
                orderItem.setDiscountPrice(discountPrice);
            } else {//百分比折扣
                // 取出折扣
                String discountPercentInt = orderItem.getCouponMation().get("discountPercent").toString();
                // 百分比的折后价
                String percentPrice = CalculationUtil.multiply(orderItem.getPrice(), discountPercentInt, CommonNumConstants.NUM_SIX);
                // 百分比折扣的优惠价格
                String percentDiscountPrice = CalculationUtil.subtract(orderItem.getPrice(), percentPrice, CommonNumConstants.NUM_SIX);
                // 折扣上限
                String discountLimitPrice = orderItem.getCouponMation().get("discountLimitPrice").toString();
                // 折扣上限的折后价
                String limitPrice = CalculationUtil.multiply(orderItem.getPrice(), discountLimitPrice, CommonNumConstants.NUM_SIX);
                // 是否超过折扣上限
                boolean priceCompare = CalculationUtil.getMax(percentDiscountPrice, discountLimitPrice, CommonNumConstants.NUM_SIX).equals(percentDiscountPrice);
                // 设置应支付价格和优惠价格
                if (priceCompare) { // 未超过优惠价
                    orderItem.setPayPrice(percentPrice);
                    orderItem.setDiscountPrice(percentDiscountPrice);
                } else {// 超过优惠价
                    orderItem.setPayPrice(limitPrice);
                    orderItem.setDiscountPrice(discountLimitPrice);
                }
            }
            order.setCount(order.getCount() + orderItem.getCount());
            orderItem.setCommentState(ShopOrderCommentState.UNFINISHED.getKey());
            order.setTotalPrice(CalculationUtil.add(order.getTotalPrice(), orderItem.getPayPrice(), CommonNumConstants.NUM_SIX));
            order.setPayPrice(CalculationUtil.add(order.getPayPrice(), orderItem.getPayPrice(), CommonNumConstants.NUM_SIX));
        }
    }

    private void checkAndSetOrderCouponUse(Order order) {// 总单的优惠券处理
        if (StrUtil.isEmpty(order.getCouponId())) {
            return;
        }
        couponUseService.setDataMation(order, Order::getCouponId);
        Integer couponState = (Integer) (order.getCouponMation().get("state"));
        if (!Objects.equals(CouponUseState.UNUSED.getKey(), couponState)) {
            throw new CustomException("存在不可用优惠券");
        }
        // 获取总订单总价
        String totalPrice = order.getTotalPrice();
        // 获取优惠券使用条件，即满多少金额可使用。
        String usePrice = order.getCouponMation().get("usePrice").toString();
        if (CalculationUtil.getMax(totalPrice, usePrice, CommonNumConstants.NUM_SIX).equals(usePrice)) {
            throw new CustomException("优惠券不满足使用金额");
        }
        // 获取折扣类型
        Integer discountType = (Integer) (order.getCouponMation().get("discountType"));
        if (Objects.equals(PromotionDiscountType.PRICE.getKey(), discountType)) {// 满减
            // 取出折扣价格
            String discountPrice = order.getCouponMation().get("discountPrice").toString();
            // 折后价
            String afterPrice = CalculationUtil.subtract(totalPrice, discountPrice, CommonNumConstants.NUM_SIX);
            order.setPayPrice(afterPrice);
            // 优惠金额
            order.setDiscountPrice(CalculationUtil.add(order.getDiscountPrice(), discountPrice, CommonNumConstants.NUM_SIX));
        } else {//百分比折扣
            // 取出折扣
            String discountPercent = order.getCouponMation().get("discountPercent").toString();
            // 百分比的折后价
            String percentPrice = CalculationUtil.multiply(totalPrice, discountPercent, CommonNumConstants.NUM_SIX);
            // 百分比折扣的优惠价格
            String percentDiscountPrice = CalculationUtil.subtract(totalPrice, percentPrice, CommonNumConstants.NUM_SIX);
            // 折扣上限
            String discountLimitPrice = order.getCouponMation().get("discountLimitPrice").toString();
            // 折扣上限的折后价
            String limitPrice = CalculationUtil.multiply(totalPrice, discountLimitPrice, CommonNumConstants.NUM_SIX);
            // 是否超过折扣上限
            boolean priceCompare = CalculationUtil.getMax(percentDiscountPrice, discountLimitPrice, CommonNumConstants.NUM_SIX).equals(percentDiscountPrice);
            // 设置应支付价格和优惠价格
            if (priceCompare) { // 未超过优惠价
                order.setPayPrice(CalculationUtil.subtract(order.getPayPrice(), percentPrice, CommonNumConstants.NUM_SIX));
                order.setDiscountPrice(CalculationUtil.subtract(order.getDiscountPrice(), percentDiscountPrice, CommonNumConstants.NUM_SIX));
            } else {// 超过优惠价
                order.setPayPrice(CalculationUtil.subtract(order.getPayPrice(), limitPrice, CommonNumConstants.NUM_SIX));
                order.setDiscountPrice(CalculationUtil.subtract(order.getDiscountPrice(), discountLimitPrice, CommonNumConstants.NUM_SIX));
            }
        }
        order.setPayPrice(CalculationUtil.divide(order.getPayPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setDiscountPrice(CalculationUtil.divide(order.getDiscountPrice(), "100", CommonNumConstants.NUM_SIX));
        order.setTotalPrice(CalculationUtil.divide(order.getTotalPrice(), "100", CommonNumConstants.NUM_SIX));
    }

    private void checkAndSetDeliveryPrice(Order order) {
        order.setDeliveryPrice(StrUtil.isEmpty(order.getDeliveryPrice()) ? "0" : order.getDeliveryPrice());
    }

    private void checkAndSetVariable(Order order) {
    }

    private void checkAndSetActive(Order order) {
    }

    @Override
    public void createPostpose(Order order, String userId) {
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setCommentState(WhetherEnum.DISABLE_USING.getKey());
            orderItem.setParentId(order.getId());
        }
        orderItemService.createEntity(order.getOrderItemList(), userId);
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
        }
        iAreaService.setDataMation(list, Order::getProvinceId);
        iAreaService.setDataMation(list, Order::getCityId);
        iAreaService.setDataMation(list, Order::getAreaId);
        iAreaService.setDataMation(list, Order::getTownshipId);
        // 分页查询时获取数据
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
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
        return order;
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
        //  大于1和小于8
        if (!(one.getState() >= ShopOrderState.SIGN.getKey() && one.getState() <= ShopOrderState.SUBMIT.getKey())) {
            throw new CustomException("订单不可取消");
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.CANCELED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelType), params.get("cancelType"));
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCancelTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
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

    }

    @Override
    public void payOrder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, params.get("id"));
        Order one = getOne(updateWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("订单不存在");
        }
        if (!Objects.equals(one.getState(), ShopOrderState.UNPAID.getKey())) {
            throw new CustomException("该订单不可支付。");
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getState), ShopOrderState.UNDELIVERED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayType), params.get("payType"));
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getPayTime), DateUtil.getTimeAndToString());
        update(updateWrapper);
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
    }

    @Override
    public void updateCommonState(String id, Integer state) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(Order::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Order::getCommentState), state);
        update(updateWrapper);
    }
}
