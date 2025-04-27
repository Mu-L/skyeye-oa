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
import com.skyeye.tenant.dao.TenantAppBuyOrderYearDao;
import com.skyeye.tenant.entity.TenantAppBuyOrderYear;
import com.skyeye.tenant.service.TenantAppBuyOrderYearService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: TenantAppBuyOrderYearServiceImpl
 * @Description: 订单-购买应用年限管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 22:13
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "订单-购买应用年限管理", groupName = "租户管理", manageShow = false, tenant = TenantEnum.PLATE)
public class TenantAppBuyOrderYearServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppBuyOrderYearDao, TenantAppBuyOrderYear> implements TenantAppBuyOrderYearService {

    @Override
    public void saveList(String parentId, List<TenantAppBuyOrderYear> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (TenantAppBuyOrderYear tenantAppBuyOrderYear : beans) {
                tenantAppBuyOrderYear.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<TenantAppBuyOrderYear> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrderYear::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<TenantAppBuyOrderYear> selectByParentId(String parentId) {
        QueryWrapper<TenantAppBuyOrderYear> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppBuyOrderYear::getParentId), parentId);
        List<TenantAppBuyOrderYear> list = list(queryWrapper);
        return list;
    }
}
