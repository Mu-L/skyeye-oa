package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@TableName(value = "check_work_scheduling_shifts", autoResultMap = true)
@ApiModel("班次表实体类")
public class SchedulingShifts extends OperatorUserInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "shift_name")
    @ApiModelProperty(value = "班次名称", required = "required")
    private String shiftName;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "班次开始时间")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "班次结束时间")
    private String endTime;

    @TableField(value = "min_staff")
    @ApiModelProperty(value = "最小需求人数")
    private String minStaff;

    @TableField(value = "max_staff")
    @ApiModelProperty(value = "最大需求人数")
    private String maxStaff;

    @TableField(value = "description")
    @ApiModelProperty(value = "班次描述")
    private String description;

}
