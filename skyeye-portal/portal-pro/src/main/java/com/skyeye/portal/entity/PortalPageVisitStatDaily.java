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
 * 官网页面访问日 PV 汇总（租户平台隔离）
 * <p>表：portal_page_visit_stat_daily，唯一键 tenant_id + stat_date + page_path</p>
 */
@Data
@TableName(value = "portal_page_visit_stat_daily", autoResultMap = true)
@ApiModel("官网页面访问日PV汇总")
public class PortalPageVisitStatDaily extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("stat_date")
    @ApiModelProperty(value = "统计日期")
    private String statDate;

    @TableField("page_path")
    @ApiModelProperty(value = "页面路径")
    private String pagePath;

    @TableField("page_name")
    @ApiModelProperty(value = "页面名称")
    private String pageName;

    @TableField("pv_count")
    @ApiModelProperty(value = "浏览量")
    private Long pvCount;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
