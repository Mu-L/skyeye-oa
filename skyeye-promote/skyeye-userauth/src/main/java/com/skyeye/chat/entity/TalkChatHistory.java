/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: TalkChatHistory
 * @Description: 聊天历史记录实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/12 14:18
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_talk_chat_history")
@ApiModel(value = "聊天历史记录实体类")
public class TalkChatHistory extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("unique_id")
    @ApiModelProperty(value = "会话id，两个人聊天的唯一标识", required = "required")
    private String uniqueId;

    @TableField("send_id")
    @ApiModelProperty(value = "消息发送人", required = "required")
    private String sendId;

    @TableField(exist = false)
    @Property(value = "消息发送人员工信息")
    private Map<String, Object> sendStaffMation;

    @TableField("receive_id")
    @ApiModelProperty(value = "消息接收人", required = "required")
    private String receiveId;

    @TableField("content")
    @ApiModelProperty(value = "聊天内容", required = "required")
    private String content;

    @TableField("read_type")
    @ApiModelProperty(value = "是否已读", enumClass = WhetherEnum.class, required = "required,num")
    private Integer readType;

    @TableField("chat_type")
    @ApiModelProperty(value = "消息类型，该字段决定receive_id值的类型", enumClass = TalkChatType.class, required = "required,num")
    private Integer chatType;

    @TableField("create_time")
    @ApiModelProperty(value = "发送时间", required = "required")
    private String createTime;

}
