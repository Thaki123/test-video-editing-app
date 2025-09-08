package com.example.worker.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends progress updates to Redis and WebSocket channels.
 */
@Component
public class RedisProgressPublisher implements ProgressPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public RedisProgressPublisher(RedisTemplate<String, String> redisTemplate,
                                  SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publish(String jobId, int progress) {
        String message = Integer.toString(progress);
        if (redisTemplate != null) {
            redisTemplate.convertAndSend("progress:" + jobId, message);
        }
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/progress/" + jobId, message);
        }
    }
}
