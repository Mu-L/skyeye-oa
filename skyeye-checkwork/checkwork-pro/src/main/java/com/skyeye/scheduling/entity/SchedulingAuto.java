package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("计算排班入参实体类")
public class SchedulingAuto {

    @TableField(exist = false)
    @ApiModelProperty(value = "车间Id" ,required = "required")
    private String farmId;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工Id列表" ,required = "required")
    private String employeeIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "班次Id" ,required = "required")
    private String schedulingShiftsId;

    @TableField(exist = false)
    @ApiModelProperty(value = "班次时间段Id列表" ,required = "required")
    private String schedulingShiftsTimeIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "排班开始时间 格式:yyyy-MM-dd HH:mm" ,required = "required")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "排班结束时间 格式:yyyy-MM-dd HH:mm" ,required = "required")
    private String endTime;
}
