/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.delivery.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.adsense.entity.Adsense;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.delivery.dao.ShopDeliveryTemplateChargeDao;
import com.skyeye.delivery.entity.ShopDeliveryTemplate;
import com.skyeye.delivery.entity.ShopDeliveryTemplateCharge;
import com.skyeye.delivery.service.ShopDeliveryTemplateChargeService;
import com.skyeye.delivery.service.ShopDeliveryTemplateService;
import com.skyeye.exception.CustomException;
import com.skyeye.store.entity.ShopArea;
import com.skyeye.store.service.ShopAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopDeliveryTemplateChargeServiceImpl
 * @Description: 快递运费模板计费配置信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "快递运费模板计费配置信息管理", groupName = "快递运费模板计费配置信息管理")
public class ShopDeliveryTemplateChargeServiceImpl extends SkyeyeBusinessServiceImpl<ShopDeliveryTemplateChargeDao, ShopDeliveryTemplateCharge> implements ShopDeliveryTemplateChargeService {

    @Autowired
    private ShopDeliveryTemplateService shopDeliveryTemplateService;

    @Autowired
    private ShopAreaService shopAreaService;

    @Override
    public QueryWrapper<ShopDeliveryTemplateCharge> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ShopDeliveryTemplateCharge> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String objectStr =  commonPageInfo.getObjectId();
        if (StrUtil.isNotEmpty(objectStr)) {
            queryWrapper.like(MybatisPlusUtil.toColumns(ShopDeliveryTemplateCharge::getTemplateId), objectStr);
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<ShopDeliveryTemplateCharge> queryWrapper = new QueryWrapper<>();
        List<ShopDeliveryTemplateCharge> beans = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }

    @Override
    public void validatorEntity(ShopDeliveryTemplateCharge shopDeliveryTemplateCharge) {
        super.validatorEntity(shopDeliveryTemplateCharge);
        ShopDeliveryTemplate shopDeliveryTemplate = shopDeliveryTemplateService.selectById(shopDeliveryTemplateCharge.getTemplateId());
        ShopArea shopArea = shopAreaService.selectById(shopDeliveryTemplateCharge.getAreaId());

        // 判断shopDeliveryTemplate是否为空，如果为空则抛出异常
        if (StrUtil.isEmpty(shopDeliveryTemplate.getId())) {
            throw new CustomException("模板不存在: " + shopDeliveryTemplateCharge.getTemplateId());
        }

        // 判断shopArea是否为空，如果为空则抛出异常
        if (StrUtil.isEmpty(shopArea.getId())) {
            throw new CustomException("区域不存在: " + shopDeliveryTemplateCharge.getAreaId());
        }
    }
}
