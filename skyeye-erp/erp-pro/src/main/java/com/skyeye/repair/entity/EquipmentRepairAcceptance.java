/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.equipment.classenum.EquipmentState;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备维修-待审核/结果验收入参
 */
@Data
@ApiModel("设备维修-结果验收")
public class EquipmentRepairAcceptance implements Serializable {

    @ApiModelProperty(value = "主键id", required = "required")
    private String id;

    @ApiModelProperty(value = "是否修复", enumClass = WhetherEnum.class, required = "num")
    private Integer isFixed;

    @ApiModelProperty(value = "设备状态（验收通过时写入设备档案）", enumClass = EquipmentState.class, required = "num")
    private Integer equipmentStatus;
}
