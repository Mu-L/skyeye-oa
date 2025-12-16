/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.enterprise.entity.UserEnterpriseApprovalHistory;

/**
 * @ClassName: UserEnterpriseApprovalHistoryService
 * @Description: 企业账号审批历史服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/16 8:59
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface UserEnterpriseApprovalHistoryService extends SkyeyeBusinessService<UserEnterpriseApprovalHistory> {

    void queryApprovalHistoryListByUserEnterpriseId(InputObject inputObject, OutputObject outputObject);
}
