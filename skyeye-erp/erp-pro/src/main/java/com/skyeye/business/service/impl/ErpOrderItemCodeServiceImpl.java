/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.business.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.service.ErpOrderItemCodeService;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dao.ErpOrderItemCodeDao;
import com.skyeye.entity.ErpOrderItemCode;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: ErpOrderItemCodeServiceImpl
 * @Description: 单据子表关联的条形码编号服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/5 19:04
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ErpOrderItemCodeServiceImpl extends SkyeyeBusinessServiceImpl<ErpOrderItemCodeDao, ErpOrderItemCode> implements ErpOrderItemCodeService {

    @Override
    public void saveList(String parentId, List<ErpOrderItemCode> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (ErpOrderItemCode erpOrderItemCode : beans) {
                erpOrderItemCode.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<ErpOrderItemCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ErpOrderItemCode::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<ErpOrderItemCode> selectByParentId(String... parentId) {
        List<String> parentIdList = Arrays.asList(parentId);
        if (CollectionUtil.isEmpty(parentIdList)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<ErpOrderItemCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderItemCode::getParentId), parentIdList);
        List<ErpOrderItemCode> list = list(queryWrapper);
        return list;
    }

    @Override
    public List<ErpOrderItemCode> selectByNormsCode(String... normsCodes) {
        List<String> normsCodeList = Arrays.asList(normsCodes);
        if (CollectionUtil.isEmpty(normsCodeList)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<ErpOrderItemCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ErpOrderItemCode::getParentId), normsCodeList);
        List<ErpOrderItemCode> list = list(queryWrapper);
        return list;
    }
}
