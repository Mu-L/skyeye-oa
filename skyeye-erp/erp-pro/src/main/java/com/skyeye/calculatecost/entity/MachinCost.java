package com.skyeye.calculatecost.entity;

import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MachinCost {

    @ApiModelProperty(value = "报废耗材成本")
    private String scrapConsumablePrice;

    @ApiModelProperty(value = "正常耗材成本")
    private String normalConsumablePrice;

    @ApiModelProperty(value = "耗材成本")
    private String consumablePrice;

    @ApiModelProperty(value = "工资金额")
    private String wage;

    @ApiModelProperty(value = "总价")
    private String totalPrice;

    @ApiModelProperty(value = "加工单子单据信息")
    List<MachinPutCost> putCostList;

}
