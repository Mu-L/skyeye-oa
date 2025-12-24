/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.abnormalmarking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.abnormalmarking.dao.ServiceAbnormalMarkingDao;
import com.skyeye.abnormalmarking.entity.ServiceAbnormalMarking;
import com.skyeye.abnormalmarking.service.ServiceAbnormalMarkingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ServiceAbnormalMarkingServiceImpl
 * @Description: 售后服务异常标记服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "售后服务异常标记", groupName = "售后服务异常标记")
public class ServiceAbnormalMarkingServiceImpl extends SkyeyeBusinessServiceImpl<ServiceAbnormalMarkingDao, ServiceAbnormalMarking> implements ServiceAbnormalMarkingService {

    @Override
    public void queryEnabledAbnormalMarkingList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ServiceAbnormalMarking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceAbnormalMarking::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ServiceAbnormalMarking::getCreateTime));
        List<ServiceAbnormalMarking> markingList = list(queryWrapper);
        outputObject.setBeans(markingList);
        outputObject.settotal(markingList.size());
    }

}

