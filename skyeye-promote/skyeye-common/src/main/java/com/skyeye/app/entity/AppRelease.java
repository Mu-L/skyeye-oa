/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.app.enums.AppReleaseStatusEnum;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: AppRelease
 * @Description: APP发布记录实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "skyeye_app_release", autoResultMap = true)
@ApiModel("APP发布记录实体类")
public class AppRelease extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("version_id")
    @ApiModelProperty(value = "版本ID", required = "required")
    private String versionId;

    @TableField("project_id")
    @ApiModelProperty(value = "项目ID", required = "required")
    private String projectId;

    @TableField("platform")
    @ApiModelProperty(value = "平台类型", required = "required")
    private String platform;

    @TableField("store_id")
    @ApiModelProperty(value = "应用商店id", required = "required")
    private String storeId;

    @TableField("status")
    @ApiModelProperty(value = "发布状态", enumClass = AppReleaseStatusEnum.class, required = "required")
    private String status;

    @TableField("scheduled_time")
    @ApiModelProperty(value = "计划发布时间")
    private String scheduledTime;

    @TableField("submit_time")
    @ApiModelProperty(value = "提交时间")
    private String submitTime;

    @TableField("approve_time")
    @ApiModelProperty(value = "审核通过时间")
    private String approveTime;

    @TableField("publish_time")
    @ApiModelProperty(value = "发布时间")
    private String publishTime;

    @TableField("reject_reason")
    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    @TableField("store_response")
    @ApiModelProperty(value = "发布到应用商店得响应信息")
    private String storeResponse;

    @TableField("download_count")
    @ApiModelProperty(value = "下载次数")
    private String downloadCount;
}
