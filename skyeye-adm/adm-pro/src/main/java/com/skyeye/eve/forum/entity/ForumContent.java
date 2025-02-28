package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.eve.forum.classenum.ContentStateEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel("论坛话题实体类")
@RedisCacheField(name = "forum:content", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName("forum_content")
public class ForumContent extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "forum_content")
    @ApiModelProperty(value = "话题内容", required = "required")
    private String forumContent;

    @TableField(value = "type")
    @ApiModelProperty(value = "话题类型(默认1)  1.所有人可看  2.只能自己查看",defaultValue = "1")
    private Integer type;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态(默认1)  1.正常  2.删除",defaultValue = "1")
    private Integer state;

    @TableField(value = "report_state")
    @ApiModelProperty(value = "举报状态(默认1)  1.正常  2.被举报",defaultValue = "1", enumClass = ContentStateEnum.class)
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
    @ApiModelProperty(value = "是否匿名发帖(默认0)  0.不是匿名发帖  1.匿名发帖",defaultValue = "0", enumClass = WhetherEnum.class)
    private Integer anonymous;

    @TableField(value = "browse_num")
    @ApiModelProperty(value = "浏览量(默认0)",defaultValue = "0")
    private String browseNum;

    @TableField(value = "comment_num")
    @ApiModelProperty(value = "评论量(默认0)",defaultValue = "0")
    private String commentNum;
}
