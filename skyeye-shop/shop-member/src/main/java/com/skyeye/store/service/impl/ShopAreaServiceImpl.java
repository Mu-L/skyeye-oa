/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.store.dao.ShopAreaDao;
import com.skyeye.store.entity.ShopArea;
import com.skyeye.store.service.ShopAreaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ShopAreaServiceImpl
 * @Description: 区域管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:07
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "区域管理", groupName = "区域管理")
public class ShopAreaServiceImpl extends SkyeyeBusinessServiceImpl<ShopAreaDao, ShopArea> implements ShopAreaService {

    @Override
    public void queryAllEnabledAreaList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ShopArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopArea::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<ShopArea> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    @IgnoreTenant
    public ShopArea selectById(String id) {
        return super.selectById(id);
    }
}
