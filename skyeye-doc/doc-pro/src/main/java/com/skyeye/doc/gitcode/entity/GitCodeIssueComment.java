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
 * @ClassName: GitCodeIssueComment
 * @Description: GitCode Issue评论实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "code:gitCodeIssueComment")
@TableName(value = "gitcode_issue_comment", autoResultMap = true)
@ApiModel("GitCode Issue评论实体类")
public class GitCodeIssueComment extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "comment_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "GitCode评论ID")
    private String commentId;

    @TableField(value = "issue_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "Issue ID", required = "required")
    private String issueId;

    @TableField(value = "project_url", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "项目地址")
    private String projectUrl;

    @TableField(value = "`body`")
    @ApiModelProperty(value = "评论内容", required = "required")
    private String body;

    @TableField(value = "`system`")
    @ApiModelProperty(value = "是否为系统评论", enumClass = WhetherEnum.class, defaultValue = "0")
    private Integer system;

    @TableField(value = "internal")
    @ApiModelProperty(value = "是否为内部评论", enumClass = WhetherEnum.class, defaultValue = "0")
    private Integer internal;
}
