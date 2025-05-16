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
    private RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_PREFIX = "ticket-lock:";

    public boolean acquireLock(UUID ticketId, UUID userId, long ttlSeconds) {
        String key = LOCK_PREFIX + ticketId;
        String value = userId.toString();
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(ttlSeconds))
        );
    }

    public boolean isLockedByUser(UUID ticketId, UUID userId) {
        String key = LOCK_PREFIX + ticketId;
        String storedUserId = redisTemplate.opsForValue().get(key);
        return userId.toString().equals(storedUserId);
    }

    public void releaseLock(UUID ticketId, UUID userId) {
        String key = LOCK_PREFIX + ticketId;
        String storedUserId = redisTemplate.opsForValue().get(key);
        if (userId.toString().equals(storedUserId)) {
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
