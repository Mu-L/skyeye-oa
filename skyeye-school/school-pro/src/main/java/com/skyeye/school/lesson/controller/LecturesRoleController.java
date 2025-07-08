package com.skyeye.school.lesson.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lesson.entity.LecturesRole;
import com.skyeye.school.lesson.service.LecturesRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LecturesRoleController
 * @Description: 听评课角色管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@RestController
@Api(value = "听评课角色管理", tags = "听评课角色管理", modelName = "听评课角色管理")
public class LecturesRoleController {

    @Autowired
    private LecturesRoleService lecturesRoleService;

    @ApiOperation(id = "queryLecturesRoleLists", value = "获取听评角色", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LecturesRoleController/queryLecturesRoleLists")
    public void queryLecturesRoleLists(InputObject inputObject, OutputObject outputObject) {
        lecturesRoleService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeLecturesRole", value = "新增/编辑角色信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LecturesRole.class)
    @RequestMapping("/post/LecturesRoleController/writeLecturesRole")
    public void writeLecturesRole(InputObject inputObject, OutputObject outputObject) {
        lecturesRoleService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLecturesRoleById", value = "根据id获取角色信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "角色id", required = "required")
    })
    @RequestMapping("/post/LecturesRoleController/queryLecturesRoleById")
    public void queryLecturesRoleById(InputObject inputObject, OutputObject outputObject) {
        lecturesRoleService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLecturesRoleById", value = "根据id删除角色信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "角色id", required = "required")
    })
    @RequestMapping("/post/LecturesRoleController/deleteLecturesRoleById")
    public void deleteLecturesRoleById(InputObject inputObject, OutputObject outputObject) {
        lecturesRoleService.deleteById(inputObject, outputObject);
    }
}
