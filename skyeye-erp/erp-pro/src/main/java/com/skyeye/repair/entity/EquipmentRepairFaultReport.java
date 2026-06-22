/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.repair.classenum.EquipmentRepairUrgency;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备维修-故障报修入参
 */
@Data
@ApiModel("设备维修-故障报修")
public class EquipmentRepairFaultReport implements Serializable {

    @ApiModelProperty(value = "主键id，编辑时必填")
    private String id;

    @ApiModelProperty(value = "设备id", required = "required")
    private String equipmentId;

    @ApiModelProperty(value = "故障描述", required = "required")
    private String faultBrief;

    @ApiModelProperty(value = "故障情况拍照", required = "required")
    private String faultPhoto;

    @ApiModelProperty(value = "故障情况视频", required = "required")
    private String faultVideo;

    @ApiModelProperty(value = "紧急程度", enumClass = EquipmentRepairUrgency.class)
    private Integer urgencyLevel;
}
