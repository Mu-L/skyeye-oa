/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.controller;

import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.afterseal.service.AfterSealService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AfterSealController
 * @Description: 工单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/10 13:14
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "工单管理", tags = "工单管理", modelName = "工单管理")
public class AfterSealController {

    @Autowired
    private AfterSealService afterSealService;

    @ApiOperation(id = "querySealServiceOrderList", value = "获取工单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AfterSealController/querySealServiceOrderList")
    public void querySealServiceOrderList(InputObject inputObject, OutputObject outputObject) {
        afterSealService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSealServiceOrder", value = "新增/编辑售后服务工单信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AfterSeal.class)
    @RequestMapping("/post/AfterSealController/writeSealServiceOrder")
    public void writeSealServiceOrder(InputObject inputObject, OutputObject outputObject) {
        afterSealService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sealseservice014", value = "派工", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "serviceUserId", name = "serviceUserId", value = "接收人", required = "required"),
        @ApiImplicitParam(id = "cooperationUserId", name = "cooperationUserId", value = "协助人", required = "json")})
    @RequestMapping("/post/AfterSealController/editSealSeServiceWaitToWorkMation")
    public void editSealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject) {
        afterSealService.editSealSeServiceWaitToWorkMation(inputObject, outputObject);
    }

    @ApiOperation(id = "sealseservice017", value = "接单", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AfterSealController/receivingSealSeServiceOrderById")
    public void receivingSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        afterSealService.receivingSealSeServiceOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealServiceOrderById", value = "根据id查询售后服务信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AfterSealController/querySealServiceOrderById")
    public void querySealServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        afterSealService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSealServiceOrderById", value = "删除售后服务信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AfterSealController/deleteSealSeServiceMationById")
    public void deleteSealSeServiceMationById(InputObject inputObject, OutputObject outputObject) {
        afterSealService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "sealseservice022", value = "查询我负责的待完工状态的工单", method = "GET", allUse = "2")
    @RequestMapping("/post/AfterSealController/querySealSeServiceSignon")
    public void querySealSeServiceSignon(InputObject inputObject, OutputObject outputObject) {
        afterSealService.querySealSeServiceSignon(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyParticipatedPendingCompletedOrders", value = "查询我参与的待完工工单（作为负责人或协助人）", method = "GET", allUse = "2")
    @RequestMapping("/post/AfterSealController/queryMyParticipatedPendingCompletedOrders")
    public void queryMyParticipatedPendingCompletedOrders(InputObject inputObject, OutputObject outputObject) {
        afterSealService.queryMyParticipatedPendingCompletedOrders(inputObject, outputObject);
    }

    @ApiOperation(id = "sealseservice035", value = "完工操作", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AfterSealController/auditSealSeServiceOrderById")
    public void auditSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        afterSealService.auditSealSeServiceOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "sealseservice038", value = "完工审核操作", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AfterSealController/finishSealSeServiceOrderById")
    public void finishSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject) {
        afterSealService.finishSealSeServiceOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOverviewSealSeServiceOrder", value = "查询售后服务总览信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealController/queryOverviewSealSeServiceOrder")
    public void queryOverviewSealSeServiceOrder(InputObject inputObject, OutputObject outputObject) {
        afterSealService.queryOverviewSealSeServiceOrder(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealSeServiceOrderTypeStats", value = "查询售后服务类型统计信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealController/querySealSeServiceOrderTypeStats")
    public void querySealSeServiceOrderTypeStats(InputObject inputObject, OutputObject outputObject) {
        afterSealService.querySealSeServiceOrderTypeStats(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealSeServiceOrderTrendStats", value = "获取工单趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealController/querySealSeServiceOrderTrendStats")
    public void querySealSeServiceOrderTrendStats(InputObject inputObject, OutputObject outputObject) {
        afterSealService.querySealSeServiceOrderTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "querySealServiceOrderWorkerStats", value = "获取工人效率统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealController/querySealServiceOrderWorkerStats")
    public void querySealServiceOrderWorkerStats(InputObject inputObject, OutputObject outputObject) {
        afterSealService.querySealServiceOrderWorkerStats(inputObject, outputObject);
    }

}
