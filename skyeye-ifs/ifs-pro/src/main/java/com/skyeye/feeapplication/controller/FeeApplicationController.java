package com.skyeye.feeapplication.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.feeapplication.entity.FeeApplication;
import com.skyeye.feeapplication.service.FeeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FeeApplicationController
 * @Description: 费用申请控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "费用申请", tags = "费用申请", modelName = "费用申请")
public class FeeApplicationController {

    @Autowired
    private FeeApplicationService feeApplicationService;

    @ApiOperation(id = "queryFeeApplicationList", value = "查询申请费用列表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FeeApplicationController/queryFeeApplicationList")
    public void queryFeeApplicationList(InputObject inputObject, OutputObject outputObject) {
        feeApplicationService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeFeeApplications", value = "新增/编辑费用申请", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = FeeApplication.class)
    @RequestMapping("/post/FeeApplicationController/writeFeeApplications")
    public void writeFeeApplications(InputObject inputObject, OutputObject outputObject) {
        feeApplicationService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeFeeApplication", value = "撤销费用申请", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/FeeApplicationController/revokeFeeApplication")
    public void revokeFeeApplication(InputObject inputObject, OutputObject outputObject) {
        feeApplicationService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "submitFeeApplication", value = "提交审批费用申请", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/FeeApplicationController/submitFeeApplication")
    public void submitFeeApplication(InputObject inputObject, OutputObject outputObject) {
        feeApplicationService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteFeeApplicationById", value = "删除费用申请", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/FeeApplicationController/deleteFeeApplicationById")
    public void deleteFeeApplicationById(InputObject inputObject, OutputObject outputObject) {
        feeApplicationService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFeeApplicationById", value = "删除费用申请", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/FeeApplicationController/queryFeeApplicationById")
    public void queryFeeApplicationById(InputObject inputObject, OutputObject outputObject) {
        feeApplicationService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFeeApplicationAnalysis", value = "费用分析", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "year", name = "year", value = "年", required = "required"),
            @ApiImplicitParam(id = "month", name = "month", value = "月")
    })
    @RequestMapping("/post/FeeApplicationController/queryFeeApplicationAnalysis")
    public void queryFeeApplicationAnalysis(InputObject inputObject, OutputObject outputObject) {
        feeApplicationService.queryFeeApplicationAnalysis(inputObject, outputObject);
    }
}
