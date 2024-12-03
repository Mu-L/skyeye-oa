/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

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
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.order.dao.OrderCommentDao;
import com.skyeye.order.entity.OrderComment;
import com.skyeye.order.entity.OrderItem;
import com.skyeye.order.enums.OrderCommentType;
import com.skyeye.order.enums.ShopOrderCommentState;
import com.skyeye.order.service.OrderCommentService;
import com.skyeye.order.service.OrderItemService;
import com.skyeye.order.service.OrderService;
import com.skyeye.service.MemberService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OrderCommentServiceImpl
 * @Description: 商品订单评价管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商品订单评价管理", groupName = "商品订单评价管理")
public class OrderCommentServiceImpl extends SkyeyeBusinessServiceImpl<OrderCommentDao, OrderComment> implements OrderCommentService {

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Override
    public void validatorEntity(OrderComment orderComment) {
        Integer commentType = orderComment.getType();
        if (commentType != OrderCommentType.MERCHANT.getKey()
            && commentType != OrderCommentType.CUSTOMERLATER.getKey()
            && commentType != OrderCommentType.CUSTOMERFiRST.getKey()) {
            throw new CustomException("type值非法");
        }
        if (commentType == OrderCommentType.MERCHANT.getKey() ||
            commentType == OrderCommentType.CUSTOMERLATER.getKey()) {
            if (StrUtil.isEmpty(orderComment.getParentId())) {
                throw new CustomException("商家回复评价和客户追评，父级评价id不能为空.");
            }
        }
        if (commentType == OrderCommentType.CUSTOMERFiRST.getKey()) {
            if (StrUtil.isNotEmpty(orderComment.getParentId())) {
                throw new CustomException("客户的评价无需父级id");
            }
        }
    }

    @Override
    public void createPrepose(OrderComment entity) {
        OrderItem orderItem = orderItemService.selectById(entity.getOrderItemId());
        // 客户评价判断
        if (orderItem.getCommentState() == WhetherEnum.DISABLE_USING.getKey()) {// 子订单未评价
            if (entity.getType() == OrderCommentType.CUSTOMERLATER.getKey()) {
                throw new CustomException("客户追评，需先进行首评。");
            }
            if (entity.getType() == OrderCommentType.CUSTOMERFiRST.getKey()) {// 客户首评
                Integer start = entity.getStart();
                if (ObjectUtil.isEmpty(entity.getStart())) {
                    throw new CustomException("首评的星级不能为空");
                }
                if (start < 0 || start > 5) {
                    throw new CustomException("评价星级为1-5");
                }
            }
        } else if (orderItem.getCommentState() == WhetherEnum.ENABLE_USING.getKey()) {// 子订单已评价
            if (entity.getType() == OrderCommentType.CUSTOMERFiRST.getKey()) {// 再次进行首评
                throw new CustomException("首评只能评价一次。");
            }
            if (entity.getType() == OrderCommentType.CUSTOMERLATER.getKey()) {// 追评
                QueryWrapper<OrderComment> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(MybatisPlusUtil.toColumns(OrderComment::getOrderItemId), entity.getOrderItemId())
                    .eq(MybatisPlusUtil.toColumns(OrderComment::getCreateId), InputObject.getLogParamsStatic().get("id").toString())
                    .and(wrap -> {
                        String parentId = MybatisPlusUtil.toColumns(OrderComment::getParentId);
                        wrap.isNotNull(parentId).ne(parentId, StrUtil.EMPTY);
                    });
                OrderComment one = getOne(queryWrapper);
                if (ObjectUtil.isNotEmpty(one)) {// 客户已追评
                    throw new CustomException("追评只能追评一次");
                }
                entity.setStart(null);
            }
        }
        entity.setStoreId(ObjectUtil.isEmpty(orderItem) ? "" : orderItem.getStoreId());// 设置门店id
        if (entity.getType() == OrderCommentType.CUSTOMERFiRST.getKey() ||
            entity.getType() == OrderCommentType.CUSTOMERLATER.getKey()) {// 顾客新增的评价，商家均未回复
            entity.setIsComment(WhetherEnum.DISABLE_USING.getKey());
        }
    }

    @Override
    public void createPostpose(OrderComment orderComment, String userId) {
        List<OrderItem> orderItemList = orderItemService.queryListByStateAndOrderId(orderComment.getOrderId(), WhetherEnum.DISABLE_USING.getKey());
        if (CollectionUtil.isNotEmpty(orderItemList)) {// 总订单的评价状态修改
            orderService.updateCommonState(orderComment.getOrderId(), ShopOrderCommentState.PORTION.getKey());
        } else {
            orderService.updateCommonState(orderComment.getOrderId(), ShopOrderCommentState.FINISHED.getKey());
        }
        orderItemService.updateCommentStateById(orderComment.getOrderItemId());
        if (orderComment.getType() == OrderCommentType.MERCHANT.getKey()) {// 商家回复时，修改客户评价状态为已评价
            UpdateWrapper<OrderComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, orderComment.getParentId());
            updateWrapper.set(MybatisPlusUtil.toColumns(OrderComment::getIsComment), WhetherEnum.ENABLE_USING.getKey());
            update(updateWrapper);
        }
    }

    @Override
    public OrderComment selectById(String id) {
        OrderComment orderComment = super.selectById(id);
        if (ObjectUtil.isEmpty(orderComment)) {
            throw new CustomException("信息不存在");
        }
        iMaterialService.setDataMation(orderComment, OrderComment::getMaterialId);
        iMaterialNormsService.setDataMation(orderComment, OrderComment::getNormsId);
        memberService.setDataMation(orderComment, OrderComment::getCreateId);
        shopStoreService.setDataMation(orderComment, OrderComment::getStoreId);
        return orderComment;
    }

    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> mapList = super.queryPageDataList(inputObject);
        iMaterialService.setMationForMap(mapList, "materialId", "materialMation");
        iMaterialNormsService.setMationForMap(mapList, "normsId", "normsMation");
        memberService.setMationForMap(mapList, "createId", "createMation");
        shopStoreService.setMationForMap(mapList, "storeId", "storeMation");
        return mapList;
    }

    @Override
    public QueryWrapper<OrderComment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        String typeId = commonPageInfo.getTypeId();
        QueryWrapper<OrderComment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.and(wrap -> {
            wrap.eq(MybatisPlusUtil.toColumns(OrderComment::getType), OrderCommentType.CUSTOMERFiRST.getKey())
                .or().eq(MybatisPlusUtil.toColumns(OrderComment::getType), OrderCommentType.CUSTOMERLATER.getKey());
        });
        if (StrUtil.isNotEmpty(typeId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(OrderComment::getStoreId), typeId);
        }
        return queryWrapper;
    }

    @Override
    public void queryOrderCommentPageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String typeId = commonPageInfo.getTypeId();
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<OrderComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderComment::getCreateId), typeId)// 创建人id
            .or().eq(MybatisPlusUtil.toColumns(OrderComment::getMaterialId), typeId) // 商品id
            .or().eq(MybatisPlusUtil.toColumns(OrderComment::getOrderItemId), typeId)// 订单子单id
            .or().eq(MybatisPlusUtil.toColumns(OrderComment::getOrderId), typeId);   // 订单id
        List<OrderComment> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        setAdditionalReviewAndMerchantReply(list);// 区分客户追评和商家回复
        iMaterialService.setDataMation(list, OrderComment::getMaterialId);
        iMaterialNormsService.setDataMation(list, OrderComment::getNormsId);
        memberService.setDataMation(list, OrderComment::getCreateId);
        shopStoreService.setDataMation(list, OrderComment::getStoreId);
        List<Map<String, Object>> mapList = JSONUtil.toList(JSONUtil.toJsonStr(list), null);
        outputObject.setBeans(mapList);
        outputObject.settotal(pages.getTotal());
    }

    private List<OrderComment> setAdditionalReviewAndMerchantReply(List<OrderComment> list) {
        List<OrderComment> customerFirstList = list.stream().filter(// 客户第一次评价
            item -> item.getType() == OrderCommentType.CUSTOMERFiRST.getKey()).collect(Collectors.toList());
        Map<String, OrderComment> customerTowMap = list.stream().filter(// 客户追评
            item -> item.getType() == OrderCommentType.CUSTOMERLATER.getKey()).collect(Collectors.toMap(OrderComment::getParentId, o -> o));
        Map<String, List<OrderComment>> merchantReplyMapList = list.stream().filter(// 商家回复
            item -> item.getType() == OrderCommentType.MERCHANT.getKey()).collect(Collectors.groupingBy(OrderComment::getParentId));
        for (OrderComment item : customerFirstList) {
            if (customerTowMap.containsKey(item.getId())) {
                item.setAdditionalReview(JSONUtil.toBean(JSONUtil.toJsonStr(customerTowMap.get(item.getId())), null));
            }
            if (merchantReplyMapList.containsKey(item.getId())) {
                item.setMerchantReply(JSONUtil.toBean(JSONUtil.toJsonStr(merchantReplyMapList.get(item.getId())), null));
            }
        }
        return customerFirstList;
    }
}
