package com.kpanov.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final StringRedisTemplate redisTemplate;

    public void blacklistToken(String token, Long duration) {
        redisTemplate.opsForValue().set(token, BLACKLISTED, duration);
    }

    public boolean isTokenNotBlacklisted(String token) {
        return !redisTemplate.hasKey(token);
    }

    private static final String BLACKLISTED = "blacklisted";
}
