/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.repair.classenum.EquipmentRepairCancelReason;
import com.skyeye.repair.classenum.EquipmentRepairFaultReason;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备维修-维修结果入参（待完工）
 */
@Data
@ApiModel("设备维修-维修结果")
public class EquipmentRepairResult implements Serializable {

    @ApiModelProperty(value = "主键id", required = "required")
    private String id;

    @ApiModelProperty(value = "是否已进行维修", enumClass = WhetherEnum.class, required = "num")
    private Integer isRepaired;

    @ApiModelProperty(value = "是否已更换配件", enumClass = WhetherEnum.class, required = "num")
    private Integer isReplaceSpare;

    @ApiModelProperty(value = "故障原因", enumClass = EquipmentRepairFaultReason.class)
    private Integer faultReason;

    @ApiModelProperty(value = "作废原因", enumClass = EquipmentRepairCancelReason.class)
    private Integer cancelReason;

    @ApiModelProperty(value = "维修情况说明")
    private String repairDesc;

    @ApiModelProperty(value = "维修完成拍照")
    private String repairFinishPhoto;

    @ApiModelProperty(value = "维修完成时间")
    private String repairFinishTime;

    @ApiModelProperty(value = "供应商ID（转委外且已维修时必填）")
    private String supplierId;
}
