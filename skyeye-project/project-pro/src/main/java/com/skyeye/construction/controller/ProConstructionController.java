package com.skyeye.construction.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.construction.entity.ProConstruction;
import com.skyeye.construction.service.ProConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProConstructionController
 * @Description: 施工方案控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "施工方案管理", tags = "施工方案管理", modelName = "施工方案管理")
public class ProConstructionController {

    @Autowired
    private ProConstructionService proConstructionService;

    @ApiOperation(id = "queryProConstructionList", value = "获取施工方案信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProConstructionController/queryProConstructionList")
    public void queryProConstructionList(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProConstruction", value = "新增/编辑施工方案信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProConstruction.class)
    @RequestMapping("/post/ProConstructionController/writeProConstruction")
    public void writeProConstruction(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProConstructionById", value = "删除施工方案信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProConstructionController/deleteProConstructionById")
    public void deleteProConstructionById(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProConstructionById", value = "根据id获取施工方案信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProConstructionController/queryProConstructionById")
    public void queryProConstructionById(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalProConstruction", value = "施工方案提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProConstructionController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeProConstruction", value = "撤销施工方案审批申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProConstructionController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryConstructionListByVersionNo", value = "根据版本号组查询施工方案历史版本列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "versionNo", name = "versionNo", value = "版本号组", required = "required")})
    @RequestMapping("/post/ProConstructionController/queryConstructionListByVersionNo")
    public void queryConstructionListByVersionNo(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.queryConstructionListByVersionNo(inputObject, outputObject);
    }

    @ApiOperation(id = "publishProConstructionVersionById", value = "根据id发布施工方案版本", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "施工方案id", required = "required")})
    @RequestMapping("/post/ProConstructionController/publishProConstructionVersionById")
    public void publishSchemeVersionById(InputObject inputObject, OutputObject outputObject) {
        proConstructionService.publishVersionById(inputObject, outputObject);
    }

}