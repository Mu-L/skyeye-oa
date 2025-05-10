/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.FarmStaffVO;
import com.skyeye.farm.service.FarmStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FarmStaffController
 * @Description: 车间与员工的关系管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 21:13
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "车间与员工的关系管理", tags = "车间与员工的关系管理", modelName = "车间与员工的关系管理")
public class FarmStaffController {

    @Autowired
    private FarmStaffService farmStaffService;

    /**
     * 获取车间下的员工信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFarmStaffList", value = "获取车间下的员工信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FarmStaffController/queryFarmStaffList")
    public void queryFarmStaffList(InputObject inputObject, OutputObject outputObject) {
        farmStaffService.queryPageList(inputObject, outputObject);
    }

    /**
     * 不分页获取全部车间下的员工信息（远程调用）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllFarmStaffList", value = "获取全部车间下的员工信息", method = "POST", allUse = "2")
    @RequestMapping("/post/FarmStaffController/queryAllFarmStaffList")
    public void queryAllFarmStaffList(InputObject inputObject, OutputObject outputObject) {
        farmStaffService.queryAllFarmStaffList(inputObject, outputObject);
    }

    /**
     * 删除车间下的员工信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteFarmStaffById", value = "删除车间下的员工信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "车间与员工的关系表主键id", required = "required")})
    @RequestMapping("/post/FarmStaffController/deleteFarmStaffById")
    public void deleteFarmStaffById(InputObject inputObject, OutputObject outputObject) {
        farmStaffService.deleteById(inputObject, outputObject);
    }

    /**
     * 新增车间下的员工信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertFarmStaff", value = "新增车间下的员工信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = FarmStaffVO.class)
    @RequestMapping("/post/FarmStaffController/insertFarmStaff")
    public void insertFarmStaff(InputObject inputObject, OutputObject outputObject) {
        farmStaffService.insertFarmStaff(inputObject, outputObject);
    }

    /**
     * 获取当前登陆用户所属的车间列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryStaffBelongFarmList", value = "获取当前登陆用户所属的车间列表", method = "GET", allUse = "2")
    @RequestMapping("/post/FarmStaffController/queryStaffBelongFarmList")
    public void queryStaffBelongFarmList(InputObject inputObject, OutputObject outputObject) {
        farmStaffService.queryStaffBelongFarmList(inputObject, outputObject);
    }

    /**
     * 根据员工id删除所有的所属车间信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteFarmStaffByStaffId", value = "根据员工id删除所有的所属车间信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "员工id", required = "required")})
    @RequestMapping("/post/FarmStaffController/deleteFarmStaffByStaffId")
    public void deleteFarmStaffByStaffId(InputObject inputObject, OutputObject outputObject) {
        farmStaffService.deleteFarmStaffByStaffId(inputObject, outputObject);
    }

}
