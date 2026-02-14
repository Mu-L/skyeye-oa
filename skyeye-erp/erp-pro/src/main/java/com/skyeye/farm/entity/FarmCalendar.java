/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.farm.classenum.FarmCalendarConfigTypeEnum;
import lombok.Data;

/**
 * @ClassName: FarmCalendar
 * @Description: 车间产能日历实体类，支持按日期/按星期/按日期区间配置差异化产能
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_farm_calendar")
@ApiModel("车间产能日历")
public class FarmCalendar extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "farm_id")
    @ApiModelProperty(value = "车间ID", required = "required")
    private String farmId;

    @TableField(value = "config_type")
    @ApiModelProperty(value = "配置类型", required = "required", enumClass = FarmCalendarConfigTypeEnum.class)
    private String configType;

    @TableField(value = "work_date")
    @ApiModelProperty(value = "工作日期(yyyy-MM-dd)，config_type=DATE时必填。")
    private String workDate;

    @TableField(value = "work_date_start")
    @ApiModelProperty(value = "区间开始日期(yyyy-MM-dd)，config_type=PERIOD时必填。")
    private String workDateStart;

    @TableField(value = "work_date_end")
    @ApiModelProperty(value = "区间结束日期(yyyy-MM-dd)，config_type=PERIOD时必填。")
    private String workDateEnd;

    @TableField(value = "day_of_week")
    @ApiModelProperty(value = "星期几(可多选，逗号分隔如1,2,3,4,5)，1=周一7=周日，按星期时必填")
    private String dayOfWeek;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "priority")
    @ApiModelProperty(value = "优先级(数值越大越优先)，多条匹配时取最高", required = "required,num")
    private Integer priority;

    @TableField(value = "daily_work_minutes")
    @ApiModelProperty(value = "当日可用工时(分钟)", required = "required,num")
    private Integer dailyWorkMinutes;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Property(value = "车间信息")
    private Farm farmMation;
}
