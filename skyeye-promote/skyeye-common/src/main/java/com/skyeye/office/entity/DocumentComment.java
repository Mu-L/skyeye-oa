package com.skyeye.office.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName: DocumentComment
 * @Description: 文档评论实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Data
@TableName(value = "sys_document_comment")
@EqualsAndHashCode(callSuper = false)
@ApiModel("文档评论实体类")
public class DocumentComment extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @ApiModelProperty(value = "文档ID", required = "required")
    private String documentId;

    @ApiModelProperty(value = "评论内容", required = "required")
    private String content;

    @ApiModelProperty(value = "父评论ID")
    private String parentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @TableField(exist = false)
    @ApiModelProperty(value = "回复数量")
    private Integer replyCount;
} 