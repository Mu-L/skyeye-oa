/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.search.CommonPageInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 按设备名称分页查询维修单入参（须在 {@code @ApiImplicitParams(classBean)} 中注册，否则框架会过滤掉 name）
 */
@Data
@ApiModel("按设备名称分页查询维修单入参")
public class EquipmentRepairOrderByNamePageInfo extends CommonPageInfo implements Serializable {

    @ApiModelProperty(value = "设备名称", required = "required")
    private String name;

}
