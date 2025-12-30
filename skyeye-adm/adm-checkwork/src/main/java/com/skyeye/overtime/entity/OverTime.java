/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: OverTime
 * @Description: 加班申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 14:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "checkwork:overTime", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "check_work_overtime", autoResultMap = true)
@ApiModel("加班申请实体类")
public class OverTime extends SkyeyeFlowable {

    @TableField(value = "`name`")
    @ApiModelProperty(value = "标题", required = "required")
    private String name;

    @TableField(value = "content")
    @ApiModelProperty(value = "加班事由", required = "required")
    private String content;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "加班时间段", required = "required,json")
    private List<OverTimeSlot> overTimeSlotList;

}
