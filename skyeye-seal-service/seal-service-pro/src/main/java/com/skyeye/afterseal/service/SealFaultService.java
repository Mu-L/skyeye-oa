/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service;

import com.skyeye.afterseal.entity.SealFault;
import com.skyeye.base.business.service.SkyeyeBusinessService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealFaultService
 * @Description: 售后服务故障信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/12 17:39
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface SealFaultService extends SkyeyeBusinessService<SealFault> {

    /**
     * 获取所有已完工的故障信息的服务总时长
     */
    Double getAllFinishedServiceTime(String startTime, String endTime);

    Map<String, Double> getAllFinishedServiceTime(List<String> userIds, String startTime, String endTime);

}
