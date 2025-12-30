/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.appeal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.checkwork.entity.CheckWork;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

/**
 * @ClassName: Appeal
 * @Description: 考勤申诉实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/7/18 11:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "checkwork:appeal", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "check_work_appeal")
@ApiModel("考勤申诉实体类")
public class Appeal extends SkyeyeFlowable {

    @TableField("work_id")
    @ApiModelProperty(value = "考勤工作日id", required = "required")
    private String workId;

    @TableField(exist = false)
    @Property(value = "考勤工作日信息")
    private CheckWork workMation;

    @TableField("appeal_reason_id")
    @ApiModelProperty(value = "申诉类型id，数据字典", required = "required")
    private String appealReasonId;

    @TableField("appeal_reason")
    @ApiModelProperty(value = "申诉原因", required = "required")
    private String appealReason;

}
