/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * WebSocket发送失败日志
 */
@Data
@TableName(value = "ws_send_fail_log", autoResultMap = true)
@ApiModel("WebSocket发送失败日志")
public class WebSocketSendFailLog extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("target_user_id")
    @ApiModelProperty(value = "发送目标用户id", fuzzyLike = true)
    private String targetUserId;

    @TableField("tenant_id")
    @ApiModelProperty(value = "目标连接所属租户id", fuzzyLike = true)
    private String tenantId;

    @TableField("session_id")
    @ApiModelProperty(value = "目标会话id", fuzzyLike = true)
    private String sessionId;

    @TableField("send_stage")
    @ApiModelProperty(value = "失败阶段(async/basic)", fuzzyLike = true)
    private String sendStage;

    @TableField("message_content")
    @ApiModelProperty(value = "消息内容", fuzzyLike = true)
    private String messageContent;

    @TableField("error_message")
    @ApiModelProperty(value = "错误信息", fuzzyLike = true)
    private String errorMessage;

    @TableField("node_id")
    @ApiModelProperty(value = "节点id", fuzzyLike = true)
    private String nodeId;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private String createTime;
}

