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
import com.skyeye.eve.assets.entity.AssetPurchaseReturn;
import com.skyeye.eve.assets.service.AssetPurchaseReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AssetPurchaseReturnController
 * @Description: 资产采购退货控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/19 20:38
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "资产采购退货", tags = "资产采购退货", modelName = "资产模块")
public class AssetPurchaseReturnController {

    @Autowired
    private AssetPurchaseReturnService assetPurchaseReturnService;

    /**
     * 获取资产退货单信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAssetPurchaseReturnList", value = "获取资产退货单信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AssetPurchaseReturnController/queryAssetPurchaseReturnList")
    public void queryAssetPurchaseList(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseReturnService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑资产退货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeAssetPurchaseReturn", value = "新增/编辑资产退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AssetPurchaseReturn.class)
    @RequestMapping("/post/AssetPurchaseReturnController/writeAssetPurchaseReturn")
    public void writeAssetPurchase(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseReturnService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 资产退货单提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitAssetPurchaseReturn", value = "资产退货单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/AssetPurchaseReturnController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseReturnService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除资产退货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteAssetPurchaseReturnById", value = "删除资产退货单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssetPurchaseReturnController/deleteAssetPurchaseReturnById")
    public void deleteAssetPurchaseReturnById(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseReturnService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销资产退货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeAssetPurchaseReturn", value = "撤销资产退货单", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/AssetPurchaseReturnController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseReturnService.revoke(inputObject, outputObject);
    }

}
