/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.gw.entity.GwSendDocument;
import com.skyeye.eve.gw.service.GwSendDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: GwSendDocumentController
 * @Description: 公文发文管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/26 15:45
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "公文发文管理", tags = "公文发文管理", modelName = "公文发文管理")
public class GwSendDocumentController {

    @Autowired
    private GwSendDocumentService gwSendDocumentService;

    /**
     * 获取公文发文列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryGwSendDocumentList", value = "获取公文发文列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/GwSendDocumentController/queryGwSendDocumentList")
    public void queryGwSendDocumentList(InputObject inputObject, OutputObject outputObject) {
        gwSendDocumentService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑公文发文
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeGwSendDocument", value = "新增/编辑公文发文", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = GwSendDocument.class)
    @RequestMapping("/post/GwSendDocumentController/writeGwSendDocument")
    public void writeGwSendDocument(InputObject inputObject, OutputObject outputObject) {
        gwSendDocumentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 公文发文提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitGwSendDocumentToApproval", value = "公文发文提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/GwSendDocumentController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        gwSendDocumentService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 公文发文详情
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryGwSendDocumentById", value = "公文发文详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/GwSendDocumentController/queryGwSendDocumentById")
    public void queryGwSendDocumentById(InputObject inputObject, OutputObject outputObject) {
        gwSendDocumentService.selectById(inputObject, outputObject);
    }

    /**
     * 作废公文发文
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "invalidGwSendDocument", value = "作废公文发文", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/GwSendDocumentController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        gwSendDocumentService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销公文发文
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeGwSendDocument", value = "撤销公文发文", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/GwSendDocumentController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        gwSendDocumentService.revoke(inputObject, outputObject);
    }

}
