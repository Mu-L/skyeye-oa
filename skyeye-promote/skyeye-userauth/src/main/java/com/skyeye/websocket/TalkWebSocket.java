/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.chat.service.TalkChatHistoryService;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.enumclass.CompanyTalkGroupState;
import com.skyeye.eve.service.CompanyTalkGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @ClassName: TalkWebSocket
 * @Description: 聊天/消息推送
 * @author: skyeye云系列--卫志强
 * @date: 2020年11月14日 下午9:36:38
 * @Copyright: 2020 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Component
@ServerEndpoint("/talkwebsocket/{userId}")
public class TalkWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalkWebSocket.class);

    /**
     * 在线人数（独立用户数）
     */
    public static int onlineNumber = 0;

    /**
     * 总连接数（所有终端的连接数）
     */
    public static int totalConnections = 0;

    /**
     * 以用户ID为key，该用户的所有WebSocket连接为值
     * 支持一个用户多终端同时在线
     */
    private static final Map<String, Set<TalkWebSocket>> userSessions = new ConcurrentHashMap<>();

    /**
     * 会话ID到用户ID的映射，用于快速定位会话属于哪个用户
     */
    private static final Map<String, String> sessionIdToUserId = new ConcurrentHashMap<>();

    /**
     * 用户会话最后活跃时间，用于超时检测
     */
    private static final Map<String, Long> lastActiveTime = new ConcurrentHashMap<>();

    /**
     * 消息发送者，用于异步批量发送消息
     */
    private static final ExecutorService messageExecutor = Executors.newFixedThreadPool(5);

    /**
     * 心跳超时时间（毫秒），默认60秒
     */
    private static final long HEARTBEAT_TIMEOUT = 60000;

    /**
     * 会话超时时间（毫秒），默认30分钟
     */
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;

    /**
     * 会话
     */
    private Session session;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户会话ID，用于标识具体会话
     */
    private String sessionId;

    /**
     * 启动定时任务，处理超时连接
     */
    static {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 定时检查心跳超时的连接
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkHeartbeatTimeout();
            } catch (Exception e) {
                LOGGER.error("检查心跳超时异常", e);
            }
        }, 30, 30, TimeUnit.SECONDS);

        // 定时检查会话超时
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkSessionTimeout();
            } catch (Exception e) {
                LOGGER.error("检查会话超时异常", e);
            }
        }, 10, 10, TimeUnit.MINUTES);

        // 应用关闭时清理资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closeAllConnections();
                messageExecutor.shutdown();
                scheduler.shutdown();
            } catch (Exception e) {
                LOGGER.error("关闭连接资源异常", e);
            }
        }));
    }

    /**
     * 建立连接
     *
     * @param session WebSocket会话
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        try {
            boolean isNewUser = false;
            // 检查用户是否已经有其他终端连接
            if (!userSessions.containsKey(userId)) {
                // 新用户，创建一个新的连接集合
                userSessions.put(userId, ConcurrentHashMap.newKeySet());
                isNewUser = true;
                onlineNumber++;
            }

            totalConnections++;
            LOGGER.info("新连接加入 - 客户端ID: {}, 用户ID: {}, 当前在线人数: {}, 总连接数: {}",
                session.getId(), userId, onlineNumber, totalConnections);

            this.userId = userId;
            this.session = session;
            this.sessionId = session.getId();

            // 设置会话配置
            this.session.setMaxIdleTimeout(120000); // 设置会话超时时间为2分钟

            // 更新会话ID到用户ID的映射
            sessionIdToUserId.put(sessionId, userId);

            // 更新最后活跃时间
            lastActiveTime.put(userId, System.currentTimeMillis());

            // 将当前连接添加到用户的连接集合中
            userSessions.get(userId).add(this);

            // 如果是新用户，通知其他用户我上线了
            if (isNewUser) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("messageType", SocketConstants.MessageType.First.getType());
                map1.put("userId", userId);
                sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId); // 不给自己发送
            }

            // 给自己的所有终端发送一条消息：告诉当前有谁在线
            Map<String, Object> map2 = new HashMap<>();
            map2.put("messageType", SocketConstants.MessageType.Third.getType());
            map2.put("onlineUsers", userSessions.keySet());
            map2.put("terminals", userSessions.get(userId).size());  // 当前用户的终端数
            sendMessageTo(JSONUtil.toJsonStr(map2), userId);
        } catch (Exception e) {
            LOGGER.error("建立连接异常: {}", e.getMessage(), e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.warn("服务端发生了错误: {}", error.getMessage());
        try {
            // 通知客户端出错
            if (session.isOpen()) {
                Map<String, Object> errorMsg = new HashMap<>();
                errorMsg.put("messageType", "error");
                errorMsg.put("message", "连接发生错误，请尝试重新连接");
                session.getAsyncRemote().sendText(JSONUtil.toJsonStr(errorMsg));
            }
        } catch (Exception e) {
            LOGGER.error("发送错误通知失败: {}", e.getMessage());
        }
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        if (ToolUtil.isBlank(userId) || sessionId == null) {
            return;
        }

        try {
            // 更新会话ID到用户ID的映射
            sessionIdToUserId.remove(sessionId);

            // 从用户的会话集合中移除当前会话
            Set<TalkWebSocket> userSocketSet = userSessions.get(userId);
            if (userSocketSet != null) {
                userSocketSet.remove(this);
                totalConnections--;

                // 如果用户的所有连接都断开了，则从用户列表中移除
                if (userSocketSet.isEmpty()) {
                    userSessions.remove(userId);
                    onlineNumber--;

                    // 通知其他用户我下线了
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("messageType", SocketConstants.MessageType.Second.getType());
                    map1.put("onlineUsers", userSessions.keySet());
                    map1.put("userId", userId);
                    sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);

                    LOGGER.info("用户完全离线 - 用户ID: {}, 当前在线人数: {}, 总连接数: {}",
                        userId, onlineNumber, totalConnections);
                } else {
                    LOGGER.info("终端断开连接 - 用户ID: {}, 剩余终端数: {}, 当前在线人数: {}, 总连接数: {}",
                        userId, userSocketSet.size(), onlineNumber, totalConnections);
                }
            }
        } catch (Exception e) {
            LOGGER.error("关闭连接异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            LOGGER.info("来自客户端消息: {}, 客户端的id是: {}", message, session.getId());

            // 更新最后活跃时间
            if (userId != null) {
                lastActiveTime.put(userId, System.currentTimeMillis());
            }

            JSONObject jsonObject = JSONUtil.toBean(message, null);

            // 处理心跳消息
            String typeStr = jsonObject.getStr("type");
            if ("ping".equals(typeStr)) {
                // 回复pong消息
                JSONObject pongMessage = new JSONObject();
                pongMessage.put("type", "pong");
                pongMessage.put("timestamp", System.currentTimeMillis());
                if (this.session.isOpen()) {
                    this.session.getAsyncRemote().sendText(JSONUtil.toJsonStr(pongMessage));
                }
                return;
            }

            // 处理其他类型消息
            int type = Integer.parseInt(typeStr);
            Map<String, Object> map1 = new HashMap<>();
            map1.put("messageType", type);

            if (SocketConstants.MessageType.Fourth.getType() == type) {
                // 普通消息 - 使用事务保证数据一致性
                TransactionTemplate transactionTemplate = SpringUtils.getBean(TransactionTemplate.class);
                transactionTemplate.execute(status -> {
                    try {
                        // 插入消息记录
                        TalkChatHistoryService talkChatHistoryService = SpringUtils.getBean(TalkChatHistoryService.class);

                        String toUserId = jsonObject.getStr("to");
                        // 判断接收者是否在线，如果在线就发送
                        if (isUserOnline(toUserId)) {
                            String id = talkChatHistoryService.createEntity(jsonObject, TalkChatType.PERSONAL_TO_PERSONAL.getKey(), WhetherEnum.ENABLE_USING.getKey());
                            Map<String, Object> finalMap = new HashMap<>();
                            finalMap.put("messageType", type);
                            finalMap.put("dataId", id);
                            // 给接收者发送消息
                            finalMap.putAll(SocketConstants.sendOrdinaryMsg(jsonObject));
                            sendMessageTo(JSONUtil.toJsonStr(finalMap), toUserId);
                        } else {
                            talkChatHistoryService.createEntity(jsonObject, TalkChatType.PERSONAL_TO_PERSONAL.getKey(), WhetherEnum.DISABLE_USING.getKey());
                        }
                        return true;
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        LOGGER.error("发送个人消息失败: {}", e.getMessage(), e);
                        throw e;
                    }
                });
            } else if (SocketConstants.MessageType.Fifth.getType() == type) {
                // 系统消息
                LOGGER.info("收到系统消息: {}", jsonObject);
                // TODO: 实现系统消息处理逻辑
            } else if (SocketConstants.MessageType.Sixth.getType() == type) {
                // 全体消息
                map1 = SocketConstants.sendAllPeopleMsg(jsonObject);
                sendMessageToAll(JSONUtil.toJsonStr(map1)); // 给所有人发送，包括自己的其他终端
            } else if (SocketConstants.MessageType.Seventh.getType() == type) {
                // 群组邀请消息
                map1.put("toId", jsonObject.getStr("to")); // 收件人id
                sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
            } else if (SocketConstants.MessageType.Eighth.getType() == type) {
                // 隐身消息
                map1.put("userId", jsonObject.getStr("userId"));
                sendMessageToAll(JSONUtil.toJsonStr(map1));
            } else if (SocketConstants.MessageType.Ninth.getType() == type) {
                // 隐身上线消息
                map1.put("userId", jsonObject.getStr("userId"));
                sendMessageToAll(JSONUtil.toJsonStr(map1));
            } else if (SocketConstants.MessageType.Tenth.getType() == type) {
                // 搜索账号入群审核同意后通知用户加载群信息
                map1 = SocketConstants.sendAgreeJoinGroupMsg(jsonObject);
                sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
            } else if (SocketConstants.MessageType.Eleventh.getType() == type) {
                // 群聊 - 使用事务保证数据一致性
                TransactionTemplate transactionTemplate = SpringUtils.getBean(TransactionTemplate.class);
                transactionTemplate.execute(status -> {
                    try {
                        Map<String, Object> finalMap = new HashMap<>();
                        finalMap.put("messageType", type);
                        finalMap = SocketConstants.sendGroupTalkPeopleMsg(jsonObject);
                        CompanyTalkGroupService companyTalkGroupService = SpringUtils.getBean(CompanyTalkGroupService.class);
                        CompanyTalkGroup groupMation = companyTalkGroupService.selectById(finalMap.get("id").toString());
                        if (CompanyTalkGroupState.NORMAL.getKey() == groupMation.getState()) {//正常
                            //插入消息记录
                            TalkChatHistoryService talkChatHistoryService = SpringUtils.getBean(TalkChatHistoryService.class);
                            String id = talkChatHistoryService.createEntity(jsonObject, TalkChatType.GROUP_CHAT.getKey());
                            finalMap.put("createTime", DateUtil.getTimeAndToString());
                            finalMap.put("dataId", id);
                            // 发送给所有人，包括自己的其他终端
                            sendMessageToAll(JSONUtil.toJsonStr(finalMap));
                        } else {
                            finalMap.clear();
                            finalMap.put("messageType", "1301");
                            finalMap.put("groupId", jsonObject.getStr("to"));//收件人id，在此处为群聊id
                            sendMessageToSession(JSONUtil.toJsonStr(finalMap), this.session);
                        }
                        return true;
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        LOGGER.error("发送群聊消息失败: {}", e.getMessage(), e);
                        throw e;
                    }
                });
            } else if (SocketConstants.MessageType.Twelfth.getType() == type) {
                // 退出群聊--创建人接收消息
                map1 = SocketConstants.sendOutGroupToCreaterMsg(jsonObject);
                CompanyTalkGroupService companyTalkGroupService = SpringUtils.getBean(CompanyTalkGroupService.class);
                CompanyTalkGroup groupMation = companyTalkGroupService.selectById(map1.get("groupId").toString());
                map1.put("toId", groupMation.getCreateId());//收件人id
                sendMessageTo(JSONUtil.toJsonStr(map1), groupMation.getCreateId());
            } else if (SocketConstants.MessageType.Thirteenth.getType() == type) {
                // 解散群聊--所有人接收消息
                map1 = SocketConstants.sendDisbandGroupToAllMsg(jsonObject);
                sendMessageToAll(JSONUtil.toJsonStr(map1));
            }
        } catch (Exception e) {
            LOGGER.warn("处理消息时发生错误: {}", e.getMessage(), e);
            try {
                // 通知客户端消息处理错误
                JSONObject errorMsg = new JSONObject();
                errorMsg.put("type", "error");
                errorMsg.put("message", "消息处理失败");
                session.getAsyncRemote().sendText(JSONUtil.toJsonStr(errorMsg));
            } catch (Exception ex) {
                LOGGER.error("发送错误通知失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 发送消息给指定会话
     *
     * @param message 消息内容
     * @param session 会话对象
     */
    public void sendMessageToSession(String message, Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                LOGGER.error("发送消息给会话 {} 失败: {}", session.getId(), e.getMessage());
            }
        }
    }

    /**
     * 发送消息给指定用户的所有终端
     *
     * @param message 消息内容
     * @param userId  用户ID
     */
    public void sendMessageTo(String message, String userId) {
        Set<TalkWebSocket> sockets = userSessions.get(userId);
        if (sockets != null && !sockets.isEmpty()) {
            for (TalkWebSocket socket : sockets) {
                try {
                    if (socket.session.isOpen()) {
                        socket.session.getAsyncRemote().sendText(message);
                    }
                } catch (Exception e) {
                    LOGGER.error("发送消息给用户 {} 的终端 {} 失败: {}",
                        userId, socket.sessionId, e.getMessage());
                }
            }
        }
    }

    /**
     * 发送给全部用户的所有终端，除了指定用户
     *
     * @param message       消息内容
     * @param excludeUserId 排除的用户ID（不发送给该用户的所有终端）
     */
    public void sendMessageToAllExcept(String message, String excludeUserId) {
        // 使用线程池异步发送消息
        messageExecutor.submit(() -> {
            for (Map.Entry<String, Set<TalkWebSocket>> entry : userSessions.entrySet()) {
                String userId = entry.getKey();
                // 排除指定用户
                if (excludeUserId != null && excludeUserId.equals(userId)) {
                    continue;
                }

                Set<TalkWebSocket> sockets = entry.getValue();
                for (TalkWebSocket socket : sockets) {
                    try {
                        if (socket.session.isOpen()) {
                            socket.session.getAsyncRemote().sendText(message);
                        }
                    } catch (Exception e) {
                        LOGGER.error("发送全体消息给用户 {} 的终端 {} 失败: {}",
                            userId, socket.sessionId, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 发送给全部用户的所有终端，不排除任何用户
     *
     * @param message 消息内容
     */
    public void sendMessageToAll(String message) {
        sendMessageToAllExcept(message, null);
    }

    /**
     * 检查心跳超时的连接
     */
    private static void checkHeartbeatTimeout() {
        long now = System.currentTimeMillis();

        // 遍历所有用户的所有连接
        for (Map.Entry<String, Set<TalkWebSocket>> entry : userSessions.entrySet()) {
            String userId = entry.getKey();
            Set<TalkWebSocket> sockets = entry.getValue();

            // 检查该用户的活跃时间是否超时
            Long lastActive = lastActiveTime.get(userId);
            if (lastActive != null && now - lastActive > HEARTBEAT_TIMEOUT) {
                // 用户已超时，关闭所有连接
                LOGGER.info("用户 {} 心跳超时，关闭所有连接", userId);

                Set<TalkWebSocket> socketsToRemove = new HashSet<>();
                for (TalkWebSocket socket : sockets) {
                    try {
                        if (socket.session.isOpen()) {
                            socket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "心跳超时"));
                        }
                        sessionIdToUserId.remove(socket.sessionId);
                        socketsToRemove.add(socket);
                    } catch (IOException e) {
                        LOGGER.error("关闭超时连接失败: {}", e.getMessage());
                    }
                }

                // 从用户的连接集合中移除已关闭的连接
                sockets.removeAll(socketsToRemove);
                totalConnections -= socketsToRemove.size();

                // 如果用户没有任何连接了，从用户列表中移除
                if (sockets.isEmpty()) {
                    userSessions.remove(userId);
                    onlineNumber--;

                    // 通知其他用户该用户下线了
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("messageType", SocketConstants.MessageType.Second.getType());
                    map1.put("onlineUsers", userSessions.keySet());
                    map1.put("userId", userId);

                    // 创建一个临时对象用于发送消息
                    TalkWebSocket tempSocket = new TalkWebSocket();
                    tempSocket.sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);
                }

                // 重新更新最后活跃时间，防止重复处理
                lastActiveTime.put(userId, now);
            }
        }
    }

    /**
     * 检查会话超时，清理长时间无活动的会话
     */
    private static void checkSessionTimeout() {
        long now = System.currentTimeMillis();
        List<String> timeoutUsers = lastActiveTime.entrySet().stream()
            .filter(entry -> now - entry.getValue() > SESSION_TIMEOUT)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        for (String userId : timeoutUsers) {
            Set<TalkWebSocket> sockets = userSessions.remove(userId);
            lastActiveTime.remove(userId);

            if (sockets != null) {
                for (TalkWebSocket socket : sockets) {
                    try {
                        sessionIdToUserId.remove(socket.sessionId);
                        if (socket.session.isOpen()) {
                            socket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "会话超时"));
                        }
                    } catch (IOException e) {
                        LOGGER.error("关闭超时会话失败: {}", e.getMessage());
                    }
                }

                totalConnections -= sockets.size();
                onlineNumber--;

                // 通知其他用户该用户下线了
                Map<String, Object> map1 = new HashMap<>();
                map1.put("messageType", SocketConstants.MessageType.Second.getType());
                map1.put("onlineUsers", userSessions.keySet());
                map1.put("userId", userId);

                // 创建一个临时对象用于发送消息
                TalkWebSocket tempSocket = new TalkWebSocket();
                tempSocket.sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);

                LOGGER.info("用户 {} 会话超时，清理资源，当前在线人数: {}, 总连接数: {}",
                    userId, onlineNumber, totalConnections);
            }
        }
    }

    /**
     * 关闭所有连接
     */
    private static void closeAllConnections() {
        for (Map.Entry<String, Set<TalkWebSocket>> entry : userSessions.entrySet()) {
            for (TalkWebSocket socket : entry.getValue()) {
                try {
                    if (socket.session.isOpen()) {
                        socket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "服务器关闭"));
                    }
                } catch (IOException e) {
                    LOGGER.error("关闭连接失败: {}", e.getMessage());
                }
            }
        }
        userSessions.clear();
        sessionIdToUserId.clear();
        lastActiveTime.clear();
        onlineNumber = 0;
        totalConnections = 0;
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isUserOnline(String userId) {
        Set<TalkWebSocket> sockets = userSessions.get(userId);
        if (sockets == null || sockets.isEmpty()) {
            return false;
        }

        // 检查是否至少有一个连接是打开的
        for (TalkWebSocket socket : sockets) {
            if (socket.session.isOpen()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前在线的用户id
     *
     * @return 在线用户ID集合
     */
    public static Set<String> getOnlineUserId() {
        return userSessions.keySet();
    }

    /**
     * 获取在线人数（独立用户数）
     *
     * @return 在线人数
     */
    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }

    /**
     * 获取总连接数（所有终端的连接数）
     *
     * @return 总连接数
     */
    public static synchronized int getTotalConnections() {
        return totalConnections;
    }

    /**
     * 获取指定用户的连接数
     *
     * @param userId 用户ID
     * @return 该用户的连接数
     */
    public static int getUserConnectionCount(String userId) {
        Set<TalkWebSocket> sockets = userSessions.get(userId);
        return sockets != null ? sockets.size() : 0;
    }
}
