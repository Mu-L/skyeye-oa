package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "check_work_scheduling_shifts_time_work", autoResultMap = true)
@ApiModel("班次时间段下工位信息表实体类")
public class SchedulingShiftsTimeWork extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "shifts_time_id")
    @ApiModelProperty(value = "班次时间id")
    private String shiftsTimeId;

    @TableField(value = "min_staff")
    @ApiModelProperty(value = "最小需求人数")
    private Integer minStaff;

    @TableField(value = "max_staff")
    @ApiModelProperty(value = "最大需求人数")
    private Integer maxStaff;

    @TableField(value = "work_id")
    @ApiModelProperty(value = "工位Id")
    private String workId;

    @TableField(exist = false)
    @ApiModelProperty(value = "工位信息")
    private Map<String, Object> workMation;

}
