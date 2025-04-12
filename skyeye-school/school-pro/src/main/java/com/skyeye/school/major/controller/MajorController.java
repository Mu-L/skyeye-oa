/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.major.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MajorController
 * @Description: 专业管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/9 9:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "专业管理", tags = "专业管理", modelName = "专业管理")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @ApiOperation(id = "queryMajorList", value = "获取专业信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MajorController/queryMajorList")
    public void queryFacultyList(InputObject inputObject, OutputObject outputObject) {
        majorService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMajor", value = "新增/编辑专业信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Major.class)
    @RequestMapping("/post/MajorController/writeMajor")
    public void writeMajor(InputObject inputObject, OutputObject outputObject) {
        majorService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMajorById", value = "根据ID删除专业信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MajorController/deleteMajorById")
    public void deleteMajorById(InputObject inputObject, OutputObject outputObject) {
        majorService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMajorByIds", value = "根据id批量查询专业信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id，多个用逗号隔开", required = "required")})
    @RequestMapping("/post/MajorController/queryMajorByIds")
    public void queryMajorByIds(InputObject inputObject, OutputObject outputObject) {
        majorService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMajorListByFacultyId", value = "根据院系id获取专业列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "facultyId", name = "facultyId", value = "院系id")})
    @RequestMapping("/post/MajorController/queryMajorListByFacultyId")
    public void queryMajorListByFacultyId(InputObject inputObject, OutputObject outputObject) {
        majorService.queryMajorListByFacultyId(inputObject, outputObject);
    }

}
