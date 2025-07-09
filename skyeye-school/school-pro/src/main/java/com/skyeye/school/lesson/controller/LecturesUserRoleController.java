package com.skyeye.school.lesson.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lesson.entity.LecturesRole;
import com.skyeye.school.lesson.entity.LecturesUserRole;
import com.skyeye.school.lesson.service.LecturesUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LecturesUserRoleController
 * @Description: 用户角色关联管理
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@RestController
@Api(value = "用户角色关联管理", tags = "用户角色关联管理", modelName = "用户角色关联管理")
public class LecturesUserRoleController {

    @Autowired
    private LecturesUserRoleService lecturesUserRoleService;

    @ApiOperation(id = "queryLecturesUserRoleLists", value = "根据(objectId)角色id获取用户列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LecturesUserRoleController/queryLecturesUserRoleLists")
    public void queryLecturesRoleLists(InputObject inputObject, OutputObject outputObject) {
        lecturesUserRoleService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeLecturesUserRole", value = "新增/编辑用户角色关联信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LecturesUserRole.class)
    @RequestMapping("/post/LecturesUserRoleController/writeLecturesRole")
    public void writeLecturesRole(InputObject inputObject, OutputObject outputObject) {
        lecturesUserRoleService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLecturesUserRoleById", value = "根据id获用户角色信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/LecturesUserRoleController/queryLecturesUserRoleById")
    public void queryLecturesUserRoleById(InputObject inputObject, OutputObject outputObject) {
        lecturesUserRoleService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLecturesUserRoleById", value = "根据id删除用户角色关联信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/LecturesUserRoleController/deleteLecturesUserRoleById")
    public void deleteLecturesUserRoleById(InputObject inputObject, OutputObject outputObject) {
        lecturesUserRoleService.deleteById(inputObject, outputObject);
    }
}
