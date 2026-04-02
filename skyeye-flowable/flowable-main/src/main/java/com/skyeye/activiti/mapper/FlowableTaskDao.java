/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.mapper;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FlowableTaskDao
 * @Description: 任务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/25 23:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FlowableTaskDao {

    /**
     * 查询待办任务
     *
     * @param pageInfo 参数
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> getApplyingTasks(CommonPageInfo pageInfo);

    @IgnoreTenant
    List<String> queryRunningProcessIdsByExecutionAssignee(@Param("userId") String userId, @Param("keyword") String keyword, @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<String> queryRunningProcessIdsByHostAssignee(@Param("userId") String userId, @Param("keyword") String keyword, @Param("tenantId") String tenantId);

    @IgnoreTenant
    String queryHostAssigneeByProcessInstanceId(@Param("processInstanceId") String processInstanceId, @Param("tenantId") String tenantId);

}
