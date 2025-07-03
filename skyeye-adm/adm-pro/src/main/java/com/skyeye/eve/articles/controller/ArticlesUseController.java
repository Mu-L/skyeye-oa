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
import com.skyeye.eve.articles.entity.ArticlesUse;
import com.skyeye.eve.articles.service.ArticlesUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ArticlesUseController
 * @Description: 用品领用申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用品领用", tags = "用品领用", modelName = "用品模块")
public class ArticlesUseController {

    @Autowired
    private ArticlesUseService articlesUseService;

    @ApiOperation(id = "assetarticles017", value = "获取我领用的用品信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ArticlesUseController/queryMyUseAssetArticlesMation")
    public void queryMyUseAssetArticlesMation(InputObject inputObject, OutputObject outputObject) {
        articlesUseService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAssetArticlesApplyUse", value = "用品领用申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ArticlesUse.class)
    @RequestMapping("/post/ArticlesUseController/writeArticlesApplyUse")
    public void writeArticlesApplyUse(InputObject inputObject, OutputObject outputObject) {
        articlesUseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "assetarticles023", value = "用品领用申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ArticlesUseController/articlesUseSubmitToApproval")
    public void articlesUseSubmitToApproval(InputObject inputObject, OutputObject outputObject) {
        articlesUseService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "assetarticles030", value = "作废用品领用申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ArticlesUseController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        articlesUseService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "assetarticles034", value = "撤销用品领用申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ArticlesUseController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        articlesUseService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "myhasmation004", value = "我的用品领用历史", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ArticlesUseController/queryMyArticlesList")
    public void queryMyArticlesList(InputObject inputObject, OutputObject outputObject) {
        articlesUseService.queryMyArticlesList(inputObject, outputObject);
    }

}
