package com.skyeye.followup.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.followup.entity.ProFollowup;
import com.skyeye.followup.service.ProFollowupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProFollowupController
 * @Description: 项目跟进控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "项目跟进管理", tags = "项目跟进管理", modelName = "项目跟进管理")
public class ProFollowupController {

    @Autowired
    private ProFollowupService proFollowupService;

    @ApiOperation(id = "queryProFollowupList", value = "获取项目跟进信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProFollowupController/queryProFollowupList")
    public void queryProFollowupList(InputObject inputObject, OutputObject outputObject) {
        proFollowupService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProFollowup", value = "新增/编辑项目跟进信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProFollowup.class)
    @RequestMapping("/post/ProFollowupController/writeProFollowup")
    public void writeProFollowup(InputObject inputObject, OutputObject outputObject) {
        proFollowupService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProFollowupById", value = "删除项目跟进信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProFollowupController/deleteProFollowupById")
    public void deleteProFollowupById(InputObject inputObject, OutputObject outputObject) {
        proFollowupService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProFollowupById", value = "根据id获取项目跟进信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProFollowupController/queryProFollowupById")
    public void queryProFollowupById(InputObject inputObject, OutputObject outputObject) {
        proFollowupService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "updateFollowupState", value = "更新跟进状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "跟进ID", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "跟进状态", required = "required")})
    @RequestMapping("/post/ProFollowupController/updateFollowupState")
    public void updateFollowupState(InputObject inputObject, OutputObject outputObject) {
        proFollowupService.updateFollowupState(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFollowupStatistics", value = "获取项目跟进统计信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "projectId", name = "projectId", value = "项目ID", required = "required")})
    @RequestMapping("/post/ProFollowupController/queryFollowupStatistics")
    public void queryFollowupStatistics(InputObject inputObject, OutputObject outputObject) {
        proFollowupService.queryFollowupStatistics(inputObject, outputObject);
    }

}