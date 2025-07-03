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
@TableName(value = "check_work_scheduling_time_work_people", autoResultMap = true)
@ApiModel("排班工位下员工表实体类")
public class SchedulingTimeWorkPeople extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "scheduling_time_work_id")
    @ApiModelProperty(value = "排班时间工位id")
    private String schedulingTimeWorkId;

    @TableField(value = "employee_id")
    @ApiModelProperty(value = "员工id")
    private String employeeId;

    @TableField(value = "scheduling_id")
    @ApiModelProperty(value = "排班id")
    private String schedulingId;

    @TableField(value = "scheduling_time_id")
    @ApiModelProperty(value = "排班时间段id")
    private String schedulingTimeId;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工信息")
    private Map<String, Object> staffMation;
}
