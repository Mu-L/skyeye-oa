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
import com.skyeye.common.entity.features.AreaGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.patrol.classenum.PatrolPointType;
import lombok.Data;

/**
 * @ClassName: PatrolPoint
 * @Description: 巡检点位实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "seal:patrol:point", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "crm_service_patrol_point")
@ApiModel("巡检点位实体类")
public class PatrolPoint extends AreaGeneralInfo {

    @TableField(value = "point_code")
    @ApiModelProperty(value = "点位编码", required = "required")
    private String pointCode;

    @TableField(value = "point_type")
    @ApiModelProperty(value = "点位类型", enumClass = PatrolPointType.class, required = "required,num")
    private Integer pointType;

    @TableField(value = "longitude")
    @ApiModelProperty(value = "经度")
    private String longitude;

    @TableField(value = "latitude")
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @TableField("absolute_address")
    @ApiModelProperty(value = "具体地址")
    private String absoluteAddress;

    @TableField(value = "qr_code")
    @ApiModelProperty(value = "二维码")
    private String qrCode;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}

