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
import com.skyeye.eve.gw.entity.GwReceiveDocument;
import com.skyeye.eve.gw.service.GwReceiveDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: GwReceiveDocumentController
 * @Description: 公文收文管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/26 22:43
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "公文收文管理", tags = "公文收文管理", modelName = "公文收文管理")
public class GwReceiveDocumentController {

    @Autowired
    private GwReceiveDocumentService gwReceiveDocumentService;

    /**
     * 获取公文收文列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryGwReceiveDocumentList", value = "获取公文收文列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/GwReceiveDocumentController/queryGwReceiveDocumentList")
    public void queryGwReceiveDocumentList(InputObject inputObject, OutputObject outputObject) {
        gwReceiveDocumentService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑公文收文
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeGwReceiveDocument", value = "新增/编辑公文收文", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = GwReceiveDocument.class)
    @RequestMapping("/post/GwReceiveDocumentController/writeGwReceiveDocument")
    public void writeGwReceiveDocument(InputObject inputObject, OutputObject outputObject) {
        gwReceiveDocumentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 公文收文提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitGwReceiveDocumentToApproval", value = "公文收文提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/GwReceiveDocumentController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        gwReceiveDocumentService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废公文收文
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "invalidGwReceiveDocument", value = "作废公文收文", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/GwReceiveDocumentController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        gwReceiveDocumentService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销公文收文
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeGwReceiveDocument", value = "撤销公文收文", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/GwReceiveDocumentController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        gwReceiveDocumentService.revoke(inputObject, outputObject);
    }

}
