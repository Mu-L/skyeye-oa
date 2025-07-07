package com.skyeye.receivepayment.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.receivepayment.entity.ReceivePayment;
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

    @ApiOperation(id = "queryFundPercentage", value = "客户和供应商资金占比", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id="year", name = "year", value = "年份", required = "required"),
            @ApiImplicitParam(id="month", name = "month", value = "月份")
    })
    @RequestMapping("/post/FundAnalysisController/queryFundPercentage")
    public void queryFundPercentage(InputObject inputObject, OutputObject outputObject) {
        fundAnalysisService.queryFundPercentage(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFundTypePercentage", value = "客户和供应商付款方式占比", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id="year", name = "year", value = "年份", required = "required"),
            @ApiImplicitParam(id="month", name = "month", value = "月份")
    })
    @RequestMapping("/post/FundAnalysisController/queryFundTypePercentage")
    public void queryFundTypePercentage(InputObject inputObject, OutputObject outputObject) {
        fundAnalysisService.queryFundTypePercentage(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFundMetrics", value = "客户/供应商,回款/付款关键指标", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id="year", name = "year", value = "年份", required = "required"),
            @ApiImplicitParam(id="objectKey", name = "objectKey", value = "客户/供应商serviceClassName", required = "required"),
            @ApiImplicitParam(id="month", name = "month", value = "月份")
    })
    @RequestMapping("/post/FundAnalysisController/queryFundMetrics")
    public void queryFundMetrics(InputObject inputObject, OutputObject outputObject) {
        fundAnalysisService.queryFundMetrics(inputObject, outputObject);
    }


}
