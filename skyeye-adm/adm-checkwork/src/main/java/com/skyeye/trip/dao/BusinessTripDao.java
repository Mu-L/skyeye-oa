/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.trip.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.trip.entity.BusinessTrip;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: BusinessTripDao
 * @Description: 出差申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface BusinessTripDao extends SkyeyeBaseMapper<BusinessTrip> {

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的出差申请数据
     *
     * @param userId     用户id
     * @param timeId     班次id
     * @param months     指定月份，月格式（yyyy-MM）
     * @param childState 子对象状态
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryStateIsSuccessBusinessTripDay(@Param("userId") String userId,
                                                                 @Param("timeId") String timeId,
                                                                 @Param("months") List<String> months,
                                                                 @Param("childState") String childState,
                                                                 @Param("tenantId") String tenantId);
}
