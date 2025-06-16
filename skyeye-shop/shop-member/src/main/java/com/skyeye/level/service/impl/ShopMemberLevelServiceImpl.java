/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.level.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.level.dao.ShopMemberLevelDao;
import com.skyeye.level.entity.ShopMemberLevel;
import com.skyeye.level.service.ShopMemberLevelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopMemberLevelServiceImpl
 * @Description: 会员等级服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "会员等级", groupName = "会员等级", tenant = TenantEnum.PLATE)
public class ShopMemberLevelServiceImpl extends SkyeyeBusinessServiceImpl<ShopMemberLevelDao, ShopMemberLevel> implements ShopMemberLevelService {

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<ShopMemberLevel> beans = queryAllEnabledMemberLevel();
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }

    private List<ShopMemberLevel> queryAllEnabledMemberLevel() {
        QueryWrapper<ShopMemberLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMemberLevel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ShopMemberLevel::getLevel));
        List<ShopMemberLevel> beans = list(queryWrapper);
        return beans;
    }

    @Override
    @IgnoreTenant
    public ShopMemberLevel getMinLevel() {
        List<ShopMemberLevel> beans = queryAllEnabledMemberLevel();
        return beans.stream().findFirst().orElse(null);
    }

    @Override
    @IgnoreTenant
    public ShopMemberLevel selectById(String id) {
        return super.selectById(id);
    }

    @Override
    @IgnoreTenant
    public ShopMemberLevel getSimpleLevelByLevel(Integer level) {
        QueryWrapper<ShopMemberLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMemberLevel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopMemberLevel::getLevel), level);
        ShopMemberLevel bean = getOne(queryWrapper, false);
        return bean;
    }
}
