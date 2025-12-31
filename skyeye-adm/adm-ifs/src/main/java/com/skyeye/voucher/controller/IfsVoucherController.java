/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.voucher.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.voucher.entity.Voucher;
import com.skyeye.voucher.service.IfsVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IfsVoucherController
 * @Description: 凭证信息管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/3 18:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "凭证管理", tags = "凭证管理", modelName = "凭证管理")
public class IfsVoucherController {

    @Autowired
    private IfsVoucherService ifsVoucherService;

    /**
     * 查询我上传的凭证
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "ifsVoucher001", value = "查询我上传的凭证", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/IfsVoucherController/queryIfsVoucherList")
    public void queryIfsVoucherList(InputObject inputObject, OutputObject outputObject) {
        ifsVoucherService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增凭证
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "ifsVoucher002", value = "新增凭证", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Voucher.class)
    @RequestMapping("/post/IfsVoucherController/insertIfsVoucher")
    public void insertIfsVoucher(InputObject inputObject, OutputObject outputObject) {
        ifsVoucherService.createEntity(inputObject, outputObject);
    }

    /**
     * 删除上传的凭证
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "ifsVoucher003", value = "删除上传的凭证", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/IfsVoucherController/deleteIfsVoucherById")
    public void deleteIfsVoucherById(InputObject inputObject, OutputObject outputObject) {
        ifsVoucherService.deleteById(inputObject, outputObject);
    }

}
