/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.certification.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.certification.entity.Certification;
import com.skyeye.certification.service.CertificationService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CertificationController
 * @Description: 学生认证信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/24 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "学生认证管理", tags = "学生认证管理", modelName = "学生认证管理")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    /**
     * 获取学生认证信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCertificationList", value = "获取学生认证信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CertificationController/queryCertificationList")
    public void queryCertificationList(InputObject inputObject, OutputObject outputObject) {
        certificationService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据id查询认证信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCertificationById", value = "根据id查询认证信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "用户id", required = "required")})
    @RequestMapping("/post/CertificationController/queryCertificationById")
    public void queryCertificationById(InputObject inputObject, OutputObject outputObject) {
        certificationService.queryByUserId(inputObject, outputObject);
    }

    /**
     * 新增/编辑学生认证信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "saveOrUpdateCertification", value = "新增/编辑学生认证信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Certification.class)
    @RequestMapping("/post/CertificationController/saveOrUpdateCertification")
    public void saveOrUpdateCertification(InputObject inputObject, OutputObject outputObject) {
        certificationService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 审核学生信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "reviewInformation", value = "审核学生信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "状态", required = "required")})
    @RequestMapping("/post/CertificationController/reviewInformation")
    public void reviewInformation(InputObject inputObject, OutputObject outputObject) {
        certificationService.reviewInformation(inputObject, outputObject);
    }

    /**
     * 根据学生学号查询已认证的学生信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUserByStudentNumber", value = "根据学生学号查询已认证的学生信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "studentNumber", name = "studentNumber", value = "学生学号，多个逗号隔开", required = "required")})
    @RequestMapping("/post/CertificationController/queryUserByStudentNumber")
    public void queryUserByStudentNumber(InputObject inputObject, OutputObject outputObject) {
        certificationService.queryUserByStudentNumber(inputObject, outputObject);
    }
}