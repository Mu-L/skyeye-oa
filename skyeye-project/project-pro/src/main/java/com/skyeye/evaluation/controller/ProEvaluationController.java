package com.skyeye.evaluation.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.evaluation.entity.ProEvaluation;
import com.skyeye.evaluation.service.ProEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProEvaluationController
 * @Description: 项目评估控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "项目评估管理", tags = "项目评估管理", modelName = "项目评估管理")
public class ProEvaluationController {

    @Autowired
    private ProEvaluationService proEvaluationService;

    @ApiOperation(id = "queryProEvaluationList", value = "获取项目评估信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProEvaluationController/queryProEvaluationList")
    public void queryProEvaluationList(InputObject inputObject, OutputObject outputObject) {
        proEvaluationService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProEvaluation", value = "新增/编辑项目评估信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProEvaluation.class)
    @RequestMapping("/post/ProEvaluationController/writeProEvaluation")
    public void writeProEvaluation(InputObject inputObject, OutputObject outputObject) {
        proEvaluationService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProEvaluationById", value = "删除项目评估信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProEvaluationController/deleteProEvaluationById")
    public void deleteProEvaluationById(InputObject inputObject, OutputObject outputObject) {
        proEvaluationService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProEvaluationById", value = "根据id获取项目评估信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProEvaluationController/queryProEvaluationById")
    public void queryProEvaluationById(InputObject inputObject, OutputObject outputObject) {
        proEvaluationService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalProEvaluation", value = "项目评估提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProEvaluationController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        proEvaluationService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeProEvaluation", value = "撤销项目评估审批申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProEvaluationController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        proEvaluationService.revoke(inputObject, outputObject);
    }

}