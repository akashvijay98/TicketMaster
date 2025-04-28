package com.example.TicketMaster.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class RedisLockService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    private static final String LOCK_PREFIX = "ticket-lock:";

    public boolean acquireLock(UUID ticketId, String lockId, long timeoutSec){
        String key = LOCK_PREFIX + ticketId;


        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, lockId, Duration.ofSeconds(timeoutSec))
        );

    }

    public void releaseLock(UUID ticketId, String lockId){
        String key = LOCK_PREFIX + ticketId;

        String currentLockId = (String )redisTemplate.opsForValue().get(key);
        if (lockId.equals(currentLockId)) {
            redisTemplate.delete(key);
        }
    }
    @PostConstruct
    public void testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("test-connection", "ok", Duration.ofSeconds(60));
            System.out.println("✅ Redis connection successful");
        } catch (Exception e) {
            System.out.println("❌ Redis connection failed: " + e.getMessage());
        }
    }
}
