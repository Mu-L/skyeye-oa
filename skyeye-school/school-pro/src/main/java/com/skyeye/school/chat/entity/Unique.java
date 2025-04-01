package com.skyeye.school.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@TableName(value = "school_talk_unique")
@ApiModel(value = "聊天会话实体类")
public class Unique extends CommonInfo {

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

    @TableField("create_time")
    @ApiModelProperty(value = "发送时间", required = "required")
    private String createTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "其他人具体信息")
    private Map<String, Object> OtherUserMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "最后一条聊天")
    private ChatHistory LastMessage;


}
