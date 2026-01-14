/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ProjectInstallerCommission
 * @Description: 安装员提成实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24 12:00
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "project_installer_commission")
@ApiModel("安装员提成实体类")
public class ProjectInstallerCommission extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "project_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "项目信息")
    private Map<String, Object> projectMation;

    @TableField(value = "dispatch_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "工单ID", required = "required")
    private String dispatchId;

    @TableField(exist = false)
    @Property(value = "工单信息")
    private Map<String, Object> dispatchMation;

    @TableField(value = "installer_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "安装员ID，用户id", required = "required")
    private String installerId;

    @TableField(exist = false)
    @Property(value = "安装员信息")
    private Map<String, Object> installerMation;

    @TableField(value = "installer_commission")
    @ApiModelProperty(value = "个人提成", required = "double", defaultValue = "0")
    private String installerCommission;

    @TableField(value = "work_hours")
    @ApiModelProperty(value = "个人总工时", required = "double", defaultValue = "0")
    private String workHours;

    @TableField(value = "total_work_hours")
    @ApiModelProperty(value = "工单总工时", required = "double", defaultValue = "0")
    private String totalWorkHours;

    @TableField(value = "commission_rate")
    @ApiModelProperty(value = "提成比例", required = "double", defaultValue = "0")
    private String commissionRate;

}

