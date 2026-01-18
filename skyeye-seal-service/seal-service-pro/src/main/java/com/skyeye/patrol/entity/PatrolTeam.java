/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * @ClassName: PatrolTeam
 * @Description: 巡检班组实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "seal:patrol:team", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_patrol_team")
@ApiModel("巡检班组实体类")
public class PatrolTeam extends BaseGeneralInfo {

    @TableField(value = "team_code")
    @ApiModelProperty(value = "班组编码", required = "required")
    private String teamCode;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "班组排序", required = "required,num")
    private Integer orderBy;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}

