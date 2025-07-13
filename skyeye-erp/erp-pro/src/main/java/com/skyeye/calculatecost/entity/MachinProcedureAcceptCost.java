package com.skyeye.calculatecost.entity;

import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptChild;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptProductNum;
import lombok.Data;

import java.util.List;

/**
 * 工序核算实体类
 */
@Data
public class MachinProcedureAcceptCost {

    @ApiModelProperty(value = "员工生产数量信息列表")
    private List<MachinProcedureAcceptProductNum> productNumMationList;

    @ApiModelProperty(value = "工序信息")
    private MachinProcedure MachinProcedureMation;

    @ApiModelProperty(value = "耗材信息列表")
    private List<MachinProcedureAcceptChild> childList;

    @ApiModelProperty(value = "耗材成本")
    private String consumablePrice;

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

    @ApiModelProperty(value = "工序金额") // 未计算
    private String MachinProcedurePrice;

    @ApiModelProperty(value = "工资金额")
    private String wage;

    @ApiModelProperty(value = "总价")
    private String totalPrice;
}
