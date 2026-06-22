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
 * 维修工单备件领用单
 */
@Data
@RedisCacheField(name = "erp:repair:sparepart:requisition", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_equipment_spare_part_requisition")
@ApiModel("维修工单备件领用单实体类")
public class EquipmentSparePartRequisition extends SkyeyeLinkData {

    @TableField(value = "source_order_id")
    @ApiModelProperty(value = "来源单据ID", required = "required")
    private String sourceOrderId;

    @TableField(exist = false)
    @Property(value = "来源单据信息")
    private Map<String, Object> sourceOrderMation;

    @TableField(value = "odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "depot_id")
    @ApiModelProperty(value = "出库仓库ID，关联 erp 仓库 depot")
    private String depotId;

    @TableField(exist = false)
    @Property(value = "仓库信息")
    private Map<String, Object> depotMation;

    @TableField(value = "requisition_purpose")
    @ApiModelProperty(value = "领用目的：1-设备维修", enumClass = EquipmentSparePartRequisitionPurpose.class, required = "num")
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
