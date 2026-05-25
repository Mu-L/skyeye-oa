/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * 官网页面访问明细（租户平台隔离）
 * <p>表：portal_page_visit_log，用于明细分页与排查；报表统计读日汇总表</p>
 */
@Data
@TableName(value = "portal_page_visit_log", autoResultMap = true)
@ApiModel("官网页面访问日志")
public class PortalPageVisitLog extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("page_path")
    @ApiModelProperty(value = "页面路径", required = "required")
    private String pagePath;

    @TableField("page_name")
    @ApiModelProperty(value = "页面名称", fuzzyLike = true)
    private String pageName;

    @TableField("visit_time")
    @ApiModelProperty(value = "访问时间")
    private String visitTime;

    @TableField("visitor_id")
    @ApiModelProperty(value = "访客标识")
    private String visitorId;

    @TableField("client_ip")
    @ApiModelProperty(value = "客户端IP")
    private String clientIp;

    @TableField("user_agent")
    @ApiModelProperty(value = "浏览器UA")
    private String userAgent;

    @TableField("referrer")
    @ApiModelProperty(value = "来源页")
    private String referrer;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
