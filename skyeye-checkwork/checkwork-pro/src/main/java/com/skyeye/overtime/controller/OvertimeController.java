/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.overtime.entity.OverTime;
import com.skyeye.overtime.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: OvertimeController
 * @Description: 加班申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/8 22:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "加班申请", tags = "加班申请", modelName = "加班申请")
public class OvertimeController {

    @Autowired
    private OvertimeService overtimeService;

    /**
     * 获取我的加班申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkovertime001", value = "获取我的加班申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OvertimeController/queryOvertimeList")
    public void queryOvertimeList(InputObject inputObject, OutputObject outputObject) {
        overtimeService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑加班申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeOvertime", value = "新增/编辑加班申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = OverTime.class)
    @RequestMapping("/post/OvertimeController/writeOvertime")
    public void writeOvertime(InputObject inputObject, OutputObject outputObject) {
        overtimeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 加班申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkovertime006", value = "加班申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/OvertimeController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        overtimeService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废加班申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkovertime007", value = "作废加班申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OvertimeController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        overtimeService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销加班申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkovertime009", value = "撤销加班申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/OvertimeController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        overtimeService.revoke(inputObject, outputObject);
    }

}
