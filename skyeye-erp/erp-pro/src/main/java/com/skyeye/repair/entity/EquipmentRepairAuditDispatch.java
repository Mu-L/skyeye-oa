/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.repair.classenum.EquipmentFaultCategory;
import com.skyeye.repair.classenum.EquipmentRepairAuditOpinion;
import com.skyeye.repair.classenum.EquipmentRepairTeam;
import com.skyeye.repair.classenum.EquipmentRepairUrgency;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备维修-审核派工入参
 */
@Data
@ApiModel("设备维修-审核派工")
public class EquipmentRepairAuditDispatch implements Serializable {

    @ApiModelProperty(value = "主键id", required = "required")
    private String id;

    @ApiModelProperty(value = "审核意见", enumClass = EquipmentRepairAuditOpinion.class, required = "num")
    private Integer auditOpinion;

    @ApiModelProperty(value = "紧急程度", enumClass = EquipmentRepairUrgency.class, required = "num")
    private Integer urgencyLevel;

    @ApiModelProperty(value = "故障类别", enumClass = EquipmentFaultCategory.class, required = "num")
    private Integer faultType;

    @ApiModelProperty(value = "维修班组", enumClass = EquipmentRepairTeam.class, required = "num")
    private Integer repairTeam;

    @ApiModelProperty(value = "故障响应时长(小时)")
    private Double responseHours;

    @ApiModelProperty(value = "维修负责人用户ID，有值则变为待接单")
    private String serviceUserId;

    @ApiModelProperty(value = "派工时间")
    private String serviceTime;
}
