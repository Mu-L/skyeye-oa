/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.lifecycle.entity.LifecycleStateChangeHistory;

/**
 * @ClassName: LifecycleStateChangeHistoryService
 * @Description: 生命周期状态变更历史服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/15 10:03
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface LifecycleStateChangeHistoryService extends SkyeyeBusinessService<LifecycleStateChangeHistory> {

    void queryNewLifecycleStateChangeHistory(InputObject inputObject, OutputObject outputObject);

}
