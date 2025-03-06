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
import com.skyeye.school.checkwork.entity.Checkwork;
import com.skyeye.school.checkwork.service.CheckworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CheckworkController
 * @Description: 考勤管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/24 10:47
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "考勤管理", tags = "考勤管理", modelName = "考勤管理")
public class CheckworkController {

    @Autowired
    private CheckworkService checkworkService;

    /**
     * 获取考勤信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCheckworkList", value = "获取考勤信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CheckworkController/queryCheckworkList")
    public void queryCheckworkList(InputObject inputObject, OutputObject outputObject) {
        checkworkService.queryPageList(inputObject, outputObject);
    }

    /**
     * 添加或修改考勤
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeCheckwork", value = "新增/编辑考勤信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Checkwork.class)
    @RequestMapping("/post/CheckworkController/writeCheckwork")
    public void writeCheckwork(InputObject inputObject, OutputObject outputObject) {
        checkworkService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询考勤信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCheckworkById", value = "根据id查询考勤信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CheckworkController/queryCheckworkById")
    public void queryCheckworkById(InputObject inputObject, OutputObject outputObject) {
        checkworkService.selectById(inputObject, outputObject);
    }

    /**
     * 根据考勤码查询考勤信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCheckworkBySourceCode", value = "根据考勤码查询考勤信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "sourceCode", name = "sourceCode", value = "考勤码", required = "required")})
    @RequestMapping("/post/CheckworkController/queryCheckworkBySourceCode")
    public void queryCheckworkBySourceCode(InputObject inputObject, OutputObject outputObject) {
        checkworkService.queryCheckworkBySourceCode(inputObject, outputObject);
    }

    /**
     * 删除考勤信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteCheckworkById", value = "根据ID删除考勤信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CheckworkController/deleteCheckworkById")
    public void deleteCheckworkById(InputObject inputObject, OutputObject outputObject) {
        checkworkService.deleteById(inputObject, outputObject);
    }

    /**
     * 已考勤 待考勤的数量统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCheckworkBySourceCodeAll", value = "已考勤 待考勤的数量统计", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CheckworkController/queryCheckworkBySourceCodeAll")
    public void queryCheckworkBySourceCodeAll(InputObject inputObject, OutputObject outputObject) {
        checkworkService.queryCheckworkBySourceCodeAll(inputObject, outputObject);
    }

}
