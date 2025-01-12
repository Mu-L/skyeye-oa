/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.project.entity.Project;
import com.skyeye.project.service.ProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProProjectController
 * @Description: 项目管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2023/7/24 8:01
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "项目管理", tags = "项目管理", modelName = "项目管理")
public class ProProjectController {

    @Autowired
    private ProProjectService proProjectService;

    /**
     * 获取项目管理列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProProjectList", value = "获取项目管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProProjectController/queryProProjectList")
    public void queryProProjectList(InputObject inputObject, OutputObject outputObject) {
        proProjectService.queryProProjectList(inputObject, outputObject);
    }

    /**
     * 新增/编辑项目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProject", value = "新增/编辑项目信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Project.class)
    @RequestMapping("/post/ProProjectController/writeProject")
    public void writeProject(InputObject inputObject, OutputObject outputObject) {
        proProjectService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id批量获取项目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProProjectByIds", value = "根据id批量获取项目信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/ProProjectController/queryProProjectByIds")
    public void queryProProjectByIds(InputObject inputObject, OutputObject outputObject) {
        proProjectService.selectByIds(inputObject, outputObject);
    }

    /**
     * 项目提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "proproject008", value = "项目提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "approvalId", name = "approvalId", value = "[提交审批]操作必填审批人", required = "required")})
    @RequestMapping("/post/ProProjectController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        proProjectService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除项目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "proproject009", value = "删除项目信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProProjectController/deleteProjectById")
    public void deleteProjectById(InputObject inputObject, OutputObject outputObject) {
        proProjectService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销项目审批申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "proproject010", value = "撤销项目审批申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程id", required = "required")})
    @RequestMapping("/post/ProProjectController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        proProjectService.revoke(inputObject, outputObject);
    }

    /**
     * 作废项目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "proproject011", value = "作废项目", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProProjectController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        proProjectService.invalid(inputObject, outputObject);
    }

    /**
     * 开始执行项目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "proproject012", value = "开始执行项目", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProProjectController/executeProjectById")
    public void executeProjectById(InputObject inputObject, OutputObject outputObject) {
        proProjectService.executeProjectById(inputObject, outputObject);
    }

    /**
     * 信息完善
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "proproject016", value = "信息完善", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "actualStartTime", name = "actualStartTime", value = "实际开始时间", required = "required"),
        @ApiImplicitParam(id = "actualEndTime", name = "actualEndTime", value = "实际结束时间", required = "required"),
        @ApiImplicitParam(id = "resultsContent", name = "resultsContent", value = "总结", required = "required")})
    @RequestMapping("/post/ProProjectController/perfectProjectById")
    public void perfectProjectById(InputObject inputObject, OutputObject outputObject) {
        proProjectService.perfectProjectById(inputObject, outputObject);
    }

}
