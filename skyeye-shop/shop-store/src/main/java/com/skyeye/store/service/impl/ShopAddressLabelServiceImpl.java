/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.store.dao.ShopAddressLabelDao;
import com.skyeye.store.entity.ShopAddressLabel;
import com.skyeye.store.service.ShopAddressLabelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopAddressLabelServiceImpl
 * @Description: 收件地址标签管理服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/8 14:57
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "收件地址标签管理", groupName = "收件地址标签管理", tenant = TenantEnum.NO_ISOLATION)
public class ShopAddressLabelServiceImpl extends SkyeyeBusinessServiceImpl<ShopAddressLabelDao, ShopAddressLabel> implements ShopAddressLabelService {

    @Override
    public void validatorEntity(ShopAddressLabel shopAddressLabel) {
        super.validatorEntity(shopAddressLabel);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<ShopAddressLabel> queryWrapper = new QueryWrapper<>();
        //更新时排除本身
        if (StrUtil.isNotEmpty(shopAddressLabel.getId())) {
            queryWrapper.ne(CommonConstants.ID, shopAddressLabel.getId());
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddressLabel::getCreateId), userId);
        List<ShopAddressLabel> list = list(queryWrapper);
        //排除重复的标签
        for (ShopAddressLabel addressLabel : list) {
            if (addressLabel.getContent().equals(shopAddressLabel.getContent())) {
                throw new RuntimeException("标签重复");
            }
        }
    }

    @Override
    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<ShopAddressLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ShopAddressLabel::getCreateId), inputObject.getLogParams().get("id").toString());
        List<ShopAddressLabel> list = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }
}