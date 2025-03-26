/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserStaffTeacher;
import com.skyeye.personnel.service.SysEveUserStaffTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveUserStaffTeacherController
 * @Description: 员工所属学校关系控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/12 23:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工所属学校关系管理", tags = "员工所属学校关系管理", modelName = "员工管理")
public class SysEveUserStaffTeacherController {

    @Autowired
    private SysEveUserStaffTeacherService sysEveUserStaffTeacherService;

    /**
     * 普通员工转教职工
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "staff007", value = "普通员工转教职工", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysEveUserStaffTeacher.class)
    @RequestMapping("/post/SysEveUserStaffTeacherController/createStaffTeacherLink")
    public void createStaffTeacherLink(InputObject inputObject, OutputObject outputObject) {
        sysEveUserStaffTeacherService.createEntity(inputObject, outputObject);
    }

}
