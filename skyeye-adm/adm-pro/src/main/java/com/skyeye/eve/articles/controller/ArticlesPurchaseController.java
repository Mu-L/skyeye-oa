/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.articles.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.articles.entity.ArticlesPurchase;
import com.skyeye.eve.articles.service.ArticlesPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ArticlesPurchaseController
 * @Description: 用品采购申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 11:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用品采购", tags = "用品采购", modelName = "用品模块")
public class ArticlesPurchaseController {

    @Autowired
    private ArticlesPurchaseService articlesPurchaseService;

    /**
     * 用品采购申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeArticlesPurchase", value = "用品采购申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ArticlesPurchase.class)
    @RequestMapping("/post/ArticlesPurchaseController/writeArticlesPurchasee")
    public void writeArticlesPurchasee(InputObject inputObject, OutputObject outputObject) {
        articlesPurchaseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取用品采购申请信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "assetarticles025", value = "获取用品采购申请信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ArticlesPurchaseController/queryArticlesPurchaseList")
    public void queryArticlesPurchaseList(InputObject inputObject, OutputObject outputObject) {
        articlesPurchaseService.queryPageList(inputObject, outputObject);
    }

    /**
     * 用品采购申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "assetarticles027", value = "用品采购申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ArticlesPurchaseController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        articlesPurchaseService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废用品采购申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "assetarticles031", value = "作废用品采购申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ArticlesPurchaseController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        articlesPurchaseService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销用品采购申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "assetarticles035", value = "撤销用品采购申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ArticlesPurchaseController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        articlesPurchaseService.revoke(inputObject, outputObject);
    }

}
