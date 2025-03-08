package com.skyeye.school.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@TableName(value = "school_talk_chat_history")
@ApiModel(value = "聊天历史实体类")
public class ChatHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("unique_id")
    @ApiModelProperty(value = "两个人聊天的唯一标识")
    private String uniqueId;

    @TableField("send_id")
    @ApiModelProperty(value = "消息发送人", required = "required")
    private String sendId;

    @TableField("receive_id")
    @ApiModelProperty(value = "消息接收人", required = "required")
    private String receiveId;

    @TableField("content")
    @ApiModelProperty(value = "聊天内容", required = "required")
    private String content;

    @TableField("read_type")
    @ApiModelProperty(value = "是否已读，默认为0", required = "required")
    private Integer readType;

    @TableField("chat_type")
    @ApiModelProperty(value = "消息类型", required = "required")
    private Integer chatType;

}
