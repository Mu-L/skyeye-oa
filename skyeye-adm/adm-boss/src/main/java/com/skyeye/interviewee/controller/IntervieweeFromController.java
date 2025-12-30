/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.interviewee.entity.IntervieweeFrom;
import com.skyeye.interviewee.service.IntervieweeFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IntervieweeFromController
 * @Description: 面试者来源管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/7 13:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "面试者来源管理", tags = "面试者来源管理", modelName = "面试者管理")
public class IntervieweeFromController {

    @Autowired
    private IntervieweeFromService bossIntervieweeFromService;

    /**
     * 获取面试者来源信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "bossIntervieweeFrom001", value = "获取面试者来源信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/BossIntervieweeFromController/queryBossIntervieweeFromList")
    public void queryBossIntervieweeFromList(InputObject inputObject, OutputObject outputObject) {
        bossIntervieweeFromService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑面试者来源信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeBossIntervieweeFrom", value = "新增/编辑面试者来源信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = IntervieweeFrom.class)
    @RequestMapping("/post/BossIntervieweeFromController/writeBossIntervieweeFrom")
    public void writeBossIntervieweeFrom(InputObject inputObject, OutputObject outputObject) {
        bossIntervieweeFromService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除面试者来源信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteBossIntervieweeFrom", value = "删除面试者来源信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键ID", required = "required")})
    @RequestMapping("/post/BossIntervieweeFromController/deleteBossIntervieweeFrom")
    public void deleteBossIntervieweeFrom(InputObject inputObject, OutputObject outputObject) {
        bossIntervieweeFromService.deleteById(inputObject, outputObject);
    }

    /**
     * 获取所有的面试者来源信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllBossIntervieweeFrom", value = "获取所有的面试者来源信息", method = "GET", allUse = "2")
    @RequestMapping("/post/BossIntervieweeFromController/queryAllBossIntervieweeFrom")
    public void queryAllBossIntervieweeFrom(InputObject inputObject, OutputObject outputObject) {
        bossIntervieweeFromService.queryAllBossIntervieweeFrom(inputObject, outputObject);
    }

}
