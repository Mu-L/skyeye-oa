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
 * 官网下载中心版本（租户平台隔离）
 */
@Data
@TableName(value = "portal_download_version", autoResultMap = true)
@ApiModel("官网下载中心版本")
public class PortalDownloadVersion extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("version")
    @ApiModelProperty(value = "版本号", required = "required", fuzzyLike = true)
    private String version;

    @TableField("release_date")
    @ApiModelProperty(value = "发布日期")
    private String releaseDate;

    @TableField("tag")
    @ApiModelProperty(value = "版本标签")
    private String tag;

    @TableField("article_url")
    @ApiModelProperty(value = "更新介绍链接")
    private String articleUrl;

    @TableField("video_url")
    @ApiModelProperty(value = "更新视频链接")
    private String videoUrl;

    @TableField("download_url")
    @ApiModelProperty(value = "下载链接")
    private String downloadUrl;

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
