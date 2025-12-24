/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.serviceitems.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.serviceitems.dao.ServiceItemsDao;
import com.skyeye.serviceitems.entity.ServiceItems;
import com.skyeye.serviceitems.service.ServiceItemsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ServiceItemsServiceImpl
 * @Description: 售后服务项目服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "售后服务项目", groupName = "售后服务项目")
public class ServiceItemsServiceImpl extends SkyeyeBusinessServiceImpl<ServiceItemsDao, ServiceItems> implements ServiceItemsService {

    @Override
    public void queryEnabledServiceItemsList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ServiceItems> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceItems::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ServiceItems::getCreateTime));
        List<ServiceItems> itemsList = list(queryWrapper);
        outputObject.setBeans(itemsList);
        outputObject.settotal(itemsList.size());
    }

    @Override
    public void incrementSalesVolume(String id) {
        if (StrUtil.isEmpty(id)) {
            throw new CustomException("服务项目id不能为空");
        }

        // 查询当前服务项目
        ServiceItems serviceItems = selectById(id);
        if (serviceItems == null) {
            throw new CustomException("服务项目不存在");
        }

        // 获取当前销量，如果为空则默认为"0"
        String currentVolume = StrUtil.isEmpty(serviceItems.getSalesVolume()) ? "0" : serviceItems.getSalesVolume();

        // 使用工具函数进行+1计算（整数，0位小数）
        String newVolume = CalculationUtil.add(CommonNumConstants.NUM_ZERO, currentVolume, "1");

        // 更新销量
        UpdateWrapper<ServiceItems> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ServiceItems::getId), id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ServiceItems::getSalesVolume), newVolume);
        update(updateWrapper);

        // 清除缓存
        clearCache(id);
    }

}

