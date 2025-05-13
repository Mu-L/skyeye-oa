/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.trip.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.trip.dao.BusinessTripTimeSlotDao;
import com.skyeye.trip.entity.BusinessTripTimeSlot;
import com.skyeye.trip.service.BusinessTripTimeSlotService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: BusinessTripTimeSlotServiceImpl
 * @Description: 出差申请出差时间段服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 10:48
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "出差时间段", groupName = "出差申请", manageShow = false)
public class BusinessTripTimeSlotServiceImpl extends SkyeyeLinkDataServiceImpl<BusinessTripTimeSlotDao, BusinessTripTimeSlot> implements BusinessTripTimeSlotService {

    @Override
    public Map<String, List<BusinessTripTimeSlot>> queryBusinessTripTimeSlotListByBusinessTripIds(List<String> allBusinessTripIds) {
        if (CollectionUtil.isEmpty(allBusinessTripIds)) {
            return new HashMap<>();
        }

        QueryWrapper<BusinessTripTimeSlot> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(BusinessTripTimeSlot::getParentId), allBusinessTripIds);
        Map<String, List<BusinessTripTimeSlot>> stringListMap = list(queryWrapper).stream()
            .collect(Collectors.groupingBy(BusinessTripTimeSlot::getParentId));
        return stringListMap;
    }
}
