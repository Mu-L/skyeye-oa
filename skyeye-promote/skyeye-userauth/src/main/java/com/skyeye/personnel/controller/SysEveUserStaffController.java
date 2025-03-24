/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.entity.SysEveUserStaffQuery;
import com.skyeye.personnel.service.SysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveUserStaffController
 * @Description: 员工管理开始
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/12 23:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工管理", tags = "员工管理", modelName = "员工管理")
public class SysEveUserStaffController {

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @ApiOperation(id = "querySysUserStaffList", value = "查看所有员工列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveUserStaffQuery.class)
    @RequestMapping("/post/SysEveUserStaffController/querySysUserStaffList")
    public void querySysUserStaffList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSysUserStaff", value = "新增/编辑员工信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveUserStaff.class)
    @RequestMapping("/post/SysEveUserStaffController/writeSysUserStaff")
    public void writeSysUserStaff(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySysUserStaffById", value = "根据id查询员工信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "员工id", required = "required")})
    @RequestMapping("/post/SysEveUserStaffController/querySysUserStaffById")
    public void querySysUserStaffById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySysUserStaffByUserId", value = "根据用户id查询员工信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户id", required = "required")})
    @RequestMapping("/post/SysEveUserStaffController/querySysUserStaffByUserId")
    public void querySysUserStaffByUserId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.querySysUserStaffByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "staff006", value = "员工离职", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "员工id", required = "required"),
        @ApiImplicitParam(id = "quitTime", name = "quitTime", value = "离职时间"),
        @ApiImplicitParam(id = "quitReason", name = "quitReason", value = "离职原因")})
    @RequestMapping("/post/SysEveUserStaffController/editSysUserStaffState")
    public void editSysUserStaffState(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.editSysUserStaffState(inputObject, outputObject);
    }

    @ApiOperation(id = "staff010", value = "获取当前登录员工的信息", method = "GET", allUse = "2")
    @RequestMapping("/post/SysEveUserStaffController/querySysUserStaffLogin")
    public void querySysUserStaffLogin(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.querySysUserStaffLogin(inputObject, outputObject);
    }

    /**
     * 根据用户ids/员工ids获取员工信息集合
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUserMationList", value = "根据用户ids/员工ids获取员工信息集合", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userIds", name = "userIds", value = "用户id，多个逗号隔离(两个参数传一个即可，默认优先以userIds查询为主)"),
        @ApiImplicitParam(id = "staffIds", name = "staffIds", value = "员工id，多个逗号隔开(两个参数传一个即可，默认优先以userIds查询为主)")})
    @RequestMapping("/post/SysEveUserStaffController/queryUserMationList")
    public void queryUserMationList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.queryUserMationList(inputObject, outputObject);
    }

    /**
     * 修改员工剩余年假信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "editSysUserStaffAnnualLeaveById", value = "修改员工剩余年假信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required"),
        @ApiImplicitParam(id = "quarterYearHour", name = "quarterYearHour", value = "年假,精确到6位", required = "required"),
        @ApiImplicitParam(id = "annualLeaveStatisTime", name = "annualLeaveStatisTime", value = "员工剩余年假数据刷新日期", required = "required")})
    @RequestMapping("/post/SysEveUserStaffController/editSysUserStaffAnnualLeaveById")
    public void editSysUserStaffAnnualLeaveById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.editSysUserStaffAnnualLeaveById(inputObject, outputObject);
    }

    /**
     * 修改员工的补休池剩余补休信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "updateSysUserStaffHolidayNumberById", value = "修改员工的补休池剩余补休信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required"),
        @ApiImplicitParam(id = "holidayNumber", name = "holidayNumber", value = "当前员工剩余补休天数", required = "required"),
        @ApiImplicitParam(id = "holidayStatisTime", name = "holidayStatisTime", value = "员工剩余补休数据刷新日期", required = "required")})
    @RequestMapping("/post/SysEveUserStaffController/updateSysUserStaffHolidayNumberById")
    public void updateSysUserStaffHolidayNumberById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.updateSysUserStaffHolidayNumberById(inputObject, outputObject);
    }

    /**
     * 修改员工的补休池已休补休信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "updateSysUserStaffRetiredHolidayNumberById", value = "修改员工的补休池已休补休信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required"),
        @ApiImplicitParam(id = "retiredHolidayNumber", name = "retiredHolidayNumber", value = "当前员工已休补休天数", required = "required"),
        @ApiImplicitParam(id = "retiredHolidayStatisTime", name = "retiredHolidayStatisTime", value = "员工已休补休数据刷新日期", required = "required")})
    @RequestMapping("/post/SysEveUserStaffController/updateSysUserStaffRetiredHolidayNumberById")
    public void updateSysUserStaffRetiredHolidayNumberById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.updateSysUserStaffRetiredHolidayNumberById(inputObject, outputObject);
    }

    /**
     * 根据员工id获取该员工的考勤时间段
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryStaffCheckWorkTimeRelationNameByStaffId", value = "根据员工id获取该员工的考勤时间段", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required")})
    @RequestMapping("/post/SysEveUserStaffController/queryStaffCheckWorkTimeRelationNameByStaffId")
    public void queryStaffCheckWorkTimeRelationNameByStaffId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.queryStaffCheckWorkTimeRelationNameByStaffId(inputObject, outputObject);
    }

    /**
     * 获取所有在职的，拥有账号的员工
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "commonselpeople007", value = "获取所有在职的，拥有账号的员工", method = "POST", allUse = "2")
    @RequestMapping("/post/SysEveUserStaffController/queryAllSysUserIsIncumbency")
    public void queryAllSysUserIsIncumbency(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.queryAllSysUserIsIncumbency(inputObject, outputObject);
    }

    /**
     * 修改员工薪资设定信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "editSysUserStaffActMoneyById", value = "修改员工薪资设定信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required"),
        @ApiImplicitParam(id = "actMoney", name = "actMoney", value = "员工月标准工资", required = "required,double")})
    @RequestMapping("/post/SysEveUserStaffController/editSysUserStaffActMoneyById")
    public void editSysUserStaffActMoneyById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.editSysUserStaffActMoneyById(inputObject, outputObject);
    }

    /**
     * 根据姓名和工号查询员工
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectByName", value = "根据姓名和工号查询员工", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "员工姓名"),
            @ApiImplicitParam(id = "keyword", name = "keyword", value = "员工工号"),
            @ApiImplicitParam(id = "limit", name = "limit", value = "限制次数"),
            @ApiImplicitParam(id = "pages", name = "pages", value = "限制页数")})
    @RequestMapping("/post/SysEveUserStaffController/selectByName")
    public void selectByName(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.selectByName(inputObject, outputObject);
    }

    /**
     * 根据Id查询员工
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectByObjectId", value = "根据Id查询员工", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "objectId", name = "objectId", value = "员工Id")})
    @RequestMapping("/post/SysEveUserStaffController/selectByObjectId")
    public void selectByObjectId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.selectByObjectId(inputObject, outputObject);
    }

}
