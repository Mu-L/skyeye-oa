package com.skyeye.aiStreamModle;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.listener.SparkConsoleListener;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import okhttp3.WebSocket;

import java.util.List;

/**
 * @ClassName: SparkConsoleListener
 * @Description: AI流式处理类
 * @author: skyeye云系列--lqy
 * @date: 2024/10/20 22:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SparkListener extends SparkConsoleListener {

    private final StringBuilder stringBuilder = new StringBuilder();

    public SparkListener() {
        super();
    }

    @Override
    public void onMessage(String content, SparkResponseUsage usage, Integer status, SparkRequest sparkRequest, SparkResponse sparkResponse, WebSocket webSocket) {
        this.stringBuilder.append(content);
        if (0 == status) {
            List<SparkMessage> messages = sparkRequest.getPayload().getMessage().getText();

            try {
                SparkApiVersion apiVersion = sparkRequest.getApiVersion();
                System.out.println("请求地址：" + apiVersion.getUrl() + "  版本：" + apiVersion.getVersion());
                System.out.println("\n提问：" + this.objectMapper.writeValueAsString(messages));
            } catch (JsonProcessingException var10) {
                JsonProcessingException e = var10;
                throw new RuntimeException(e);
            }

            System.out.println("\n收到回答：\n");
        }

        try {
            System.out.println("--content：" + content + " --完整响应：" + this.objectMapper.writeValueAsString(sparkResponse));
        } catch (JsonProcessingException var9) {
            JsonProcessingException e = var9;
            throw new RuntimeException(e);
        }

        if (2 == status) {
            System.out.println("\n完整回答：" + this.stringBuilder);
            SparkTextUsage textUsage = usage.getText();
            System.out.println("\n回答结束；提问tokens：" + textUsage.getPromptTokens() + "，回答tokens：" + textUsage.getCompletionTokens() + "，总消耗tokens：" + textUsage.getTotalTokens());
        }

    }
}
