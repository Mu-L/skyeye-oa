/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.other.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.other.entity.OtherWareHous;
import com.skyeye.other.service.OtherWareHousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: OtherWareHousController
 * @Description: 其他入库单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:08
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "其他入库单", tags = "其他入库单", modelName = "其他订单模块")
public class OtherWareHousController {

    @Autowired
    private OtherWareHousService otherWareHousService;

    /**
     * 获取其他入库单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "otherwarehous001", value = "获取其他入库单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OtherWareHousController/queryOtherWareHousList")
    public void queryOtherWareHousList(InputObject inputObject, OutputObject outputObject) {
        otherWareHousService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑其他入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeOtherWareHous", value = "新增/编辑其他入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = OtherWareHous.class)
    @RequestMapping("/post/OtherWareHousController/writeOtherWareHous")
    public void writeOtherWareHous(InputObject inputObject, OutputObject outputObject) {
        otherWareHousService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 转仓库入库单时，根据id查询其他入库信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryOtherWareHousTransById", value = "转仓库入库单时，根据id查询其他入库信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OtherWareHousController/queryOtherWareHousTransById")
    public void queryOtherWareHousTransById(InputObject inputObject, OutputObject outputObject) {
        otherWareHousService.queryOtherWareHousTransById(inputObject, outputObject);
    }

    /**
     * 其他入库单信息转仓库入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertOtherWareHousToTurnDepot", value = "其他入库单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OtherWareHousController/insertOtherWareHousToTurnDepot")
    public void insertOtherWareHousToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        otherWareHousService.insertOtherWareHousToTurnDepot(inputObject, outputObject);
    }

}
