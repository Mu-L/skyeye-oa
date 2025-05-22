package com.skyeye.product.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.product.dao.ProductLeadLinkCodeDao;
import com.skyeye.product.entity.ProductLeadLinkCode;
import com.skyeye.product.service.ProductLeadLinkCodeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@SkyeyeService(name = "借出出库子表关联的条形码编号", groupName = "借出出库子表关联的条形码编号", manageShow = false)
public class ProductLeadLinkCodeServiceImpl extends SkyeyeBusinessServiceImpl<ProductLeadLinkCodeDao, ProductLeadLinkCode> implements ProductLeadLinkCodeService {

    @Override
    public void saveList(String pId, List<ProductLeadLinkCode> productLeadLinkCodes) {
        deleteByParentId(pId);
        if (CollectionUtil.isNotEmpty(productLeadLinkCodes)) {
            for (ProductLeadLinkCode productLeadLinkCode : productLeadLinkCodes) {
                productLeadLinkCode.setParentId(pId);
            }
            createEntity(productLeadLinkCodes, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<ProductLeadLinkCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadLinkCode::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<ProductLeadLinkCode>> selectByParentIds(List<String> parentIds) {
        if (CollectionUtil.isEmpty(parentIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ProductLeadLinkCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ProductLeadLinkCode::getParentId), parentIds);
        List<ProductLeadLinkCode> list = list(queryWrapper);
        Map<String, List<ProductLeadLinkCode>> listMap = list.stream().collect(Collectors.groupingBy(ProductLeadLinkCode::getParentId));
        return listMap;
    }
}
