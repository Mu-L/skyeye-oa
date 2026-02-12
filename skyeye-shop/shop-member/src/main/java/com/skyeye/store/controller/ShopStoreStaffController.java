/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ShopStoreStaffVO;
import com.skyeye.store.service.ShopStoreStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopStoreStaffController
 * @Description: 门店与员工的关系控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:13
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店员工关系管理", tags = "门店员工关系管理", modelName = "门店员工关系管理")
public class ShopStoreStaffController {

    @Autowired
    private ShopStoreStaffService shopStoreStaffService;

    @ApiOperation(id = "storeStaff001", value = "获取门店下的员工信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ShopStoreStaffController/queryStoreStaffList")
    public void queryStoreStaffList(InputObject inputObject, OutputObject outputObject) {
        shopStoreStaffService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "storeStaff002", value = "删除门店下的员工信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "门店与员工的关系表主键id", required = "required")})
    @RequestMapping("/post/ShopStoreStaffController/deleteStoreStaffMationById")
    public void deleteStoreStaffMationById(InputObject inputObject, OutputObject outputObject) {
        shopStoreStaffService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "storeStaff003", value = "新增门店下的员工信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ShopStoreStaffVO.class)
    @RequestMapping("/post/ShopStoreStaffController/insertStoreStaffMation")
    public void insertStoreStaffMation(InputObject inputObject, OutputObject outputObject) {
        shopStoreStaffService.insertStoreStaffMation(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStaffListByStoreId", value = "根据门店ID获取员工信息列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店ID")})
    @RequestMapping("/post/ShopStoreStaffController/queryStaffListByStoreId")
    public void queryStaffListByStoreId(InputObject inputObject, OutputObject outputObject) {
        shopStoreStaffService.queryStaffListByStoreId(inputObject, outputObject);
    }

    @ApiOperation(id = "storeStaff004", value = "获取当前登陆用户所属的区域列表", method = "GET", allUse = "2")
    @RequestMapping("/post/ShopStoreStaffController/queryStaffBelongAreaList")
    public void queryStaffBelongAreaList(InputObject inputObject, OutputObject outputObject) {
        shopStoreStaffService.queryStaffBelongAreaList(inputObject, outputObject);
    }

    @ApiOperation(id = "storeStaff005", value = "获取当前登陆用户所属的门店列表(只包含已启用门店)", method = "GET", allUse = "2")
    @RequestMapping("/post/ShopStoreStaffController/queryStaffBelongStoreList")
    public void queryStaffBelongStoreList(InputObject inputObject, OutputObject outputObject) {
        shopStoreStaffService.queryStaffBelongStoreList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteStoreStaffMationByStaffId", value = "根据员工id删除所有的所属门店信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "员工id", required = "required")})
    @RequestMapping("/post/ShopStoreStaffController/deleteStoreStaffMationByStaffId")
    public void deleteStoreStaffMationByStaffId(InputObject inputObject, OutputObject outputObject) {
        shopStoreStaffService.deleteStoreStaffMationByStaffId(inputObject, outputObject);
    }

}
