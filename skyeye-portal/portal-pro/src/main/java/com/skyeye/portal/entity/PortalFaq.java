/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import lombok.Data;

/**
 * 官网常见问题（租户平台隔离）
 */
@Data
@TableName(value = "portal_faq", autoResultMap = true)
@ApiModel("官网常见问题")
public class PortalFaq extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("question")
    @ApiModelProperty(value = "问题标题", required = "required", fuzzyLike = true)
    private String question;

    @TableField("answer")
    @ApiModelProperty(value = "回答内容，支持HTML", required = "required")
    private String answer;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required")
    private Integer enabled;

    @TableField("order_by")
    @ApiModelProperty(value = "排序，越大越靠前", required = "required,num")
    private Integer orderBy;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}
