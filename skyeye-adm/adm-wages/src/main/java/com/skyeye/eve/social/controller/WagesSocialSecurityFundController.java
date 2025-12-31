/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.social.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.social.entity.SocialSecurityFund;
import com.skyeye.eve.social.service.WagesSocialSecurityFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WagesSocialSecurityFundController
 * @Description: 社保公积金控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/15 8:50
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "社保公积金管理", tags = "社保公积金管理", modelName = "社保公积金管理")
public class WagesSocialSecurityFundController {

    @Autowired
    private WagesSocialSecurityFundService wagesSocialSecurityFundService;

    /**
     * 获取社保公积金模板列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wagessocialsecurityfund001", value = "获取社保公积金模板列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WagesSocialSecurityFundController/queryWagesSocialSecurityFundList")
    public void queryWagesSocialSecurityFundList(InputObject inputObject, OutputObject outputObject) {
        wagesSocialSecurityFundService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑社保公积金模板信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeWagesSocialSecurityFund", value = "新增/编辑社保公积金模板信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SocialSecurityFund.class)
    @RequestMapping("/post/WagesSocialSecurityFundController/writeWagesSocialSecurityFund")
    public void writeWagesSocialSecurityFund(InputObject inputObject, OutputObject outputObject) {
        wagesSocialSecurityFundService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除社保公积金模板信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteWagesSocialSecurityFund", value = "删除社保公积金模板信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WagesSocialSecurityFundController/deleteWagesSocialSecurityFund")
    public void deleteWagesSocialSecurityFund(InputObject inputObject, OutputObject outputObject) {
        wagesSocialSecurityFundService.deleteById(inputObject, outputObject);
    }

}
