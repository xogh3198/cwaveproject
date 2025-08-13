package com.example.demo.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class WaitingQueueService {

    private static final String QUEUE_KEY = "waiting:queue:";
    private final RedisTemplate<String, String> redisTemplate;

    public WaitingQueueService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Long enterQueue(Long performanceId, String userId) {
        String key = QUEUE_KEY + performanceId;
        Long queueSize = redisTemplate.opsForList().rightPush(key, userId);
        return queueSize;
    }

    public String leaveQueue(Long performanceId) {
        String key = QUEUE_KEY + performanceId;
        return redisTemplate.opsForList().leftPop(key, 1, TimeUnit.SECONDS);
    }
    
    public Long getQueueSize(Long performanceId) {
        String key = QUEUE_KEY + performanceId;
        return redisTemplate.opsForList().size(key);
    }
}