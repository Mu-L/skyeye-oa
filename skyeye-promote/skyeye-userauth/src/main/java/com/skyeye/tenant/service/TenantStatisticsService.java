/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 租户统计分析服务接口：租户、用户、订单、邀请、应用等维度统计（共9个接口，便于报表布局）
 */
public interface TenantStatisticsService {

    /**
     * 1. 租户总数统计
     */
    void queryTenantTotal(InputObject inputObject, OutputObject outputObject);

    /**
     * 2. 租户用户总数统计（支持时间范围）
     */
    void queryTenantUserTotal(InputObject inputObject, OutputObject outputObject);

    /**
     * 3. 租户订单总数统计（支持时间范围）
     */
    void queryTenantOrderTotal(InputObject inputObject, OutputObject outputObject);

    /**
     * 4. 租户邀请总数统计（支持时间范围）
     */
    void queryTenantInviteTotal(InputObject inputObject, OutputObject outputObject);

    /**
     * 5. 租户应用总数统计
     */
    void queryTenantAppTotal(InputObject inputObject, OutputObject outputObject);

    /**
     * 6. 租户按创建时间趋势统计（按月，xAxisData + seriesData）
     */
    void queryTenantStatsByCreateTime(InputObject inputObject, OutputObject outputObject);

    /**
     * 7. 租户用户按在职状态统计（xAxisData + seriesData）
     */
    void queryTenantUserStatsByState(InputObject inputObject, OutputObject outputObject);

    /**
     * 8. 租户订单按租户统计（按 buyTenantId 分组，空归其他）
     */
    void queryTenantOrderStatsByTenant(InputObject inputObject, OutputObject outputObject);

    /**
     * 9. 租户邀请按是否使用统计（xAxisData + seriesData）
     */
    void queryTenantInviteStatsByUsed(InputObject inputObject, OutputObject outputObject);
}
