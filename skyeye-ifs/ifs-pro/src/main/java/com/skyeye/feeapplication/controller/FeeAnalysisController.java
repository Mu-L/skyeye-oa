package com.skyeye.feeapplication.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.feeapplication.service.FeeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FeeAnalysisController
 * @Description: 费用申请分析控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "费用申请分析", tags = "费用申请分析", modelName = "费用申请分析")
public class FeeAnalysisController {

    @Autowired
    private FeeAnalysisService feeAnalysisService;

    @ApiOperation(id = "queryFeeAnalysis", value = "费用申请分析趋势图", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "year", name = "year", value = "年", required = "required"),
    })
    @RequestMapping("/post/FeeAnalysisController/queryFeeAnalysis")
    public void queryFeeAnalysis(InputObject inputObject, OutputObject outputObject) {
        feeAnalysisService.queryFeeAnalysis(inputObject, outputObject);
    }

}
