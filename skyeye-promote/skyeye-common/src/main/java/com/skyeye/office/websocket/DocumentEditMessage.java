package com.skyeye.office.websocket;

import lombok.Data;

/**
 * @ClassName: DocumentEditMessage
 * @Description: 文档编辑消息实体类
 * 用于在WebSocket通信中传递各类消息，包括：
 * 1. 编辑操作消息
 * 2. 用户状态消息（加入/离开）
 * 3. 光标和选择范围同步消息
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Data
public class DocumentEditMessage {
    
    /**
     * 消息类型
     * @see MessageType
     */
    private String type;
    
    /**
     * 用户ID
     * 标识消息发送者
     */
    private String userId;
    
    /**
     * 文档ID
     * 标识操作的目标文档
     */
    private String documentId;
    
    /**
     * 消息内容
     * 根据消息类型不同，内容格式也不同
     */
    private Object content;
    
    /**
     * 时间戳
     * 消息发送的时间，用于消息排序和超时判断
     */
    private Long timestamp;

    /**
     * 版本号
     * 用于文档内容的版本控制
     */
    private Integer version;

    /**
     * 操作类型（对于编辑消息）
     * 例如：插入、删除、更新等
     */
    private String operation;

    /**
     * 操作位置（对于编辑消息）
     * 标识编辑操作发生的位置
     */
    private Integer position;

    /**
     * 创建编辑消息
     * @param documentId 文档ID
     * @param userId 用户ID
     * @param operation 操作类型
     * @param position 操作位置
     * @param content 操作内容
     * @return 编辑消息对象
     */
    public static DocumentEditMessage createEditMessage(String documentId, String userId, 
            String operation, Integer position, Object content) {
        DocumentEditMessage message = new DocumentEditMessage();
        message.setType(MessageType.EDIT.getType());
        message.setDocumentId(documentId);
        message.setUserId(userId);
        message.setOperation(operation);
        message.setPosition(position);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }

    /**
     * 创建用户状态消息
     * @param documentId 文档ID
     * @param userId 用户ID
     * @param type 消息类型（用户加入/离开）
     * @return 用户状态消息对象
     */
    public static DocumentEditMessage createUserStatusMessage(String documentId, String userId, 
            MessageType type) {
        DocumentEditMessage message = new DocumentEditMessage();
        message.setType(type.getType());
        message.setDocumentId(documentId);
        message.setUserId(userId);
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }
} 