/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service;

import com.skyeye.afterseal.entity.SealSign;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.Map;

/**
 * @ClassName: SealSignService
 * @Description: 工人签到报工信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealSignService extends SkyeyeBusinessService<SealSign> {

    /**
     * 报工：更新工时信息，状态改为"待审核"
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void reportWork(InputObject inputObject, OutputObject outputObject);

    /**
     * 审核：更新审核信息，状态改为"已通过"或"已驳回"
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void auditSign(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取指定时间范围内所有已审核通过的签到报工的总工时（转换为小时）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 总工时（小时）的字符串
     */
    String getAllFinishedWorkHours(String startTime, String endTime);

    /**
     * 获取指定时间范围内各用户已审核通过的签到报工的总工时（转换为小时）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 按用户ID分组的工时（小时）Map
     */
    Map<String, String> getAllFinishedWorkHoursByUserId(String startTime, String endTime);

}
