/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

/**
 * @ClassName: GitCodeIssue
 * @Description: GitCode Issue实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "code:gitCodeIssue")
@TableName(value = "gitcode_issue", autoResultMap = true)
@ApiModel("GitCode Issue实体类")
public class GitCodeIssue extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "issue_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "GitCode Issue ID")
    private String issueId;

    @TableField(value = "project_url", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "项目地址")
    private String projectUrl;

    @TableField(value = "title")
    @ApiModelProperty(value = "Issue标题", required = "required", fuzzyLike = true)
    private String title;

    @TableField(value = "description")
    @ApiModelProperty(value = "Issue描述")
    private String description;

    @TableField(value = "state")
    @ApiModelProperty(value = "Issue状态", required = "required", defaultValue = "opened")
    private String state;

    @TableField(value = "assignee_id")
    @ApiModelProperty(value = "负责人ID")
    private String assigneeId;

    @TableField(value = "milestone_id")
    @ApiModelProperty(value = "里程碑ID")
    private String milestoneId;

    @TableField(value = "labels")
    @ApiModelProperty(value = "标签，多个用逗号分隔")
    private String labels;

    @TableField(value = "version_id")
    @ApiModelProperty(value = "版本id")
    private String versionId;

    @TableField(value = "due_date")
    @ApiModelProperty(value = "截止日期")
    private String dueDate;

    @TableField(value = "confidential")
    @ApiModelProperty(value = "是否保密", enumClass = WhetherEnum.class, defaultValue = "0")
    private Integer confidential;

    @TableField(value = "discussion_locked")
    @ApiModelProperty(value = "是否锁定讨论", enumClass = WhetherEnum.class, defaultValue = "0")
    private Integer discussionLocked;
}
