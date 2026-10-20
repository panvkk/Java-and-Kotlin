package com.kpanov.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final StringRedisTemplate redisTemplate;

    public void blacklistToken(String token, Long duration) {
        var key = KEY_BEGINNING + token;
        redisTemplate.opsForValue().set(key, BLACKLISTED, duration);
    }

    public boolean isTokenNotBlacklisted(String token) {
        var key = KEY_BEGINNING + token;
        return !redisTemplate.hasKey(key);
    }

    private static final String BLACKLISTED = "blacklisted";
    private static final String KEY_BEGINNING = "blacklisted_access_token: ";
}
