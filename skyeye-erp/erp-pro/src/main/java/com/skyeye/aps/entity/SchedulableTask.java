/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.aps.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: SchedulableTask
 * @Description: APS可排程任务
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("APS可排程任务")
public class SchedulableTask {

    @ApiModelProperty(value = "加工单ID")
    private String machinId;

    @ApiModelProperty(value = "加工单工序ID")
    private String machinProcedureId;

    @ApiModelProperty(value = "车间ID")
    private String farmId;

    @ApiModelProperty(value = "加工单子单据ID")
    private String childId;

    @ApiModelProperty(value = "工艺路线工序ID，同一加工单下同工序名对应同一procedureId")
    private String procedureId;

    @ApiModelProperty(value = "工序名称")
    private String procedureName;

    @ApiModelProperty(value = "商品名称")
    private String materialName;

    @ApiModelProperty(value = "规格名称")
    private String normsName;

    @ApiModelProperty(value = "加工数量")
    private String targetNum;

    @ApiModelProperty(value = "工序时长(分钟)，支持小数")
    private String durationMinutes;

    @ApiModelProperty(value = "交货期")
    private String deliveryTime;

    @ApiModelProperty(value = "工序排序")
    private int orderBy;

    @ApiModelProperty(value = "前置工序ID")
    private String prevProcedureId;

    /** 历史已排单的计划开始时间，不为空表示优先保留不重新分配 */
    @ApiModelProperty(value = "历史已排单的计划开始时间")
    private String existingPlanStartTime;

    /** 历史已排单的计划结束时间 */
    @ApiModelProperty(value = "历史已排单的计划结束时间")
    private String existingPlanEndTime;
}
