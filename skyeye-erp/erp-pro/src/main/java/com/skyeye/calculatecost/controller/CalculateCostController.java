package com.skyeye.calculatecost.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.calculatecost.service.CalculateCostService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "工序/部门加工单/加工入库单核算", tags = "工序/部门加工单/加工入库单核算", modelName = "工序/部门加工单/加工入库单核算")
public class CalculateCostController {

    @Autowired
    private CalculateCostService calculateCostService;

    @ApiOperation(id = "calculateMachinProcedureAcceptCost", value = "工序验收核算", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "machinProcedureAcceptId", name = "machinProcedureAcceptId", value = "工序验收单主键id", required = "required")})
    @RequestMapping("/post/CalculateCostController/calculateMachinProcedureAcceptCost")
    public void calculateMachinProcedureAcceptCost(InputObject inputObject, OutputObject outputObject) {
        calculateCostService.calculateMachinProcedureAcceptCost(inputObject, outputObject);
    }

    @ApiOperation(id = "calculateMachinProcedureCost", value = "工序核算", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "machinProcedureId", name = "machinProcedureId", value = "加工单子单据工序id", required = "required")})
    @RequestMapping("/post/CalculateCostController/calculateMachinProcedureCost")
    public void calculateMachinProcedureCost(InputObject inputObject, OutputObject outputObject) {
        calculateCostService.calculateMachinProcedureCost(inputObject, outputObject);
    }

    @ApiOperation(id = "calculateMachinPutCost", value = "加工入库单核算", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "machinPutId", name = "machinPutId", value = "加工入库单id", required = "required")})
    @RequestMapping("/post/CalculateCostController/calculateMachinPutCost")
    public void calculateMachinPutCost(InputObject inputObject, OutputObject outputObject) {
        calculateCostService.calculateMachinPutCost(inputObject, outputObject);
    }

    /**
     * 部门加工单核算：根据加工单 id 汇总该单下各子件（商品）的工序成本，返回耗材、工资、总价及工序/验收/员工报工明细。
     * 无验收时仍返回完整商品与工序结构，数量与金额为 0，供统计报表展示。
     */
    @ApiOperation(id = "calculateMachinCost", value = "部门加工单核算", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "machinId", name = "machinId", value = "加工单id", required = "required")})
    @RequestMapping("/post/CalculateCostController/calculateMachinCost")
    public void calculateMachinCost(InputObject inputObject, OutputObject outputObject) {
        calculateCostService.calculateMachinCost(inputObject, outputObject);
    }
}
