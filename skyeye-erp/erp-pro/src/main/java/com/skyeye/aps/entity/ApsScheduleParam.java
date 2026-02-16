/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.aps.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ApsScheduleParam
 * @Description: APS排程参数
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("APS排程参数")
public class ApsScheduleParam {

    @ApiModelProperty(value = "加工单ID列表，为空则排程所有待排程加工单")
    private List<String> machinIds;

    @ApiModelProperty(value = "车间ID列表，为空则使用全部启用车间")
    private List<String> farmIds;

    @ApiModelProperty(value = "排程开始日期，格式yyyy-MM-dd", required = "required")
    private String scheduleStartDate;

    @ApiModelProperty(value = "排程结束日期，格式yyyy-MM-dd，为空则自动推算(如排程范围90天)")
    private String scheduleEndDate;

    @ApiModelProperty(value = "是否考虑工序依赖(前置工序完成才能开始)，默认true")
    private Boolean respectProcedureOrder = true;

    @ApiModelProperty(value = "排程策略：DELIVERY-按交货期优先，PRIORITY-按加工单优先级(扩展)")
    private String scheduleStrategy = "DELIVERY";
}
