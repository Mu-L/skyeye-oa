package com.skyeye.school.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.personnel.service.SysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "员工管理", tags = "员工管理", modelName = "员工管理")
public class SysEveUserStaffController {

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    /**
     * 获取员工列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySysUserStaffList", value = "查看所有员工列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveUserStaff.class)
    @RequestMapping("/post/SysEveUserStaffController/querySysUserStaffList")
    public void querySysUserStaffList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.queryPageList(inputObject, outputObject);
    }


    @ApiOperation(id = "querySysUserStaffByUserId", value = "根据用户id查询员工信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "userId", name = "userId", value = "用户id", required = "required"),
            @ApiImplicitParam(id = "id", name = "id", value = "员工id")})
    @RequestMapping("/post/SysEveUserStaffController/querySysUserStaffByUserId")
    public void querySysUserStaffByUserId(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffService.querySysUserStaffByUserId(inputObject, outputObject);
    }



}
