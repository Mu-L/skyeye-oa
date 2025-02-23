package com.skyeye.eve.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.forum.classenum.NotificationTypeEnum;
import lombok.Data;

@Data
@TableName("forum_notice")
@ApiModel(value = "论坛通知实体类")
public class ForumNotice extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "notice_title")
    @ApiModelProperty(value = "通知标题", required = "required")
    private String noticeTitle;

    @TableField(value = "notice_content")
    @ApiModelProperty(value = "通知内容", required = "required")
    private String noticeContent;

    @TableField(value = "forum_id")
    @ApiModelProperty(value = "帖子id", required = "required")
    private String forumId;

    @TableField(value = "receive_id")
    @ApiModelProperty(value = "接收人id", required = "required")
    private String receiveId;

    @TableField(value = "type")
    @ApiModelProperty(value = "通知类型  1.帖子回复通知  2.发帖通知粉丝", required = "required", enumClass = NotificationTypeEnum.class)
    private Integer type;

    @TableField(value = "state")
    @ApiModelProperty(value = "通知状态  1.未读  2.已读", required = "required")
    private Integer state;
}
