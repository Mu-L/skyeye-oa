/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.vehicle.entity.Inspection;
import com.skyeye.eve.vehicle.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: InspectionController
 * @Description: 车辆年检管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 10:15
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车辆年检管理", tags = "车辆年检管理", modelName = "车辆模块")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    /**
     * 遍历所有的年检列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "inspection001", value = "遍历所有的年检列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InspectionController/queryInspectionList")
    public void queryInspectionList(InputObject inputObject, OutputObject outputObject) {
        inspectionService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/修改年检信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeVehicleInspection", value = "新增/修改年检信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Inspection.class)
    @RequestMapping("/post/InspectionController/writeVehicleInspection")
    public void writeVehicleInspection(InputObject inputObject, OutputObject outputObject) {
        inspectionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id删除年检信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "inspection003", value = "根据id删除年检信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/InspectionController/deleteInspectionById")
    public void deleteInspectionById(InputObject inputObject, OutputObject outputObject) {
        inspectionService.deleteById(inputObject, outputObject);
    }
}
