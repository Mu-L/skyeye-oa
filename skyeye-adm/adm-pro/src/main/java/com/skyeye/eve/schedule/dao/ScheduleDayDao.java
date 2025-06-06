/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.schedule.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.schedule.entity.ScheduleDay;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ScheduleDayDao
 * @Description: 日程管理数据交互层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/1 11:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ScheduleDayDao extends SkyeyeBaseMapper<ScheduleDay> {

    List<Map<String, Object>> queryScheduleDayMationByUserId(@Param("userId") String userId,
                                                             @Param("list") List<String> months,
                                                             @Param("tenantId") String tenantId);

    Map<String, Object> queryIsnullThistime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Map<String, Object>> queryScheduleList(CommonPageInfo commonPageInfo);

    List<Map<String, Object>> queryAllUserAndEmailISNotNull();

    List<Map<String, Object>> queryMyAgencyList(CommonPageInfo pageInfo);
}
