/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.document.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.doc.document.enums.DocumentType;
import lombok.Data;

/**
 * @ClassName: Document
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/24 11:13
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@RedisCacheField(name = "code:docDocument")
@TableName(value = "document", autoResultMap = true)
@ApiModel("文档实体类")
public class Document extends BaseGeneralInfo {

    @TableField(value = "content")
    @ApiModelProperty(value = "文档内容")
    private String content;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "启用状态", enumClass = EnableEnum.class, required = "required,num", defaultValue = "1")
    private Integer enabled;

    @TableField(value = "type")
    @ApiModelProperty(value = "类型", enumClass = DocumentType.class, required = "required,num", defaultValue = "1")
    private Integer type;

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "父节点id", required = "required")
    private String parentId;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，值越大越往后", required = "required")
    private Integer orderBy;

}
