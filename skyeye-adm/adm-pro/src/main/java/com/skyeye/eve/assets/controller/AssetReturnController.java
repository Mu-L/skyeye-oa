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
import com.skyeye.eve.assets.entity.AssetReturn;
import com.skyeye.eve.assets.service.AssetReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AssetReturnController
 * @Description: 资产归还单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/20 22:36
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "资产归还", tags = "资产归还", modelName = "资产模块")
public class AssetReturnController {

    @Autowired
    private AssetReturnService assetReturnService;

    /**
     * 获取我的资产归还申请信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset025", value = "获取我的资产归还申请信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AssetReturnController/queryAssetReturnList")
    public void queryAssetReturnList(InputObject inputObject, OutputObject outputObject) {
        assetReturnService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑资产归还单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeAssetReturn", value = "新增/编辑资产归还单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AssetReturn.class)
    @RequestMapping("/post/AssetReturnController/writeAssetReturn")
    public void writeAssetReturn(InputObject inputObject, OutputObject outputObject) {
        assetReturnService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 资产归还申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset028", value = "资产归还申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/AssetReturnController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        assetReturnService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废资产归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset030", value = "作废资产归还申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssetReturnController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        assetReturnService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销资产归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "asset038", value = "撤销资产归还申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/AssetReturnController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        assetReturnService.revoke(inputObject, outputObject);
    }

}
