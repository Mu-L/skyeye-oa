/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备维修-待评价入参（评价类型与工单一致，走数据字典）
 */
@Data
@ApiModel("设备维修-待评价")
public class EquipmentRepairEvaluate implements Serializable {

    @ApiModelProperty(value = "主键id", required = "required")
    private String id;

    @ApiModelProperty(value = "评价类型，参考数据字典（与工单评价 typeId 一致）", required = "required")
    private String evaluateTypeId;

    @ApiModelProperty(value = "评价内容", required = "required")
    private String evaluateContent;
}
