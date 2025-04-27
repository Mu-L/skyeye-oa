/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.TenantAppBuyOrderNumDao;
import com.skyeye.tenant.entity.TenantAppBuyOrderNum;
import com.skyeye.tenant.service.TenantAppBuyOrderNumService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: TenantAppBuyOrderNumServiceImpl
 * @Description: 订单-购买租户数量管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/30 16:18
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "订单-购买租户数量管理", groupName = "租户管理", manageShow = false, tenant = TenantEnum.PLATE)
public class TenantAppBuyOrderNumServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppBuyOrderNumDao, TenantAppBuyOrderNum> implements TenantAppBuyOrderNumService {

    @Override
    public void saveList(String parentId, List<TenantAppBuyOrderNum> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (TenantAppBuyOrderNum tenantAppBuyOrderNum : beans) {
                tenantAppBuyOrderNum.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<TenantAppBuyOrderNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrderNum::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<TenantAppBuyOrderNum> selectByParentId(String parentId) {
        QueryWrapper<TenantAppBuyOrderNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrderNum::getParentId), parentId);
        List<TenantAppBuyOrderNum> list = list(queryWrapper);
        return list;
    }

}
