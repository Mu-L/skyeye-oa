/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.overtime.entity.OverTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: OvertimeDao
 * @Description: 加班申请数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/8 22:29
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface OvertimeDao extends SkyeyeBaseMapper<OverTime> {

    @IgnoreTenant
    List<Map<String, Object>> queryOvertimeList(CommonPageInfo pageInfo);

    /**
     * 获取指定员工在指定天是否有审批通过的数据
     *
     * @param createId    创建人
     * @param overtimeDay 指定天
     * @param childState  子对象状态
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryPassThisDayAndCreateId(@Param("createId") String createId,
                                                          @Param("overtimeDay") String overtimeDay,
                                                          @Param("childState") String childState,
                                                          @Param("tenantId") String tenantId);

    /**
     * 获取指定员工在指定月份的所有审核通过的加班申请数据
     *
     * @param userId 用户id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(@Param("userId") String userId,
                                                                                  @Param("months") List<String> months,
                                                                                  @Param("tenantId") String tenantId);

    /**
     * 获取所有待结算的加班数据
     *
     * @param childState 子对象状态
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryCheckWorkOvertimeWaitSettlement(@Param("childState") String childState,
                                                                   @Param("tenantId") String tenantId);

}
