package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@ApiModel(value = "论坛标签实体类")
@TableName("forum_tag")
public class ForumTag extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "tag_name")
    @ApiModelProperty(value = "标签名称", required = "required")
    private String tagName;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序，值越大越往后", defaultValue = "1")
    private Integer orderBy;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态  1.新建  2.上线  3.下线  4.删除", defaultValue = "1")
    private Integer state;
}
