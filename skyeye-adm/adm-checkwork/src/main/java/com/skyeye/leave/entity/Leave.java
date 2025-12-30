/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.entity;

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
 * @ClassName: Leave
 * @Description: 请假申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "checkwork:leave", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "check_work_leave", autoResultMap = true)
@ApiModel("请假申请实体类")
public class Leave extends SkyeyeFlowable {

    @TableField(value = "`name`")
    @ApiModelProperty(value = "标题", required = "required")
    private String name;

    @TableField("remark")
    @ApiModelProperty(value = "描述")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "请假时间段", required = "required,json")
    private List<LeaveTimeSlot> leaveTimeSlotList;

}
