/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.procedure.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.procedure.entity.WayProcedure;
import com.skyeye.procedure.service.WayProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WayProcedureController
 * @Description: 工艺路线管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/24 22:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工艺路线管理", tags = "工艺路线管理", modelName = "工艺路线管理")
public class WayProcedureController {

    @Autowired
    private WayProcedureService wayProcedureService;

    @ApiOperation(id = "erpwayprocedure001", value = "查询工艺列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WayProcedureController/queryWayProcedureList")
    public void queryWayProcedureList(InputObject inputObject, OutputObject outputObject) {
        wayProcedureService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeWayProcedure", value = "新增/编辑工艺信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = WayProcedure.class)
    @RequestMapping("/post/WayProcedureController/writeWayProcedure")
    public void writeWayProcedure(InputObject inputObject, OutputObject outputObject) {
        wayProcedureService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteWayProcedureById", value = "删除工艺", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WayProcedureController/deleteWayProcedureById")
    public void deleteWayProcedureById(InputObject inputObject, OutputObject outputObject) {
        wayProcedureService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProcedureListByWayId", value = "获取工艺下的工序列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "工艺id", required = "required")})
    @RequestMapping("/post/WayProcedureController/queryProcedureListByWayId")
    public void queryProcedureListByWayId(InputObject inputObject, OutputObject outputObject) {
        wayProcedureService.queryProcedureListByWayId(inputObject, outputObject);
    }

    @ApiOperation(id = "publishProcedureVersionById", value = "根据id发布工艺路线", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "工艺路线id", required = "required")})
    @RequestMapping("/post/WayProcedureController/publishProcedureVersionById")
    public void publishProcedureVersionById(InputObject inputObject, OutputObject outputObject) {
        wayProcedureService.publishVersionById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllPublishProcedureList", value = "获取所有工艺列表", method = "GET", allUse = "2")
    @RequestMapping("/post/WayProcedureController/queryAllPublishProcedureList")
    public void queryAllPublishProcedureList(InputObject inputObject, OutputObject outputObject) {
        wayProcedureService.queryAllPublishProcedureList(inputObject, outputObject);
    }

}
