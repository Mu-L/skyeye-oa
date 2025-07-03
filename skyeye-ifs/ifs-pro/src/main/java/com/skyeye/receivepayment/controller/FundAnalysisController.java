package com.skyeye.receivepayment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.receivepayment.service.FundAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FundAnalysisController
 * @Description: 资金分析管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "资金分析管理", tags = "资金分析管理", modelName = "资金分析管理")
public class FundAnalysisController {

    @Autowired
    private FundAnalysisService fundAnalysisService;

    @ApiOperation(id = "queryFundAnalysis", value = "获取每月的资金分析", method = "POST", allUse = "2")
    @RequestMapping("/post/FundAnalysisController/queryFundAnalysis")
    public void queryFundAnalysis(InputObject inputObject, OutputObject outputObject) {
        fundAnalysisService.queryFundAnalysis(inputObject, outputObject);
    }
}
