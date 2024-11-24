/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    @Override
    public void validatorEntity(OrderComment orderComment) {
        if (orderComment.getType() == OrderCommentType.MERCHANT.getKey()) {
            if (StrUtil.isEmpty(orderComment.getParentId())) {
                throw new CustomException("商家回复评价，父级评价id不能为空.");
            }
        }
        if (orderComment.getType() == OrderCommentType.CUSTOMERFiRST.getKey() ||
            orderComment.getType() == OrderCommentType.CUSTOMERLATER.getKey()) {
            if (StrUtil.isNotEmpty(orderComment.getParentId())) {
                throw new CustomException("客户的评价无需父级id");
            }
        }
    }

    @Override
    public void createPostpose(OrderComment orderComment, String userId) {
        List<OrderItem> orderItemList = orderItemService.queryListByStateAndOrderId(orderComment.getOrderId(), WhetherEnum.DISABLE_USING.getKey());
        if (CollectionUtil.isNotEmpty(orderItemList)) {
            orderService.updateCommonState(orderComment.getOrderId(), ShopOrderCommentState.PORTION.getKey());
        } else {
            orderService.updateCommonState(orderComment.getOrderId(), ShopOrderCommentState.FINISHED.getKey());
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
        return orderComment;
    }

    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> mapList = super.queryPageDataList(inputObject);
        iMaterialService.setMationForMap(mapList, "materialId", "materialMation");
        iMaterialNormsService.setMationForMap(mapList, "normsId", "normsMation");
        return mapList;
    }

    @Override
    public QueryWrapper<OrderComment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        String typeId = commonPageInfo.getTypeId();
        QueryWrapper<OrderComment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(OrderComment::getCreateId), typeId)
            .or().eq(MybatisPlusUtil.toColumns(OrderComment::getMaterialId), typeId);
        return queryWrapper;
    }
}
