/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.code.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: CodeVersion
 * @Description: 代码版本实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/17 21:12
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@UniqueField
@RedisCacheField(name = "code:version")
@TableName(value = "code_version")
@ApiModel("源代码版本")
public class CodeVersion extends BaseGeneralInfo {

    @TableField("release_state")
    @ApiModelProperty(value = "是否发布", enumClass = WhetherEnum.class, required = "required,num")
    private Integer releaseState;

    @TableField("release_time")
    @ApiModelProperty(value = "发布时间，年月日时分秒，例如：2025-08-17 10:10:10，发布状态必填")
    private String releaseTime;

    @TableField("release_year")
    @Property(value = "发布年份")
    private String releaseYear;

}
