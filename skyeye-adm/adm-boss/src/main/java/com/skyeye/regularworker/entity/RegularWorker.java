/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.regularworker.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: RegularWorker
 * @Description: 转正申请实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-24 15:16:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "boss:regularWorker", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "boss_interview_regular_worker", autoResultMap = true)
@ApiModel("转正申请实体类")
public class RegularWorker extends SkyeyeFlowable {

    @TableField(value = "department_id")
    @Property(value = "申请人部门id")
    private String departmentId;

    @TableField(exist = false)
    @Property(value = "部门信息")
    private Map<String, Object> departmentMation;

    @TableField(value = "job_id")
    @Property(value = "申请人岗位id")
    private String jobId;

    @TableField(exist = false)
    @Property(value = "岗位信息")
    private Map<String, Object> jobMation;

    @TableField(value = "regular_time")
    @ApiModelProperty(value = "转正日期", required = "required")
    private String regularTime;

    @TableField(value = "remark")
    @ApiModelProperty(value = "申请说明", required = "required")
    private String remark;

}
