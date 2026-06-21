/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.equipment.entity.Equipment;
import com.skyeye.maintenance.classenum.MaintainResultEnum;
import com.skyeye.sparepart.entity.EquipmentSparePartRequisition;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备保养单
 * TODO: 保养单明细（EquipmentMaintainOrderItem）待实现：独立实体与表，保存/查询及计划下发时复制明细
 */
@Data
@RedisCacheField(name = "erp:equipment:maintainOrder", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "erp_equipment_maintain_order")
@ApiModel("设备保养单")
public class EquipmentMaintainOrder extends SkyeyeFlowable {

    @TableField(value = "equipment_id")
    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @TableField(exist = false)
    @Property(value = "设备信息")
    private Equipment equipmentMation;

    @TableField(value = "maintain_current_date")
    @ApiModelProperty(value = "本次保养日期")
    private String maintainCurrentDate;

    @TableField(value = "maintenance_plan_id")
    @ApiModelProperty(value = "保养计划id", required = "required")
    private String maintenancePlanId;

    @TableField(exist = false)
    @Property(value = "保养计划信息")
    private Map<String, Object> maintenancePlanMation;

    @TableField(value = "charge_id")
    @ApiModelProperty(value = "保养负责人id")
    private String chargeId;

    @TableField(exist = false)
    @Property(value = "保养负责人")
    private Map<String, Object> chargeMation;

    @TableField(value = "maintenance_standard_id")
    @ApiModelProperty(value = "保养标准id")
    private String maintenanceStandardId;

    @TableField(exist = false)
    @Property(value = "保养标准")
    private MaintenanceStandard maintenanceStandardMation;

    @TableField(value = "maintain_photos")
    @ApiModelProperty(value = "保养拍照")
    private String maintainPhotos;

    @TableField(value = "maintain_result")
    @ApiModelProperty(value = "保养结果", enumClass = MaintainResultEnum.class)
    private Integer maintainResult;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Property(value = "备件信息")
    @ApiModelProperty(value = "备件领用单列表", required = "json")
    private List<EquipmentSparePartRequisition> sparePartRequisitionList;
}
