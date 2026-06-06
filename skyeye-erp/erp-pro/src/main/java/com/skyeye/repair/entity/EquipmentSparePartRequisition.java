/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.repair.classenum.EquipmentSparePartRequisitionPurpose;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 备件领用单主表（关联 erp 仓库、设备维修单、sys 用户/部门、明细一对多 erp_material）
 */
@Data
@RedisCacheField(name = "seal:repair:spareRequisition", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "equipment_spare_part_requisition")
@ApiModel("备件领用单实体类")
public class EquipmentSparePartRequisition extends SkyeyeLinkData {

    @TableField(value = "repair_order_id")
    @ApiModelProperty(value = "设备维修单ID，关联 equipment_repair_order.id")
    private String repairOrderId;

    @TableField(exist = false)
    @Property(value = "设备维修单信息")
    private EquipmentRepairOrder repairOrderMation;

    @TableField(value = "odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    @ApiModelProperty(value = "单据编号")
    private String oddNumber;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "出库仓库ID，关联 erp 仓库 depot")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Map<String, Object> depotMation;

    @TableField(value = "requisition_purpose")
    @ApiModelProperty(value = "领用目的：1-设备维修、2-设备保养", enumClass = EquipmentSparePartRequisitionPurpose.class, required = "num")
    private Integer requisitionPurpose;

    @TableField(value = "requisition_date")
    @ApiModelProperty(value = "领用日期 yyyy-MM-dd")
    private String requisitionDate;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "领用人用户ID")
    private String userId;

    @TableField(exist = false)
    @Property(value = "领用人信息")
    private Map<String, Object> userMation;

    @TableField(value = "total_amount")
    @ApiModelProperty(value = "出库总金额(元)，由明细出库金额汇总")
    private BigDecimal totalAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "领用明细列表", required = "json")
    private List<EquipmentSparePartRequisitionDetail> detailList;

}
