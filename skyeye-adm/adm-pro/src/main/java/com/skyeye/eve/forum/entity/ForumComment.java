package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

@Data
@TableName("forum_comment")
@RedisCacheField(name = "forum:comment", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@ApiModel(value = "论坛评论实体类")
public class ForumComment extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "forum_id")
    @ApiModelProperty(value = "帖子id", required = "required")
    private String forumId;

    @TableField(value = "content")
    @ApiModelProperty(value = "评论内容", required = "required")
    private String content;

    @TableField(value = "comment_id")
    @ApiModelProperty(value = "评论人", required = "required")
    private String commentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "评论人信息")
    private Map<String, Object> commentMation;

    @TableField(value = "belong_comment_id")
    @ApiModelProperty(value = "回复时，属于哪个评论模块", required = "required")
    private String belongCommentId;

    @TableField(value = "reply_id")
    @ApiModelProperty(value = "评论回复人id")
    private String replyId;

    @TableField(exist = false)
    @ApiModelProperty(value = "评论回复人信息")
    private Map<String, Object> replyMation;

    @TableField(value = "comment_time")
    @ApiModelProperty(value = "评论时间", required = "required")
    private String commentTime;
}
