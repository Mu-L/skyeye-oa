/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotStaffVO;
import com.skyeye.depot.service.DepotStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DepotStaffController
 * @Description: 仓库与员工的关系管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:13
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "仓库与员工的关系管理", tags = "仓库与员工的关系管理", modelName = "仓库与员工的关系管理")
public class DepotStaffController {

    @Autowired
    private DepotStaffService depotStaffService;

    @ApiOperation(id = "queryDepotStaffList", value = "获取仓库下的员工信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotStaffController/queryDepotStaffList")
    public void queryDepotStaffList(InputObject inputObject, OutputObject outputObject) {
        depotStaffService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDepotStaffById", value = "删除仓库下的员工信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "仓库与员工的关系表主键id", required = "required")})
    @RequestMapping("/post/DepotStaffController/deleteDepotStaffById")
    public void deleteDepotStaffById(InputObject inputObject, OutputObject outputObject) {
        depotStaffService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertDepotStaff", value = "新增仓库下的员工信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DepotStaffVO.class)
    @RequestMapping("/post/DepotStaffController/insertDepotStaff")
    public void insertDepotStaff(InputObject inputObject, OutputObject outputObject) {
        depotStaffService.insertDepotStaff(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStaffBelongDepotList", value = "获取当前登陆用户所属的仓库列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "启用状态", enumClass = EnableEnum.class)})
    @RequestMapping("/post/DepotStaffController/queryStaffBelongDepotList")
    public void queryStaffBelongDepotList(InputObject inputObject, OutputObject outputObject) {
        depotStaffService.queryStaffBelongDepotList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteDepotStaffByStaffId", value = "根据员工id删除所有的所属仓库信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "员工id", required = "required")})
    @RequestMapping("/post/DepotStaffController/deleteDepotStaffByStaffId")
    public void deleteDepotStaffByStaffId(InputObject inputObject, OutputObject outputObject) {
        depotStaffService.deleteDepotStaffByStaffId(inputObject, outputObject);
    }

}
