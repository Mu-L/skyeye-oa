package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.scheduling.classenum.ScheduleLeaveType;
import lombok.Data;

@Data
@TableName(value = "check_work_scheduling_leave", autoResultMap = true)
@ApiModel("临时员工请假表实体类")
public class SchedulingLeave extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "employee_id")
    @ApiModelProperty(value = "员工id", required = "required")
    private String employeeId;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间id", required = "required")
    private String farmId;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "请假开始时间(yyyy-MM-dd HH:mm:ss)", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "请假结束时间（yyyy-MM-dd HH:mm:ss）", required = "required")
    private String endTime;

    @TableField(value = "reason")
    @ApiModelProperty(value = "请假原因")
    private String reason;

    @TableField(value = "status")
    @ApiModelProperty(value = "请假状态，如'1 已申请'、'2 已批准'、'3 已拒绝'", enumClass = ScheduleLeaveType.class)
    private Integer status;

}
