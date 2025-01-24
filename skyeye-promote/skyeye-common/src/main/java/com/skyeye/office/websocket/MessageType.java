package com.skyeye.office.websocket;

/**
 * @ClassName: MessageType
 * @Description: WebSocket消息类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
public enum MessageType {
    EDIT("edit", "编辑操作"),
    CURSOR("cursor", "光标移动"),
    SELECTION("selection", "文本选择"),
    USER_JOIN("user_join", "用户加入"),
    USER_LEAVE("user_leave", "用户离开"),
    HEARTBEAT("heartbeat", "心跳检测");

    private final String type;
    private final String desc;

    MessageType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
} 