package com.skyeye.loan.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.loan.service.LoanRepayAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LoanRepayAnalysisController
 * @Description: 还款单分析控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:16
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "还款单分析", tags = "还款单分析", modelName = "还款单分析")
public class LoanRepayAnalysisController {

    @Autowired
    private LoanRepayAnalysisService loanRepayAnalysisService;

    @ApiOperation(id = "queryLoanRepayAnalysis", value = "借款单趋势图", method = "POST", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "year", name = "year", value = "年", required = "required"),
    })
    @RequestMapping("/post/LoanRepayAnalysisController/queryLoanRepayAnalysis")
    public void queryLoanRepayAnalysis(InputObject inputObject, OutputObject outputObject) {
        loanRepayAnalysisService.queryLoanRepayAnalysis(inputObject, outputObject);
    }


}
