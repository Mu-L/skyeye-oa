package com.skyeye.loan.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.loan.entity.LoanBorrowAnalysis;

/**
 * @ClassName: LoanBorrowAnalysisService
 * @Description: 借款单分析接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:16
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LoanBorrowAnalysisService extends SkyeyeBusinessService<LoanBorrowAnalysis> {
    void writeLoanBorrowAnalysisRecord();

    void queryLoanBorrowAnalysis(InputObject inputObject, OutputObject outputObject);
}
