/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.product.classenum.AutoProductState;
import com.skyeye.product.dao.AutoProductDao;
import com.skyeye.product.entity.AutoProduct;
import com.skyeye.product.service.AutoProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoProductServiceImpl
 * @Description: 产品管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "产品管理", groupName = "产品管理")
public class AutoProductServiceImpl extends SkyeyeBusinessServiceImpl<AutoProductDao, AutoProduct> implements AutoProductService {

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoProductList(commonPageInfo);
        return beans;
    }

    @Override
    public void createPrepose(AutoProduct entity) {
        entity.setState(AutoProductState.NEW.getKey());
    }

    @Override
    public void queryAllAutoProductList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<AutoProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoProduct::getState), AutoProductState.PROGRESS.getKey());
        List<AutoProduct> autoProductList = list(queryWrapper);
        outputObject.setBeans(autoProductList);
        outputObject.settotal(autoProductList.size());
    }

    @Override
    public void editAutoProductToProgressById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        UpdateWrapper<AutoProduct> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AutoProduct::getState), AutoProductState.PROGRESS.getKey());
        update(updateWrapper);
        refreshCache(id);
    }
}
