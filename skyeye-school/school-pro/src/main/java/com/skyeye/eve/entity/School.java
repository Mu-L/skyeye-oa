/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.AreaGeneralInfo;
import lombok.Data;

/**
 * @ClassName: SchoolEntity
 * @Description: 学校实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/6 21:13
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "school:mation")
@TableName(value = "school_mation")
@ApiModel(value = "学校实体类")
public class School extends AreaGeneralInfo {

    @TableField(value = "longitude")
    @ApiModelProperty(value = "经度")
    private String longitude;

    @TableField(value = "latitude")
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @TableField(value = "power")
    @ApiModelProperty(value = "数据权限  1.查看所有  2.查看本校", required = "required,num")
    private String power;

    @TableField(value = "ne_longitude")
    @ApiModelProperty(value = "东北经度")
    private String neLongitude;

    @TableField(value = "ne_latitude")
    @ApiModelProperty(value = "东北纬度")
    private String neLatitude;

    @TableField(value = "sw_longitude")
    @ApiModelProperty(value = "西南经度")
    private String swLongitude;

    @TableField(value = "sw_latitude")
    @ApiModelProperty(value = "西南纬度")
    private String swLatitude;

    @TableField(value = "background")
    @ApiModelProperty(value = "学校背景图")
    private String background;

}
