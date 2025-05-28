package com.skyeye.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.business.service.impl.SkyeyeErpOrderServiceImpl;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.product.dao.ProductLeadPutDao;
import com.skyeye.product.entity.ProductLeadPut;
import com.skyeye.product.service.ProductLeadPutService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "借出出库", groupName = "借出出库")
public class ProductLeadPutServiceImpl extends SkyeyeErpOrderServiceImpl<ProductLeadPutDao, ProductLeadPut> implements ProductLeadPutService {

    @Override
    public ProductLeadPut queryLendPutByHolderId(String holderId) {
        QueryWrapper<ProductLeadPut> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductLeadPut::getHolderId), holderId);
        queryWrapper.ne(MybatisPlusUtil.toColumns(ProductLeadPut::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.ne(MybatisPlusUtil.toColumns(ProductLeadPut::getState), FlowableStateEnum.IN_EXAMINE.getKey());
        ProductLeadPut productLeadPut = getOne(queryWrapper);
        return productLeadPut;
    }
}
