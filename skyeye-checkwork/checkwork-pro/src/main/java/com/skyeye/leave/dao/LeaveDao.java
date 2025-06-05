/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.leave.entity.Leave;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LeaveDao
 * @Description: 请假申请数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 17:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LeaveDao extends SkyeyeBaseMapper<Leave> {

    @IgnoreTenant
    List<Map<String, Object>> queryMyCheckWorkLeaveList(CommonPageInfo pageInfo);

    /**
     * 获取指定日期已经审核通过的信息
     *
     * @param timeId   班次id
     * @param createId 创建人
     * @param leaveDay 指定日期,格式为：yyyy-MM-dd
     * @return
     */
    @IgnoreTenant
    Map<String, Object> queryCheckWorkLeaveByMation(@Param("timeId") String timeId,
                                                    @Param("createId") String createId,
                                                    @Param("leaveDay") String leaveDay,
                                                    @Param("tenantId") String tenantId);

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryStateIsSuccessLeaveDayByUserIdAndMonths(@Param("userId") String userId,
                                                                           @Param("timeId") String timeId,
                                                                           @Param("months") List<String> months,
                                                                           @Param("tenantId") String tenantId);

}
