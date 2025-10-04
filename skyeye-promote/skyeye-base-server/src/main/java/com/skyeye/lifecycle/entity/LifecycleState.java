/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.IsUsedEnum;
import lombok.Data;

/**
 * @ClassName: LifecycleState
 * @Description: 生命周期状态实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = CacheConstants.LIFECYCLE_STATE_CACHE_KEY)
@TableName(value = "lifecycle_state")
@ApiModel("生命周期状态实体类")
public class LifecycleState extends BaseGeneralInfo {

    @TableField("num_code")
    @ApiModelProperty(value = "编码", required = "required")
    private String numCode;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "color")
    @ApiModelProperty(value = "颜色")
    private String color;

    @TableField("is_used")
    @Property(value = "是否使用", enumClass = IsUsedEnum.class)
    private Integer isUsed;

    @TableField(value = "app_id")
    @ApiModelProperty(value = "应用的appId", required = "required")
    private String appId;

    @TableField("class_name")
    @ApiModelProperty(value = "服务类的className", required = "required")
    private String className;

}
