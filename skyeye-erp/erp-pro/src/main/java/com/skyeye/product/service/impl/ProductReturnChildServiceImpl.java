package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.product.dao.ProductReturnChildDao;
import com.skyeye.product.entity.ProductReturnChild;
import com.skyeye.product.service.ProductReturnChildService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@SkyeyeService(name = "归还入库申请-子单据", groupName = "归还入库申请", flowable = false)
public class ProductReturnChildServiceImpl extends SkyeyeBusinessServiceImpl<ProductReturnChildDao, ProductReturnChild> implements ProductReturnChildService {

    @Override
    public String calcOrderAllTotalPrice(List<ProductReturnChild> erpOrderItemList) {
        String totalPrice = "0";
        for (ProductReturnChild productReturnChild : erpOrderItemList) {
            // 计算子单据总价：单价 * 数量
            BigDecimal itemAllPrice = new BigDecimal(productReturnChild.getUnitPrice());
            itemAllPrice = itemAllPrice.multiply(new BigDecimal(productReturnChild.getOperNumber()));
            productReturnChild.setAllPrice(itemAllPrice.toString());
            totalPrice = new BigDecimal(totalPrice).add(itemAllPrice).toString();
        }
        return totalPrice;
    }

    @Override
    public void saveList(String parentId, List<ProductReturnChild> erpOrderItemList) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(erpOrderItemList)) {
            for (ProductReturnChild productReturnChild : erpOrderItemList) {
                productReturnChild.setParentId(parentId);
            }
            createEntity(erpOrderItemList, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String id) {
        QueryWrapper<ProductReturnChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductReturnChild::getParentId), id);
        remove(queryWrapper);
    }

    @Override
    public List<ProductReturnChild> selectProductLeadChildById(String id) {
        QueryWrapper<ProductReturnChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductReturnChild::getParentId), id);
        return list(queryWrapper);
    }

    @Override
    public List<ProductReturnChild> selectProductLeadChildByIdList(List<String> returnIds) {
        if (CollectionUtil.isEmpty(returnIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<ProductReturnChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ProductReturnChild::getParentId), returnIds);
        return list(queryWrapper);
    }
}
