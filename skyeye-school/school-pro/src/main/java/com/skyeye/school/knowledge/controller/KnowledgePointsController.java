/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.knowledge.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.knowledge.entity.KnowledgePoints;
import com.skyeye.school.knowledge.service.KnowledgePointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: KnowledgePointsController
 * @Description: 知识点管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
@RestController
@Api(value = "知识点管理", tags = "知识点管理", modelName = "知识点管理")
public class KnowledgePointsController {

    @Autowired
    private KnowledgePointsService knowledgePointsService;

    /**
     * 获取知识点信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryKnowledgePointsList", value = "获取知识点信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/KnowledgePointsController/queryKnowledgePointsList")
    public void queryKnowledgePointsList(InputObject inputObject, OutputObject outputObject) {
        knowledgePointsService.queryKnowledgePointsList(inputObject, outputObject);
    }

    /**
     * 增加/修改知识点信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeKnowledgePoints", value = "新增/编辑知识点信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = KnowledgePoints.class)
    @RequestMapping("/post/KnowledgePointsController/writeKnowledgePoints")
    public void writeKnowledgePoints(InputObject inputObject, OutputObject outputObject) {
        knowledgePointsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询知识点信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryKnowledgePointsById", value = "根据id查询知识点信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/KnowledgePointsController/queryKnowledgePointsById")
    public void queryKnowledgePointsById(InputObject inputObject, OutputObject outputObject) {
        knowledgePointsService.selectById(inputObject, outputObject);
    }

    /**
     * 删除知识点信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteKnowledgePointsById", value = "根据ID删除知识点信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/KnowledgePointsController/deleteKnowledgePointsById")
    public void deleteKnowledgePointsById(InputObject inputObject, OutputObject outputObject) {
        knowledgePointsService.deleteById(inputObject, outputObject);
    }
}