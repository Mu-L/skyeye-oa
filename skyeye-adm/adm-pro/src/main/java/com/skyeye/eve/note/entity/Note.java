/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.note.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.eve.note.classenum.NoteType;
import lombok.Data;

/**
 * @ClassName: Note
 * @Description: 笔记实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/26 22:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Data
@ApiModel("笔记实体类")
@TableName(value = "note_content")
public class Note extends BaseGeneralInfo {

    @TableField(value = "icon_logo", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "icon的图标")
    private String iconLogo;

    @TableField(value = "content")
    @ApiModelProperty(value = "笔记内容")
    private String content;

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "父文件夹id")
    private String parentId;

    @TableField(value = "type", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "笔记类型", enumClass = NoteType.class)
    private Integer type;

    @TableField(value = "delete_flag")
    private Integer deleteFlag;

}
