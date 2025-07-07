package com.skyeye.receivepayment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.receivepayment.entity.FundAnalysis;

/**
 * @ClassName: FundAnalysisService
 * @Description: 资金分析管理接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FundAnalysisService extends SkyeyeBusinessService<FundAnalysis> {
    void writeFundAnalysisRecord(String tenantId);

    void queryFundPercentage(InputObject inputObject, OutputObject outputObject);

    void queryFundTypePercentage(InputObject inputObject, OutputObject outputObject);

    void queryFundMetrics(InputObject inputObject, OutputObject outputObject);
}
