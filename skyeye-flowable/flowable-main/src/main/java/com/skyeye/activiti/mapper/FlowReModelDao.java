/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.mapper;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;
import org.flowable.engine.impl.persistence.entity.ModelEntityImpl;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FlowModelDao
 * @Description: 原生工作流模型数据层
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/5 12:15
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FlowReModelDao {

    @IgnoreTenant
    List<ModelEntityImpl> getModelByIds(@Param("ids") List<String> ids, @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<ModelEntityImpl> getModelByIdsForTenant(@Param("list") List<Map<String, Object>> actFlowList);
}
