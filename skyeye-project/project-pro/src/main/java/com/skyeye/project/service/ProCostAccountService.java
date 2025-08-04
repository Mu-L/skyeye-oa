package com.skyeye.project.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.project.entity.CostAccount;

/**
 * @ClassName: ProCostAccountService
 * @Description: 成本核算管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/1 16:24
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface ProCostAccountService extends SkyeyeBusinessService<CostAccount> {
    void writeCostAccountRecord(String tenantId);

    void queryProCostAccountList(InputObject inputObject, OutputObject outputObject);

    void queryCostAccountViews(InputObject inputObject, OutputObject outputObject);

    void queryAllProCostAccountList(InputObject inputObject, OutputObject outputObject);
}
