/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ordertype.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.ordertype.entity.SealOrderType;
import com.skyeye.ordertype.service.SealOrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealOrderTypeController
 * @Description: 工单类型控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工单类型", tags = "工单类型", modelName = "工单类型")
public class SealOrderTypeController {

    @Autowired
    private SealOrderTypeService sealOrderTypeService;

    @ApiOperation(id = "querySealOrderTypeList", value = "查询工单类型列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealOrderTypeController/querySealOrderTypeList")
    public void querySealOrderTypeList(InputObject inputObject, OutputObject outputObject) {
        sealOrderTypeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSealOrderType", value = "新增/编辑工单类型", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SealOrderType.class)
    @RequestMapping("/post/SealOrderTypeController/writeSealOrderType")
    public void writeSealOrderType(InputObject inputObject, OutputObject outputObject) {
        sealOrderTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealOrderTypeById", value = "根据id查询工单类型详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "工单类型id", required = "required")})
    @RequestMapping("/post/SealOrderTypeController/querySealOrderTypeById")
    public void querySealOrderTypeById(InputObject inputObject, OutputObject outputObject) {
        sealOrderTypeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealOrderTypeByIds", value = "根据ids批量查询工单类型详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "工单类型id集合", required = "required")})
    @RequestMapping("/post/SealOrderTypeController/querySealOrderTypeByIds")
    public void querySealOrderTypeByIds(InputObject inputObject, OutputObject outputObject) {
        sealOrderTypeService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSealOrderTypeById", value = "根据ID删除工单类型信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealOrderTypeController/deleteSealOrderTypeById")
    public void deleteSealOrderTypeById(InputObject inputObject, OutputObject outputObject) {
        sealOrderTypeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledSealOrderTypeList", value = "获取所有启用的工单类型列表", method = "GET", allUse = "0")
    @RequestMapping("/post/SealOrderTypeController/queryEnabledSealOrderTypeList")
    public void queryEnabledSealOrderTypeList(InputObject inputObject, OutputObject outputObject) {
        sealOrderTypeService.queryEnabledSealOrderTypeList(inputObject, outputObject);
    }

    @ApiOperation(id = "designSealOrderTypeById", value = "对工单类型进行设计", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "工单类型id", required = "required"),
        @ApiImplicitParam(id = "startTime", name = "startTime", value = "工单提交开始时间", required = "required"),
        @ApiImplicitParam(id = "endTime", name = "endTime", value = "工单提交结束时间", required = "required"),
        @ApiImplicitParam(id = "isAllowAllStaff", name = "isAllowAllStaff", value = "是否允许所有人接单", enumClass = WhetherEnum.class, required = "required,num"),
        @ApiImplicitParam(id = "allowedStaffId", name = "allowedStaffId", value = "允许接单的人员ID列表", required = "json")})
    @RequestMapping("/post/SealOrderTypeController/designSealOrderTypeById")
    public void designSealOrderTypeById(InputObject inputObject, OutputObject outputObject) {
        sealOrderTypeService.designSealOrderTypeById(inputObject, outputObject);
    }

}

