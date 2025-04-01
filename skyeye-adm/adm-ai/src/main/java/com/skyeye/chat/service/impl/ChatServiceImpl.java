package com.skyeye.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.core.builder.ChatBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.ai.core.enums.AiPlatformEnum;
import com.skyeye.ai.core.factory.AiFactory;
import com.skyeye.aiStreamModle.SparkListener;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chat.dao.ChatDao;
import com.skyeye.chat.entity.Chat;
import com.skyeye.chat.service.ChatService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.key.entity.AiApiKey;
import com.skyeye.key.service.AiApiKeyService;
import com.skyeye.role.service.RoleService;
import com.skyeye.websocket.AiMessageWebSocket;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import io.reactivex.Flowable;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * @ClassName: ChatServiceImpl
 * @Description: 聊天记录接口实现层
 * @author: skyeye云系列--lqy
 * @date: 2024/10/5 17:24
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ChatServiceImpl extends SkyeyeBusinessServiceImpl<ChatDao, Chat> implements ChatService {

    @Autowired
    private AiFactory aiFactory;

    @Autowired
    private AiApiKeyService aiApiKeyService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private Executor messageStreamExecutor;

    @Autowired
    private AiMessageWebSocket aiMessageWebSocket;

    @Override
    @Transactional
    public void sendChatMessage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String content = params.get("content").toString();
        String apiKeyId = params.get("apiKeyId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Chat chat = new Chat();
        AiApiKey aiApiKey = aiApiKeyService.selectById(apiKeyId);
        String platform = aiApiKey.getPlatform();
        // 获取到具有的ai模型
        AiPlatformEnum aiModel = AiPlatformEnum.getValue(platform);
        // 获取role实例
        com.skyeye.role.entity.Role role = roleService.selectById(aiApiKey.getRoleId());
        // 创建AI实例
        chat.setMessage(content);
        chat.setPlatform(platform);
        chat.setApiKeyId(apiKeyId);
        String id = createEntity(chat, userId);
        switch (aiModel) {
            case YI_YAN:
                QianFanResponse(content, userId, id, aiApiKey);
                break;
            case XUN_FEI:
                XunFeiResponse(content, userId, id, aiApiKey);
                break;
            case TONG_YI:
                TongYiResponse(content, userId, id, aiApiKey);
                break;
        }
        aiApiKey.setRoleMation(role);
        chat.setApiKeyMation(aiApiKey);

        outputObject.setBean(chat);
        outputObject.setreturnCode(CommonNumConstants.NUM_ZERO);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private void QianFanResponse(String message, String userId, String chatId, AiApiKey aiApiKey) {
        // 开启异步请求
        messageStreamExecutor.execute(() -> {
            Qianfan qianfan = (Qianfan) aiFactory.getDefaultChatModel(AiPlatformEnum.YI_YAN, aiApiKey);
            ChatBuilder model = qianfan.chatCompletion()
                .model("ERNIE-Speed-8K");
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());

            chatList.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    model.addMessage("user", chat.getMessage())
                        .addMessage("assistant", chat.getContent());
                }
            });
            model.addMessage("user", message);
            model.executeStream()
                .forEachRemaining(chunk -> {
                    String key = String.format(Locale.ROOT, "chat:%s", chatId);
                    List<String> chunkMessage = new ArrayList<>();
                    if (jedisClientService.exists(key)) {
                        chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
                    }
                    chunkMessage.add(chunk.getResult());
                    if (chunk.getEnd()) {
                        jedisClientService.del(key);
                        String content = Joiner.on("").join(chunkMessage);
                        // 修改回复内容
                        Chat chat = chatService.selectById(chatId);
                        chat.setContent(content);
                        chatService.updateEntity(chat, userId);
                    } else {
                        jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
                    }
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("message", chunk.getResult());
                    messageMap.put("end", chunk.getEnd());
                    messageMap.put("orderBy", chunk.getSentenceId());
                    aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
                });
        });

    }

    private void XunFeiResponse(String message, String userId, String chatId, AiApiKey aiApiKey) {
        messageStreamExecutor.execute(() -> {
            List<SparkMessage> messageList = new ArrayList<>();
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            chatList.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    messageList.add(SparkMessage.userContent(chat.getMessage()));
                    messageList.add(SparkMessage.systemContent(chat.getContent()));
                }
            });
            messageList.add(SparkMessage.userContent(message));

            SparkClient sparkClient = (SparkClient) aiFactory.getDefaultChatModel(AiPlatformEnum.XUN_FEI, aiApiKey);
            // 构造请求
            SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messageList)
                .maxTokens(2048)
                .temperature(0.5)
                .apiVersion(SparkApiVersion.V3_5)
                .build();
            // 封装聊天信息
            sparkClient.chatStream(sparkRequest, new SparkListener() {
                @Override
                public void onMessage(String content, SparkResponseUsage usage, Integer status, SparkRequest sparkRequest, SparkResponse sparkResponse, WebSocket webSocket) {

                    try {
                        JSONObject jsonObject = new JSONObject(this.objectMapper.writeValueAsString(sparkResponse));
                        // 获取payload对象
                        JSONObject payload = jsonObject.getJSONObject("payload");
                        // 获取choices对象
                        JSONObject choices = payload.getJSONObject("choices");
                        String key = String.format(Locale.ROOT, "chat:%s", chatId);
                        List<String> chunkMessage = new ArrayList<>();
                        if (jedisClientService.exists(key)) {
                            chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
                        }
                        chunkMessage.add(content);
                        if (status == 2) {
                            jedisClientService.del(key);
                            String totalMessage = Joiner.on("").join(chunkMessage);
                            // 修改回复内容
                            Chat chat = chatService.selectById(chatId);
                            chat.setContent(totalMessage);
                            chatService.updateEntity(chat, userId);
                        } else {
                            jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
                        }
                        Map<String, Object> messageMap = new HashMap<>();
                        messageMap.put("message", content);
                        messageMap.put("end", status == 2);
                        messageMap.put("orderBy", choices.get("seq"));
                        aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
                    } catch (JsonProcessingException var9) {
                        JsonProcessingException e = var9;
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }

    private List<Chat> getRecentlyChats(String userId, String apiKeyId) {
        // 获取聊天记录
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getApiKeyId), apiKeyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Chat::getCreateTime));
        PageHelper.startPage(1, 10);
        List<Chat> chatList = chatService.list(queryWrapper);
        return chatList;
    }

    private void TongYiResponse(String message, String userId, String chatId, AiApiKey aiApiKey) {
        // 开启异步请求：
        messageStreamExecutor.execute(() -> {
            List<Message> messages = new ArrayList<>();
            Message question = Message.builder().role(Role.USER.getValue()).content(message).build();
            Generation generation = (Generation) aiFactory.getDefaultChatModel(AiPlatformEnum.TONG_YI, aiApiKey);
            List<Chat> chatList = getRecentlyChats(userId, aiApiKey.getId());
            chatList.forEach(chat -> {
                if (StrUtil.isNotEmpty(chat.getMessage()) && StrUtil.isNotEmpty(chat.getContent())) {
                    Message userMsg = Message.builder().role(Role.USER.getValue()).content(chat.getMessage()).build();
                    Message assistantMsg = Message.builder().role(Role.SYSTEM.getValue()).content(chat.getContent()).build();
                    messages.add(userMsg);
                    messages.add(assistantMsg);
                }
            });
            messages.add(question);
            GenerationParam param = GenerationParam.builder()
                //指定用于对话的通义千问模型名
                .model("qwen-turbo")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                //生成过程中核采样方法概率阈值，例如，取值为0.8时，仅保留概率加起来大于等于0.8的最可能token的最小集合作为候选集。
                // 取值范围为（0,1.0)，取值越大，生成的随机性越高；取值越低，生成的确定性越高。
                .topP(0.8)
                //阿里云控制台DASHSCOPE获取的api-key
                .apiKey(aiApiKey.getApiKey())
                //启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑“自行判断”是否使用互联网搜索结果。
                .enableSearch(true)
                .incrementalOutput(true)
                .build();
            Flowable<GenerationResult> result;
            try {
                result = generation.streamCall(param);
            } catch (NoApiKeyException e) {
                throw new RuntimeException(e);
            } catch (InputRequiredException e) {
                throw new RuntimeException(e);
            }
            result.blockingForEach(chunk -> {
                String key = String.format(Locale.ROOT, "chat:%s", chatId);
                List<String> chunkMessage = new ArrayList<>();
                if (jedisClientService.exists(key)) {
                    chunkMessage = JSONUtil.toList(jedisClientService.get(key), null);
                }
                Boolean end = false;
                List<GenerationOutput.Choice> choiceList = chunk.getOutput().getChoices();
                GenerationOutput.Choice choice = choiceList.stream().findFirst().orElse(null);
                if (ObjectUtil.isEmpty(choice)) {
                    end = true;
                    chunkMessage.add(StrUtil.EMPTY);
                } else {
                    if (choice.getFinishReason().equals("stop")) {
                        end = true;
                    }
                    chunkMessage.add(choice.getMessage().getContent());
                }
                if (end) {
                    jedisClientService.del(key);
                    String content = Joiner.on("").join(chunkMessage);
                    // 修改回复内容
                    Chat chat = chatService.selectById(chatId);
                    chat.setContent(content);
                    chatService.updateEntity(chat, userId);
                } else {
                    jedisClientService.set(key, JSONUtil.toJsonStr(chunkMessage));
                }
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", chunkMessage.get(chunkMessage.size() - 1));
                messageMap.put("end", end);
                messageMap.put("orderBy", chunkMessage.size() - 1);
                aiMessageWebSocket.sendMessageTo(JSONUtil.toJsonStr(messageMap), userId);
            });
        });
    }


    @Override
    public void queryPageMessageList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String apiKeyId = commonPageInfo.getHolderId();
        if (StrUtil.isEmpty(apiKeyId)) {
            throw new CustomException("apiKeyId不能为空");
        }

        AiApiKey aiApiKey = aiApiKeyService.selectById(apiKeyId);
        com.skyeye.role.entity.Role role = roleService.selectById(aiApiKey.getRoleId());
        aiApiKey.setRoleMation(role);

        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getApiKeyId), apiKeyId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Chat::getCreateTime));
        List<Chat> chatList = list(queryWrapper);

        for (Chat chat : chatList) {
            chat.setApiKeyMation(aiApiKey);
        }
        outputObject.setBeans(chatList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void deleteAllByApiKeyId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String apiKeyId = params.get("apiKeyId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getApiKeyId), apiKeyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chat::getCreateId), userId);
        remove(queryWrapper);
    }

}
