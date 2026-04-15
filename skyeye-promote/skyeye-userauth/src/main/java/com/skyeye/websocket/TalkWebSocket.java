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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
    private static final AtomicInteger onlineNumber = new AtomicInteger(0);

    /**
     * 总连接数（所有终端的连接数）
     */
    private static final AtomicInteger totalConnections = new AtomicInteger(0);

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
    private static final ExecutorService messageExecutor = new ThreadPoolExecutor(
        5, 10, 60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(2000),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 心跳超时时间（毫秒），默认60秒
     */
    private static final long HEARTBEAT_TIMEOUT = 60000;

    /**
     * 会话超时时间（毫秒），默认30分钟
     */
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;

    /**
     * 单用户最大连接数，防止异常客户端占用过多连接。
     */
    private static final int MAX_CONNECTIONS_PER_USER = 5;

    /**
     * 系统最大连接数，达到上限后拒绝新连接。
     */
    private static final int MAX_TOTAL_CONNECTIONS = 20000;

    /**
     * 连接/发送可观测性指标
     */
    private static final AtomicLong rejectedConnectionCount = new AtomicLong(0);
    private static final AtomicLong broadcastRejectCount = new AtomicLong(0);
    private static final AtomicLong sendFailureCount = new AtomicLong(0);

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

        // 定时打印连接与发送指标，方便容量观察
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logRuntimeMetrics();
            } catch (Exception e) {
                LOGGER.error("打印WebSocket运行指标异常", e);
            }
        }, 1, 1, TimeUnit.MINUTES);

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
            if (ToolUtil.isBlank(userId)) {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "userId不能为空"));
                return;
            }

            if (totalConnections.get() >= MAX_TOTAL_CONNECTIONS) {
                rejectedConnectionCount.incrementAndGet();
                session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "连接已达上限"));
                LOGGER.warn("拒绝连接，达到系统连接上限: {}, userId: {}", MAX_TOTAL_CONNECTIONS, userId);
                return;
            }

            this.userId = userId;
            this.session = session;
            this.sessionId = session.getId();

            Set<TalkWebSocket> userSocketSet = userSessions.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet());
            boolean isNewUser;
            synchronized (userSocketSet) {
                if (userSocketSet.size() >= MAX_CONNECTIONS_PER_USER) {
                    rejectedConnectionCount.incrementAndGet();
                    session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "用户连接数超限"));
                    LOGGER.warn("拒绝连接，用户 {} 超过最大连接数: {}", userId, MAX_CONNECTIONS_PER_USER);
                    return;
                }
                isNewUser = userSocketSet.isEmpty();
                userSocketSet.add(this);
            }

            // 设置会话配置
            this.session.setMaxIdleTimeout(120000); // 设置会话超时时间为2分钟

            // 更新会话ID到用户ID的映射
            sessionIdToUserId.put(sessionId, userId);

            // 更新最后活跃时间
            lastActiveTime.put(sessionId, System.currentTimeMillis());
            totalConnections.incrementAndGet();
            if (isNewUser) {
                onlineNumber.incrementAndGet();
            }
            LOGGER.info("新连接加入 - 客户端ID: {}, 用户ID: {}, 当前在线人数: {}, 总连接数: {}",
                session.getId(), userId, onlineNumber.get(), totalConnections.get());

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
            sendMessageTo(JSONUtil.toJsonStr(map2), userId, null);
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
            lastActiveTime.remove(sessionId);

            // 从用户的会话集合中移除当前会话
            Set<TalkWebSocket> userSocketSet = userSessions.get(userId);
            if (userSocketSet != null) {
                userSocketSet.remove(this);
                totalConnections.decrementAndGet();

                // 如果用户的所有连接都断开了，则从用户列表中移除
                if (userSocketSet.isEmpty()) {
                    userSessions.remove(userId);
                    onlineNumber.decrementAndGet();

                    // 通知其他用户我下线了
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("messageType", SocketConstants.MessageType.Second.getType());
                    map1.put("onlineUsers", userSessions.keySet());
                    map1.put("userId", userId);
                    sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);

                    LOGGER.info("用户完全离线 - 用户ID: {}, 当前在线人数: {}, 总连接数: {}",
                        userId, onlineNumber.get(), totalConnections.get());
                } else {
                    LOGGER.info("终端断开连接 - 用户ID: {}, 剩余终端数: {}, 当前在线人数: {}, 总连接数: {}",
                        userId, userSocketSet.size(), onlineNumber.get(), totalConnections.get());
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
            if (sessionId != null) {
                lastActiveTime.put(sessionId, System.currentTimeMillis());
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
                        String id = talkChatHistoryService.createEntity(jsonObject, TalkChatType.PERSONAL_TO_PERSONAL.getKey(), WhetherEnum.DISABLE_USING.getKey());
                        // 判断接收者是否在线，如果在线就发送
                        if (isUserOnline(toUserId)) {
                            Map<String, Object> finalMap = new HashMap<>();
                            finalMap.put("messageType", type);
                            finalMap.put("dataId", id);
                            // 给接收者发送消息
                            finalMap.putAll(SocketConstants.sendOrdinaryMsg(jsonObject));
                            sendMessageTo(JSONUtil.toJsonStr(finalMap), toUserId, null);
                        }
                        // 给发送者发送消息，保证其他终端收到消息
                        sendMessageToMe(jsonObject.getStr("message"), toUserId, type);
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
            } else if (SocketConstants.MessageType.Sixth.getType() == type) {
                // 全体消息
                map1 = SocketConstants.sendAllPeopleMsg(jsonObject);
                sendMessageToAll(JSONUtil.toJsonStr(map1)); // 给所有人发送，包括自己的其他终端
            } else if (SocketConstants.MessageType.Seventh.getType() == type) {
                // 群组邀请消息
                map1.put("toId", jsonObject.getStr("to")); // 收件人id
                sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"), null);
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
                sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"), null);
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
                sendMessageTo(JSONUtil.toJsonStr(map1), groupMation.getCreateId(), null);
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
     * 发送给自己的其他终端
     *
     * @param messageStr 消息内容
     * @param talkId     聊天对象id(接收者id)
     * @param msgType    消息类型
     */
    public void sendMessageToMe(String messageStr, String talkId, Integer msgType) {
        Map<String, Object> sendMsg = new HashMap<>();
        // 我的用户id
        sendMsg.put("myId", this.userId);
        sendMsg.put("type", SocketConstants.MessageType.SendToMe.getType());
        sendMsg.put("message", messageStr);
        sendMsg.put("msgType", msgType);
        sendMsg.put("talkId", talkId);
        sendMessageTo(JSONUtil.toJsonStr(sendMsg), this.userId, this.sessionId);
    }

    /**
     * 发送消息给指定会话
     *
     * @param message 消息内容
     * @param session 会话对象
     */
    public void sendMessageToSession(String message, Session session) {
        if (session != null && session.isOpen()) {
            sendTextWithRetry(message, session, session.getId(), "session");
        }
    }

    /**
     * 发送消息给指定用户的所有终端
     *
     * @param message      消息内容
     * @param userId       用户ID
     * @param notSessionId 不发送的终端id
     */
    public void sendMessageTo(String message, String userId, String notSessionId) {
        Set<TalkWebSocket> sockets = userSessions.get(userId);
        if (sockets != null && !sockets.isEmpty()) {
            for (TalkWebSocket socket : sockets) {
                try {
                    if (socket.session.isOpen()) {
                        if (notSessionId != null && notSessionId.equals(socket.sessionId)) {
                            continue;
                        }
                        sendTextWithRetry(message, socket.session, socket.sessionId, userId);
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
        try {
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
                                sendTextWithRetry(message, socket.session, socket.sessionId, userId);
                            }
                        } catch (Exception e) {
                            LOGGER.error("发送全体消息给用户 {} 的终端 {} 失败: {}",
                                userId, socket.sessionId, e.getMessage());
                        }
                    }
                }
            });
        } catch (RejectedExecutionException ex) {
            broadcastRejectCount.incrementAndGet();
            LOGGER.warn("广播任务被拒绝，当前系统繁忙。excludeUserId: {}", excludeUserId);
        }
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
            Set<TalkWebSocket> timeoutSockets = new HashSet<>();
            for (TalkWebSocket socket : sockets) {
                Long lastActive = lastActiveTime.get(socket.sessionId);
                if (lastActive != null && now - lastActive > HEARTBEAT_TIMEOUT) {
                    timeoutSockets.add(socket);
                }
            }
            if (timeoutSockets.isEmpty()) {
                continue;
            }

            LOGGER.info("用户 {} 检测到 {} 个心跳超时连接", userId, timeoutSockets.size());
            for (TalkWebSocket socket : timeoutSockets) {
                try {
                    if (socket.session.isOpen()) {
                        socket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "心跳超时"));
                    }
                } catch (IOException e) {
                    LOGGER.error("关闭超时连接失败: {}", e.getMessage());
                } finally {
                    sessionIdToUserId.remove(socket.sessionId);
                    lastActiveTime.remove(socket.sessionId);
                    sockets.remove(socket);
                    totalConnections.decrementAndGet();
                }
            }

            if (sockets.isEmpty()) {
                userSessions.remove(userId);
                onlineNumber.decrementAndGet();
                Map<String, Object> map1 = new HashMap<>();
                map1.put("messageType", SocketConstants.MessageType.Second.getType());
                map1.put("onlineUsers", userSessions.keySet());
                map1.put("userId", userId);
                TalkWebSocket tempSocket = new TalkWebSocket();
                tempSocket.sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);
            }
        }
    }

    /**
     * 检查会话超时，清理长时间无活动的会话
     */
    private static void checkSessionTimeout() {
        long now = System.currentTimeMillis();
        List<String> timeoutSessions = lastActiveTime.entrySet().stream()
            .filter(entry -> now - entry.getValue() > SESSION_TIMEOUT)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        for (String timeoutSessionId : timeoutSessions) {
            String userId = sessionIdToUserId.remove(timeoutSessionId);
            lastActiveTime.remove(timeoutSessionId);
            if (ToolUtil.isBlank(userId)) {
                continue;
            }
            Set<TalkWebSocket> sockets = userSessions.get(userId);
            if (sockets == null || sockets.isEmpty()) {
                continue;
            }

            TalkWebSocket timeoutSocket = null;
            for (TalkWebSocket socket : sockets) {
                if (timeoutSessionId.equals(socket.sessionId)) {
                    timeoutSocket = socket;
                    break;
                }
            }
            if (timeoutSocket == null) {
                continue;
            }
            try {
                if (timeoutSocket.session.isOpen()) {
                    timeoutSocket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "会话超时"));
                }
            } catch (IOException e) {
                LOGGER.error("关闭超时会话失败: {}", e.getMessage());
            } finally {
                sockets.remove(timeoutSocket);
                totalConnections.decrementAndGet();
            }

            if (sockets.isEmpty()) {
                userSessions.remove(userId);
                onlineNumber.decrementAndGet();
                Map<String, Object> map1 = new HashMap<>();
                map1.put("messageType", SocketConstants.MessageType.Second.getType());
                map1.put("onlineUsers", userSessions.keySet());
                map1.put("userId", userId);
                TalkWebSocket tempSocket = new TalkWebSocket();
                tempSocket.sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);

                LOGGER.info("用户 {} 会话全部超时离线，当前在线人数: {}, 总连接数: {}",
                    userId, onlineNumber.get(), totalConnections.get());
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
        onlineNumber.set(0);
        totalConnections.set(0);
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
        return onlineNumber.get();
    }

    /**
     * 获取总连接数（所有终端的连接数）
     *
     * @return 总连接数
     */
    public static synchronized int getTotalConnections() {
        return totalConnections.get();
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

    public static long getRejectedConnectionCount() {
        return rejectedConnectionCount.get();
    }

    public static long getBroadcastRejectCount() {
        return broadcastRejectCount.get();
    }

    public static long getSendFailureCount() {
        return sendFailureCount.get();
    }

    public static int getActiveSessionRecordCount() {
        return lastActiveTime.size();
    }

    public static Map<String, Object> getRuntimeMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("onlineCount", getOnlineCount());
        metrics.put("totalConnections", getTotalConnections());
        metrics.put("activeSessionRecords", getActiveSessionRecordCount());
        metrics.put("rejectedConnectionCount", getRejectedConnectionCount());
        metrics.put("broadcastRejectCount", getBroadcastRejectCount());
        metrics.put("sendFailureCount", getSendFailureCount());
        metrics.put("collectTime", DateUtil.getTimeAndToString());
        return metrics;
    }

    private static void sendTextWithRetry(String message, Session session, String sessionId, String target) {
        try {
            session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            sendFailureCount.incrementAndGet();
            LOGGER.warn("发送消息失败，准备重试。target: {}, sessionId: {}, error: {}", target, sessionId, e.getMessage());
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception retryError) {
                sendFailureCount.incrementAndGet();
                LOGGER.error("发送消息重试失败。target: {}, sessionId: {}, error: {}", target, sessionId, retryError.getMessage());
            }
        }
    }

    private static void logRuntimeMetrics() {
        LOGGER.info(
            "WebSocket运行指标 -> 在线用户: {}, 总连接: {}, 拒绝连接累计: {}, 广播拒绝累计: {}, 发送失败累计: {}, 活跃session记录: {}",
            onlineNumber.get(), totalConnections.get(), rejectedConnectionCount.get(),
            broadcastRejectCount.get(), sendFailureCount.get(), lastActiveTime.size()
        );
    }
}
