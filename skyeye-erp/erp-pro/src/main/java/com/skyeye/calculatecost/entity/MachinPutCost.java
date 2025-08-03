package com.skyeye.calculatecost.entity;

import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptChild;
import lombok.Data;

import java.util.List;

@Data
public class MachinPutCost {

    @ApiModelProperty(value = "商品名")
    private String materialName;

    @ApiModelProperty(value = "工序成本信息")
    private List<MachinProcedureCost> machinProcedureCostList;

    @ApiModelProperty(value = "耗材成本")
    private String consumablePrice;

    @ApiModelProperty(value = "报废耗材成本")
    private String scrapConsumablePrice;

    @ApiModelProperty(value = "正常耗材成本")
    private String normalConsumablePrice;

    @ApiModelProperty(value = "总数量")
    private Integer allNum;

    @ApiModelProperty(value = "加工单价")
    private String price;

    @ApiModelProperty(value = "工资金额")
    private String wage;

    @ApiModelProperty(value = "总价")
    private String totalPrice;
}
