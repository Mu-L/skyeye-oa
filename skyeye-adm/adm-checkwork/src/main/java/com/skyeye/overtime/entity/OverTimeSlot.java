/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.common.enumeration.OvertimeSettlementType;
import com.skyeye.overtime.classenum.OvertimeSoltSettleState;
import lombok.Data;

/**
 * @ClassName: OverTimeSlot
 * @Description: 加班申请加班时间段实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "check_work_overtime_time_slot", autoResultMap = true)
@ApiModel("加班申请加班时间段实体类")
public class OverTimeSlot extends SkyeyeLinkData {

    @TableField(value = "overtime_day")
    @ApiModelProperty(value = "加班日期，格式：yyyy-MM-dd", required = "required")
    private String overtimeDay;

    @TableField(value = "overtime_start_time")
    @ApiModelProperty(value = "加班开始时间，格式：HH:mm:ss", required = "required")
    private String overtimeStartTime;

    @TableField(value = "overtime_end_time")
    @ApiModelProperty(value = "加班结束时间，格式：HH:mm:ss", required = "required")
    private String overtimeEndTime;

    @TableField(value = "overtime_hour")
    @ApiModelProperty(value = "加班小时", required = "required")
    private String overtimeHour;

    @TableField(value = "settle_state")
    @Property(value = "加班是否计入补休/薪资结算状态", enumClass = OvertimeSoltSettleState.class)
    private Integer settleState;

    @TableField(value = "overtime_settlement_type")
    @Property(value = "部门加班结算方式", enumClass = OvertimeSettlementType.class)
    private Integer overtimeSettlementType;

}
