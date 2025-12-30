/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.worktime.classenum.CheckWorkTimeWeekType;
import lombok.Data;

/**
 * @ClassName: CheckWorkTimeWeek
 * @Description: 考勤班次里的具体时间实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 14:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "check_work_time_week")
@ApiModel("考勤班次里的具体时间实体类")
public class CheckWorkTimeWeek extends CommonInfo {

    @TableField(value = "time_id")
    @Property(value = "班次id")
    private String timeId;

    @TableField(value = "week_number")
    @ApiModelProperty(value = "周几", required = "required")
    private Integer weekNumber;

    @TableField(value = "type")
    @ApiModelProperty(value = "上班类型", enumClass = CheckWorkTimeWeekType.class, required = "required,num")
    private Integer type;

}
