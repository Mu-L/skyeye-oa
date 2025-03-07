/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.entity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.enums.SysEveModelAttrType;
import lombok.Data;

/**
 * @ClassName: SysEveModel
 * @Description: 素材实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/7 14:23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_model")
@RedisCacheField(name = "sys:eve:model", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@ApiModel("素材分类实体类")
public class SysEveModel extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required", fuzzyLike = true)
    private String title;

    @TableField(value = "logo")
    @ApiModelProperty(value = "Logo")
    private String logo;

    @TableField("content")
    @ApiModelProperty(value = "内容", required = "required")
    private String content;

    @TableField(value = "type")
    @ApiModelProperty(value = "模板类型", enumClass = SysEveModelAttrType.class, required = "required,num")
    private Integer type;

    @TableField(exist = false)
    @Property(value = "模板类型名称")
    private String typeName;

    @TableField(value = "first_type_id")
    @ApiModelProperty(value = "所属一级分类", required = "required")
    private String firstTypeId;

    @TableField(exist = false)
    @Property(value = "所属一级分类名称")
    private String firstTypeName;

    @TableField(value = "second_type_id")
    @ApiModelProperty(value = "所属二级分类", required = "required")
    private String secondTypeId;

    @TableField(exist = false)
    @Property(value = "所属二级分类名称")
    private String secondTypeName;

}
