package com.skyeye.office.entity;

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

    @ApiModelProperty(value = "文档ID", required = "required")
    private String documentId;

    @ApiModelProperty(value = "用户ID", required = "required")
    private String userId;

    @ApiModelProperty(value = "权限类型(owner/edit/view)", required = "required")
    private String authType;
} 