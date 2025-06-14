/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.assets.entity.AssetUse;
import com.skyeye.eve.assets.service.AssetUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AssetUseController
 * @Description: 资产领用申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 17:10
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "资产领用", tags = "资产领用", modelName = "资产模块")
public class AssetUseController {

    @Autowired
    private AssetUseService assetUseService;

    /**
     * 获取我发起的资产领用信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset010", value = "获取我发起的资产领用信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AssetUseController/queryAssetUseList")
    public void queryAssetUseList(InputObject inputObject, OutputObject outputObject) {
        assetUseService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑资产领用单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeAssetUse", value = "新增/编辑资产领用单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AssetUse.class)
    @RequestMapping("/post/AssetUseController/writeAssetUse")
    public void writeAssetUse(InputObject inputObject, OutputObject outputObject) {
        assetUseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 作废资产领用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset016", value = "作废资产领用申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssetUseController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        assetUseService.invalid(inputObject, outputObject);
    }

    /**
     * 资产领用申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset017", value = "资产领用申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/AssetUseController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        assetUseService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销资产领用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset036", value = "撤销资产领用申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/AssetUseController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        assetUseService.revoke(inputObject, outputObject);
    }

}
