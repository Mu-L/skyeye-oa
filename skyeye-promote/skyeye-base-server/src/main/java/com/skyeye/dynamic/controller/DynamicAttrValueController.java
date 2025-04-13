/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dynamic.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dynamic.entity.DynamicAttrValue;
import com.skyeye.dynamic.entity.DynamicAttrValueApi;
import com.skyeye.dynamic.service.DynamicAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DynamicAttrValueController
 * @Description: 动态属性值管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/13 14:14
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "动态属性值管理", tags = "动态属性值管理", modelName = "动态属性值管理")
public class DynamicAttrValueController {

    @Autowired
    private DynamicAttrValueService dynamicAttrValueService;

    @ApiOperation(id = "writeDynamicAttrValue", value = "新增/编辑动态属性值", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = DynamicAttrValue.class)
    @RequestMapping("/post/DynamicAttrValueController/writeDynamicAttrValue")
    public void writeDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        dynamicAttrValueService.writeDynamicAttrValue(inputObject, outputObject);
    }

    @ApiOperation(id = "writeBatchDynamicAttrValue", value = "批量新增/编辑动态属性值", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = DynamicAttrValueApi.class)
    @RequestMapping("/post/DynamicAttrValueController/writeBatchDynamicAttrValue")
    public void writeBatchDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        dynamicAttrValueService.writeBatchDynamicAttrValue(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDynamicAttrValueList", value = "根据业务对象数据获取动态属性值", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "业务对象数据的id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "业务对象服务的className", required = "required")})
    @RequestMapping("/post/DynamicAttrValueController/queryDynamicAttrValueList")
    public void queryDynamicAttrValueList(InputObject inputObject, OutputObject outputObject) {
        dynamicAttrValueService.queryDynamicAttrValueList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBatchDynamicAttrValueList", value = "根据业务对象数据批量获取动态属性值", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "list", name = "list", value = "业务对象数据的信息，需要包含objectId和objectKey两个字段",
            required = "required,格式：[{\"objectId\":\"1\",\"objectKey\":\"com.skyeye.test.entity.Test\"}]")})
    @RequestMapping("/post/DynamicAttrValueController/queryBatchDynamicAttrValueList")
    public void queryBatchDynamicAttrValueList(InputObject inputObject, OutputObject outputObject) {
        dynamicAttrValueService.queryBatchDynamicAttrValueList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDynamicAttrValue", value = "根据业务对象数据删除动态属性值", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "业务对象数据的id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "业务对象服务的className", required = "required")})
    @RequestMapping("/post/DynamicAttrValueController/deleteDynamicAttrValue")
    public void deleteDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        dynamicAttrValueService.deleteDynamicAttrValue(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteBatchDynamicAttrValue", value = "根据业务对象数据批量删除动态属性值", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "list", name = "list", value = "业务对象数据的信息，需要包含objectId和objectKey两个字段",
            required = "required,格式：[{\"objectId\":\"1\",\"objectKey\":\"com.skyeye.test.entity.Test\"}]")})
    @RequestMapping("/post/DynamicAttrValueController/deleteBatchDynamicAttrValue")
    public void deleteBatchDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        dynamicAttrValueService.deleteBatchDynamicAttrValue(inputObject, outputObject);
    }

}
