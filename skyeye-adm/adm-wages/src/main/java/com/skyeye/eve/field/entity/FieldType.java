/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.eve.field.classenum.WagesTypeEnum;
import lombok.Data;

/**
 * @ClassName: FieldType
 * @Description: 薪资字段实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/26 9:18
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("薪资字段实体类")
@UniqueField(value = {"key"})
@TableName(value = "wages_field_type")
@RedisCacheField(name = CacheConstants.WAGES_FIELD_CACHE_KEY)
public class FieldType extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required")
    private String name;

    @TableField(value = "`key`", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "key，可以用于区分字段内容，用于后面公式计算", required = "required")
    private String key;

    @TableField("monthly_clearing")
    @ApiModelProperty(value = "是否每月统计上月薪资时，该字段自动清零", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer monthlyClearing;

    @TableField("wages_type")
    @ApiModelProperty(value = "薪资字段类型", enumClass = WagesTypeEnum.class, required = "required,num")
    private Integer wagesType;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

}
