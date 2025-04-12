/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.checkwork.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.checkwork.service.CheckworkSignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CheckworkSignController
 * @Description: 学生考勤签到控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/24 10:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "学生考勤签到", tags = "学生考勤签到", modelName = "考勤管理")
public class CheckworkSignController {

    @Autowired
    private CheckworkSignService checkworkSignService;

    /**
     * 扫码考勤签到
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "createCheckworkSignBySourceCode", value = "扫码考勤签到", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "sourceCode", name = "sourceCode", value = "考勤码", required = "required")})
    @RequestMapping("/post/CheckworkSignController/createCheckworkSignBySourceCode")
    public void createCheckworkSignBySourceCode(InputObject inputObject, OutputObject outputObject) {
        checkworkSignService.createCheckworkSignBySourceCode(inputObject, outputObject);
    }

    /**
     * 数字考勤签到
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "createCheckworkSignById", value = "数字考勤签到", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "checkworkId", name = "checkworkId", value = "考勤信息id", required = "required"),
        @ApiImplicitParam(id = "codeNumber", name = "codeNumber", value = "数字考勤时设置的数字编码", required = "required")})
    @RequestMapping("/post/CheckworkSignController/createCheckworkSignById")
    public void createCheckworkSignById(InputObject inputObject, OutputObject outputObject) {
        checkworkSignService.createCheckworkSignById(inputObject, outputObject);
    }

    /**
     * 获取所有我的待考勤信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCheckworkWaitSignList", value = "获取所有我的待考勤信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CheckworkSignController/queryCheckworkWaitSignList")
    public void queryCheckworkWaitSignList(InputObject inputObject, OutputObject outputObject) {
        checkworkSignService.queryCheckworkWaitSignList(inputObject, outputObject);
    }

    /**
     * 获取所有我的已经考勤信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCheckworkAlreadySignList", value = "获取所有我的已经考勤信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CheckworkSignController/queryCheckworkAlreadySignList")
    public void queryCheckworkAlreadySignList(InputObject inputObject, OutputObject outputObject) {
        checkworkSignService.queryCheckworkAlreadySignList(inputObject, outputObject);
    }

    /**
     * 获取学生的待考勤、已考勤、迟到的数量
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryStuCheckworkSignCount", value = "获取学生的待考勤、已考勤、迟到的数量", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "stuId",name = "stuId", value = "学生ID", required = "required"),
        @ApiImplicitParam(id = "subjectClassId",name = "subjectClassId", value = "科目与班级id", required = "required")
    })
    @RequestMapping("/post/CheckworkSignController/queryStuCheckworkSignCount")
    public void queryStuCheckworkSignCount(InputObject inputObject, OutputObject outputObject) {
        checkworkSignService.queryStuCheckworkSignCount(inputObject, outputObject);
    }

}
