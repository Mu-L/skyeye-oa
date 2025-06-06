package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.product.dao.ProductLeadChildDao;
import com.skyeye.product.entity.ProductLeadChild;
import com.skyeye.product.service.ProductLeadChildService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@SkyeyeService(name = "借出出库申请-子单据", groupName = "借出出库申请", flowable = true)
public class ProductLeadChildServiceImpl extends SkyeyeBusinessServiceImpl<ProductLeadChildDao, ProductLeadChild> implements ProductLeadChildService {

    @Override
    public String calcOrderAllTotalPrice(List<ProductLeadChild> productLeadChildList) {
        String totalPrice = "0";
        for (ProductLeadChild productLeadChild : productLeadChildList) {
            // 计算子单据总价：单价 * 数量
            BigDecimal itemAllPrice = new BigDecimal(productLeadChild.getUnitPrice());
            itemAllPrice = itemAllPrice.multiply(new BigDecimal(productLeadChild.getNumber()));
            productLeadChild.setAllPrice(itemAllPrice.toString());
            totalPrice = new BigDecimal(totalPrice).add(itemAllPrice).toString();
        }
        return totalPrice;
    }

    @Override
    public void saveList(String parentId, List<ProductLeadChild> productLeadChildList) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(productLeadChildList)) {
            for (ProductLeadChild productLeadChild : productLeadChildList) {
                productLeadChild.setParentId(parentId);
            }
            createEntity(productLeadChildList, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String id) {
        QueryWrapper<ProductLeadChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadChild::getParentId), id);
        remove(queryWrapper);
    }

    @Override
    public List<ProductLeadChild> selectProductLeadChildById(String id) {
        QueryWrapper<ProductLeadChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadChild::getParentId), id);
        return list(queryWrapper);
    }

}
