/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolTeamMember;
import com.skyeye.patrol.service.PatrolTeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolTeamMemberController
 * @Description: 巡检班组人员控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检班组人员", tags = "巡检班组人员", modelName = "巡检班组人员")
public class PatrolTeamMemberController {

    @Autowired
    private PatrolTeamMemberService patrolTeamMemberService;

    @ApiOperation(id = "queryPatrolTeamMemberList", value = "获取巡检班组人员列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolTeamMemberController/queryPatrolTeamMemberList")
    public void queryPatrolTeamMemberList(InputObject inputObject, OutputObject outputObject) {
        patrolTeamMemberService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePatrolTeamMember", value = "新增/编辑巡检班组人员", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatrolTeamMember.class)
    @RequestMapping("/post/PatrolTeamMemberController/writePatrolTeamMember")
    public void writePatrolTeamMember(InputObject inputObject, OutputObject outputObject) {
        patrolTeamMemberService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePatrolTeamMemberById", value = "根据ID删除巡检班组人员", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolTeamMemberController/deletePatrolTeamMemberById")
    public void deletePatrolTeamMemberById(InputObject inputObject, OutputObject outputObject) {
        patrolTeamMemberService.deleteById(inputObject, outputObject);
    }

}

