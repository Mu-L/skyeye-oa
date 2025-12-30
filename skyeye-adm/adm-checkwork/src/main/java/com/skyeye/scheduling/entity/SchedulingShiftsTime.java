package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "check_work_scheduling_shifts_time", autoResultMap = true)
@ApiModel("班次时间段表实体类")
public class SchedulingShiftsTime extends BaseGeneralInfo {

    @TableField(value = "shift_id")
    @ApiModelProperty(value = "班次id")
    private String shiftId;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "班次开始时间(格式 HH:mm:ss)")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "班次结束时间(格式 HH:mm:ss)")
    private String endTime;

    @TableField(value = "is_next_day")
    @ApiModelProperty(value = "是否跨天，0表示不跨天，1表示跨天")
    private Integer isNextDay;

    @TableField(value = "color")
    @ApiModelProperty(value = "时间段颜色")
    private String color;

    @TableField(value = "min_staff")
    @ApiModelProperty(value = "最小需求人数")
    private Integer minStaff;

    @TableField(value = "max_staff")
    @ApiModelProperty(value = "最大需求人数")
    private Integer maxStaff;

    @TableField(exist = false)
    @ApiModelProperty(value = "时间段表下工位信息")
    private List<SchedulingShiftsTimeWork> shiftsTimeWorkMation;

}
