package com.skyeye.feeapplication.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.feeapplication.entity.FeeAnalysis;
import com.skyeye.feeapplication.entity.FeeApplication;

import java.util.List;

/**
 * @ClassName: FeeApplicationService
 * @Description: 费用申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FeeApplicationService extends SkyeyeFlowableService<FeeApplication> {
    void queryFeeApplicationAnalysis(InputObject inputObject, OutputObject outputObject);

    List<FeeApplication> queryFeeApplicationListByYear(int year);

    void queryDepartmentFeeAnalysis(InputObject inputObject, OutputObject outputObject);
}
