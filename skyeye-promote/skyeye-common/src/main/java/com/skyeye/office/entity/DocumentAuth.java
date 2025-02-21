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
 * @ClassName: DocumentAuth
 * @Description: 文档权限实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_document_auth")
@EqualsAndHashCode(callSuper = false)
@ApiModel("文档权限实体类")
public class DocumentAuth extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "document_id")
    @ApiModelProperty(value = "文档ID", required = "required")
    private String documentId;

    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户ID", required = "required")
    private String userId;

    @TableField(value = "auth_type")
    @ApiModelProperty(value = "权限类型(owner/edit/view)", required = "required")
    private String authType;
} 