package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.eve.forum.classenum.ContentStateEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("论坛话题实体类")
@TableName("forum_content")
public class ForumContent extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "forum_content")
    @ApiModelProperty(value = "话题内容", required = "required")
    private String forumContent;

    @TableField(value = "type")
    @ApiModelProperty(value = "话题类型  1.所有人可看  2.只能自己查看", required = "required")
    private Integer type;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态  1.正常  2.删除", required = "required")
    private Integer state;

    @TableField(value = "report_state")
    @ApiModelProperty(value = "举报状态  1.正常  2.已举报", required = "required", enumClass = ContentStateEnum.class)
    private Integer reportState;

    @TableField(value = "tag_id")
    @ApiModelProperty(value = "所属标签，最多三个标签", required = "required")
    private String tagId;

    @TableField(exist = false)
    @ApiModelProperty(value = "标签列表")
    private List<Map<String, Object>> tagList;

    @TableField(value = "forum_title")
    @ApiModelProperty(value = "标题", required = "required")
    private String forumTitle;

    @TableField(value = "forum_desc")
    @ApiModelProperty(value = "简介", required = "required")
    private String forumDesc;

    @TableField(value = "anonymous")
    @ApiModelProperty(value = "是否匿名发帖  1.不是匿名发帖  2.匿名发帖", required = "required", enumClass = WhetherEnum.class)
    private Integer anonymous;
}
