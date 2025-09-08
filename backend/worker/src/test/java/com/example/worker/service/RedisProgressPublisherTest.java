package com.example.worker.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class RedisProgressPublisherTest {
    @Test
    void publishesToRedisAndWebsocket() {
        RedisTemplate<String, String> redis = mock(RedisTemplate.class);
        SimpMessagingTemplate ws = mock(SimpMessagingTemplate.class);
        RedisProgressPublisher publisher = new RedisProgressPublisher(redis, ws);

        publisher.publish("job", 50);

        verify(redis).convertAndSend("progress:job", "50");
        verify(ws).convertAndSend("/topic/progress/job", "50");
    }

    @Test
    void handlesNullTemplates() {
        RedisProgressPublisher publisher = new RedisProgressPublisher(null, null);
        publisher.publish("job", 10); // should not throw
    }
}
