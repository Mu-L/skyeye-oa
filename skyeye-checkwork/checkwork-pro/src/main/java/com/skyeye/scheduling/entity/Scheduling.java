package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@TableName(value = "check_work_scheduling", autoResultMap = true)
@ApiModel("排班表实体类")
public class Scheduling extends OperatorUserInfo {

    @TableId("id")
    @Property(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "employee_id")
    @ApiModelProperty(value = "员工id", required = "required")
    private String employeeId;

    @TableField(value = "shift_id")
    @ApiModelProperty(value = "班次Id", required = "required")
    private String shiftId;

    @TableField(value = "schedule_date")
    @ApiModelProperty(value = "排班时间", required = "required")
    private String scheduleDate;

    @TableField(value = "schedule_type")
    @ApiModelProperty(value = "排班状态 1 自动 2 手动")
    private Integer scheduleType;

    @TableField(value = "schedule_people_type")
    @ApiModelProperty(value = "排班人的状态（1 在职中 2 请假中 3 出差中）",defaultValue = "1")
    private Integer schedulePeopleType;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @TableField(exist = false)
    @Property(value = "员工信息")
    private List<Map<String, Object>> staffMation;
}
