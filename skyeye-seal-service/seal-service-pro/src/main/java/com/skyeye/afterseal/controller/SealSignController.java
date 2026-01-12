/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.controller;

import com.skyeye.afterseal.classenum.SealSignWorkUnit;
import com.skyeye.afterseal.entity.SealSign;
import com.skyeye.afterseal.service.SealSignService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealSignController
 * @Description: 工人签到报工信息控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工人签到报工信息", tags = "工人签到报工信息", modelName = "售后工单")
public class SealSignController {

    @Autowired
    private SealSignService sealSignService;

    @ApiOperation(id = "querySealSignList", value = "获取工人签到报工信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealSignController/querySealSignList")
    public void querySealSignList(InputObject inputObject, OutputObject outputObject) {
        sealSignService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSealSign", value = "新增工人签到信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SealSign.class)
    @RequestMapping("/post/SealSignController/insertSealSign")
    public void insertSealSign(InputObject inputObject, OutputObject outputObject) {
        sealSignService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "reportWork", value = "报工（填写工时信息）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "签到记录ID", required = "required"),
        @ApiImplicitParam(id = "workHours", name = "workHours", value = "工时（工作时长）", required = "required"),
        @ApiImplicitParam(id = "workUnit", name = "workUnit", value = "工时单位", enumClass = SealSignWorkUnit.class, required = "required")})
    @RequestMapping("/post/SealSignController/reportWork")
    public void reportWork(InputObject inputObject, OutputObject outputObject) {
        sealSignService.reportWork(inputObject, outputObject);
    }

    @ApiOperation(id = "auditSign", value = "审核签到报工记录", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "签到记录ID", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "审核结果（已通过/已驳回对应的数字值）", required = "required"),
        @ApiImplicitParam(id = "auditRemark", name = "auditRemark", value = "审核备注")})
    @RequestMapping("/post/SealSignController/auditSign")
    public void auditSign(InputObject inputObject, OutputObject outputObject) {
        sealSignService.auditSign(inputObject, outputObject);
    }

}
