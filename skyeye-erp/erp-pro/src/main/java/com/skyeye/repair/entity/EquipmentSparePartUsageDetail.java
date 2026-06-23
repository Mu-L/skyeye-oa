/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 维修工单备件使用明细（parentId = 维修单id）
 */
@Data
@TableName(value = "erp_equipment_spare_part_usage_detail")
@ApiModel("维修工单备件使用明细实体类")
public class EquipmentSparePartUsageDetail extends SkyeyeLinkData {

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品ID", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "oper_number")
    @ApiModelProperty(value = "使用数量", required = "required,num")
    private Integer operNumber;

    @TableField(value = "usage_reason")
    @ApiModelProperty(value = "使用原因")
    private String usageReason;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "出库单价")
    private BigDecimal unitPrice;

    @TableField(value = "amount")
    @ApiModelProperty(value = "总金额(元)")
    private BigDecimal allPrice;

}
