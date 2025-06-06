/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.product.entity.ProductLeadChild;
import com.skyeye.production.dao.ProductionChildDao;
import com.skyeye.production.entity.ProductionChild;
import com.skyeye.production.service.ProductionChildService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ProductionChildServiceImpl
 * @Description: 生产计划单子单据服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/28 21:25
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "生产计划单子单据", groupName = "生产计划单管理", manageShow = false)
public class ProductionChildServiceImpl extends SkyeyeBusinessServiceImpl<ProductionChildDao, ProductionChild> implements ProductionChildService {

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<ProductionChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductionChild::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<ProductionChild> selectByParentId(String parentId) {
        QueryWrapper<ProductionChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProductionChild::getParentId), parentId);
        List<ProductionChild> productionChildList = list(queryWrapper);
        return productionChildList;
    }

    @Override
    public List<ProductionChild> selectByParentId(List<String> parentIds) {
        QueryWrapper<ProductionChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ProductionChild::getParentId), parentIds);
        List<ProductionChild> productionChildList = list(queryWrapper);
        return productionChildList;
    }

    @Override
    public void saveList(String parentId, List<ProductionChild> productionChildList) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(productionChildList)) {
            for (ProductionChild productionChild : productionChildList) {
                productionChild.setParentId(parentId);
            }
            createEntity(productionChildList, StrUtil.EMPTY);
        }
    }

}
