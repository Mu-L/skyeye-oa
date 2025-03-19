/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.circle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: Circle
 * @Description: 圈子实体层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "wall_circle")
@ApiModel(value = "圈子实体层")
public class Circle extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id")
    private String id;

    @TableField("title")
    @ApiModelProperty(value = "标题", required = "required",fuzzyLike = true)
    private String title;

    @TableField("`describe`")
    @ApiModelProperty(value = "描述")
    private String describe;

    @TableField("img")
    @ApiModelProperty(value = "图片", required = "required")
    private String img;

    @TableField("view_num")
    @ApiModelProperty(value = "浏览数量")
    private Integer viewNum;

    @TableField("num")
    @ApiModelProperty(value = "圈子人数")
    private Integer num;

    @TableField(exist = false)
    @Property(value="当前用户是否加入圈子")
    private Boolean isJoin;
}
