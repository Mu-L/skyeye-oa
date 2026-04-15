package com.skyeye.websocket.config;

import com.skyeye.websocket.listener.TalkWebSocketClusterSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static com.skyeye.websocket.TalkWebSocket.WS_CLUSTER_CHANNEL;

/**
 * WebSocket跨节点分发Redis监听配置
 */
@Configuration
public class TalkWebSocketRedisConfig {

    @Bean
    public RedisMessageListenerContainer talkWsRedisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory,
        TalkWebSocketClusterSubscriber subscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(subscriber, new ChannelTopic(WS_CLUSTER_CHANNEL));
        return container;
    }
}
