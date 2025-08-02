package com.skyeye.calculatecost.entity;

import com.mchange.lang.StringUtils;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptChild;
import lombok.Data;

import java.util.List;

@Data
public class MachinProcedureCost {

    @ApiModelProperty(value = "工序编号")
    private String procedureNumber;

    @ApiModelProperty(value = "工序名称")
    private String procedureName;

    @ApiModelProperty(value = "验收的成本信息")
    private List<MachinProcedureAcceptCost> acceptCostList;

    @ApiModelProperty(value = "耗材成本")
    private String consumablePrice;

    @ApiModelProperty(value = "报废耗材成本")
    private String scrapConsumablePrice;

    @ApiModelProperty(value = "正常耗材成本")
    private String normalConsumablePrice;

    @ApiModelProperty(value = "总数量")
    private Integer allNum;

    @ApiModelProperty(value = "合格数量")
    private Integer qualifiedNum;

    @ApiModelProperty(value = "返工数量")
    private Integer reworkNum;

    @ApiModelProperty(value = "报废数量")
    private Integer scrapNum;

    @ApiModelProperty(value = "加工单价")
    private String price;

    @ApiModelProperty(value = "工资金额")
    private String wage;

    @ApiModelProperty(value = "总价")
    private String totalPrice;
}
