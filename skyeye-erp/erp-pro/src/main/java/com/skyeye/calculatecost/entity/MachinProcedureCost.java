package com.skyeye.calculatecost.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.mchange.lang.StringUtils;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptChild;
import com.skyeye.material.entity.Material;
import lombok.Data;

import java.util.List;

@Data
public class MachinProcedureCost {

    @ApiModelProperty(value = "商品id")
    private String materialId;

    @ApiModelProperty(value = "规格id")
    private String normsId;

    @ApiModelProperty(value = "该工序生产的商品名称")
    private String materialName;

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
    private String allNum;

    @ApiModelProperty(value = "合格数量")
    private String qualifiedNum;

    @ApiModelProperty(value = "返工数量")
    private String reworkNum;

    @ApiModelProperty(value = "报废数量")
    private String scrapNum;

    @ApiModelProperty(value = "工资金额")
    private String wage;

    @ApiModelProperty(value = "总价")
    private String totalPrice;
}
