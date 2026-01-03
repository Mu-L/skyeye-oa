/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.procedure.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.procedure.entity.WorkProcedure;
import com.skyeye.procedure.service.WorkProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WorkProcedureController
 * @Description: 工序管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/23 21:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工序管理", tags = "工序管理", modelName = "工序管理")
public class WorkProcedureController {

    @Autowired
    private WorkProcedureService workProcedureService;

    @ApiOperation(id = "erpworkprocedure001", value = "查询工序列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WorkProcedureController/queryWorkProcedureList")
    public void queryWorkProcedureList(InputObject inputObject, OutputObject outputObject) {
        workProcedureService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeWorkProcedure", value = "新增/编辑工序信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = WorkProcedure.class)
    @RequestMapping("/post/WorkProcedureController/writeWorkProcedure")
    public void writeWorkProcedure(InputObject inputObject, OutputObject outputObject) {
        workProcedureService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteWorkProcedureById", value = "删除工作流配置", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WorkProcedureController/deleteWorkProcedureById")
    public void deleteWorkProcedureById(InputObject inputObject, OutputObject outputObject) {
        workProcedureService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "erpworkprocedure009", value = "查询所有工序列表信息", method = "GET", allUse = "2")
    @RequestMapping("/post/WorkProcedureController/queryAllWorkProcedureList")
    public void queryAllWorkProcedureList(InputObject inputObject, OutputObject outputObject) {
        workProcedureService.queryAllWorkProcedureList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryExecuteFarmByWorkProcedureId", value = "根据工序id查询可以执行该工序的车间", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "workProcedureId", name = "workProcedureId", value = "工序id", required = "required")})
    @RequestMapping("/post/WorkProcedureController/queryExecuteFarmByWorkProcedureId")
    public void queryExecuteFarmByWorkProcedureId(InputObject inputObject, OutputObject outputObject) {
        workProcedureService.queryExecuteFarmByWorkProcedureId(inputObject, outputObject);
    }

}
