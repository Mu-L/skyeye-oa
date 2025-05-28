package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "check_work_scheduling_shifts", autoResultMap = true)
@ApiModel("班次表实体类")
public class SchedulingShifts extends BaseGeneralInfo {

    @TableField(exist = false)
    @ApiModelProperty(value = "班次时间段", required = "json")
    private List<SchedulingShiftsTime> schedulingShiftsTimeMation;

}
