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
 * 维修工单备件领用明细
 */
@Data
@TableName(value = "erp_equipment_spare_part_requisition_detail")
@ApiModel("维修工单备件领用明细实体类")
public class EquipmentSparePartRequisitionDetail extends SkyeyeLinkData {

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品ID，erp_material.id", required = "required")
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

    @TableField(value = "apply_qty")
    @ApiModelProperty(value = "领用数量", required = "required,num")
    private Integer operNumber;

    @TableField(value = "requisition_reason")
    @ApiModelProperty(value = "领用原因")
    private String requisitionReason;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "出库单价(元)，来源于备件/规格档案，保存时回填")
    private BigDecimal unitPrice;

    @TableField(value = "amount")
    @ApiModelProperty(value = "总金额(元)，单价×数量")
    private BigDecimal allPrice;

}
