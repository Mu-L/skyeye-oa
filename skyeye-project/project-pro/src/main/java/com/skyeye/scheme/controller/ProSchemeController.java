/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheme.entity.ProScheme;
import com.skyeye.scheme.service.ProSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProSchemeController
 * @Description: 项目方案管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "项目方案管理", tags = "项目方案管理", modelName = "项目方案管理")
public class ProSchemeController {

    @Autowired
    private ProSchemeService proSchemeService;

    @ApiOperation(id = "queryProSchemeList", value = "查询项目方案列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProSchemeController/queryProSchemeList")
    public void queryProSchemeList(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProScheme", value = "新增/编辑项目方案", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProScheme.class)
    @RequestMapping("/post/ProSchemeController/writeProScheme")
    public void writeProScheme(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProSchemeById", value = "根据id查询项目方案详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "方案id", required = "required")})
    @RequestMapping("/post/ProSchemeController/queryProSchemeById")
    public void queryProSchemeById(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProSchemeByIds", value = "根据ids批量查询项目方案详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "方案id集合", required = "required")})
    @RequestMapping("/post/ProSchemeController/queryProSchemeByIds")
    public void queryProSchemeByIds(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProSchemeById", value = "根据ID删除项目方案信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProSchemeController/deleteProSchemeById")
    public void deleteProSchemeById(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchemeListByProjectId", value = "根据项目id查询方案列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "projectId", name = "projectId", value = "项目id")})
    @RequestMapping("/post/ProSchemeController/querySchemeListByProjectId")
    public void querySchemeListByProjectId(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.querySchemeListByProjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "querySchemeListBySchemeCode", value = "根据方案编码查询方案历史版本列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "schemeCode", name = "schemeCode", value = "方案编码", required = "required")})
    @RequestMapping("/post/ProSchemeController/querySchemeListBySchemeCode")
    public void querySchemeListBySchemeCode(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.querySchemeListBySchemeCode(inputObject, outputObject);
    }

    @ApiOperation(id = "publishSchemeVersionById", value = "根据id发布方案版本", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "方案id", required = "required")})
    @RequestMapping("/post/ProSchemeController/publishSchemeVersionById")
    public void publishSchemeVersionById(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.publishVersionById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalProScheme", value = "项目方案提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProSchemeController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeProScheme", value = "撤销项目方案审批申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProSchemeController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        proSchemeService.revoke(inputObject, outputObject);
    }

}

