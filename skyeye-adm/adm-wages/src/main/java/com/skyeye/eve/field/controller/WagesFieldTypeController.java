/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.field.entity.FieldType;
import com.skyeye.eve.field.service.WagesFieldTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WagesFieldTypeController
 * @Description: 薪资字段管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/26 9:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "薪资字段", tags = "薪资字段", modelName = "薪资字段")
public class WagesFieldTypeController {

    @Autowired
    private WagesFieldTypeService wagesFieldTypeService;

    @ApiOperation(id = "wages001", value = "获取薪资字段列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WagesFieldTypeController/queryWagesFieldTypeList")
    public void queryWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) {
        wagesFieldTypeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeWagesFieldTypeMation", value = "新增/编辑薪资字段信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = FieldType.class)
    @RequestMapping("/post/WagesFieldTypeController/writeWagesFieldTypeMation")
    public void writeWagesFieldTypeMation(InputObject inputObject, OutputObject outputObject) {
        wagesFieldTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteWagesFieldTypeMationById", value = "删除薪资字段信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WagesFieldTypeController/deleteWagesFieldTypeMationById")
    public void deleteWagesFieldTypeMationById(InputObject inputObject, OutputObject outputObject) {
        wagesFieldTypeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnableWagesFieldTypeList", value = "获取已经启用的薪资字段列表", method = "POST", allUse = "2")
    @RequestMapping("/post/WagesFieldTypeController/queryEnableWagesFieldTypeList")
    public void queryEnableWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) {
        wagesFieldTypeService.queryEnableWagesFieldTypeList(inputObject, outputObject);
    }

    @ApiOperation(id = "querySysWagesFieldTypeList", value = "获取系统薪资字段列表", method = "POST", allUse = "2")
    @RequestMapping("/post/WagesFieldTypeController/querySysWagesFieldTypeList")
    public void querySysWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) {
        wagesFieldTypeService.querySysWagesFieldTypeList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryWagesFieldListByKeys", value = "根据字段key批量获取薪资字段信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "keys", name = "keys", value = "字段key，逗号隔开", required = "required")})
    @RequestMapping("/post/WagesFieldTypeController/queryWagesFieldListByKeys")
    public void queryWagesFieldListByKeys(InputObject inputObject, OutputObject outputObject) {
        wagesFieldTypeService.queryWagesFieldListByKeys(inputObject, outputObject);
    }

}
