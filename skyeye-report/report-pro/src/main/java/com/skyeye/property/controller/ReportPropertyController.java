/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.property.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.property.entity.Property;
import com.skyeye.property.service.ReportPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReportPropertyController
 * @Description: 模型---样式属性管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/9/5 16:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "样式属性管理", tags = "样式属性管理", modelName = "样式属性管理")
public class ReportPropertyController {

    @Autowired
    private ReportPropertyService reportPropertyService;

    @ApiOperation(id = "reportproperty001", value = "获取模型属性列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReportPropertyController/queryPropertyList")
    public void queryPropertyList(InputObject inputObject, OutputObject outputObject) {
        reportPropertyService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProperty", value = "新增/编辑模型属性", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Property.class)
    @RequestMapping("/post/ReportPropertyController/writeProperty")
    public void writeProperty(InputObject inputObject, OutputObject outputObject) {
        reportPropertyService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePropertyById", value = "删除模型属性", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportPropertyController/deletePropertyById")
    public void deletePropertyById(InputObject inputObject, OutputObject outputObject) {
        reportPropertyService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPropertyById", value = "根据id获取模型属性", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportPropertyController/queryPropertyById")
    public void queryPropertyById(InputObject inputObject, OutputObject outputObject) {
        reportPropertyService.selectById(inputObject, outputObject);
    }

}
