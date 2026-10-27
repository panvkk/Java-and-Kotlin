package com.kpanov.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.kpanov.common.util.Constants.BLACKLISTED_KEY_BEGINNING;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final StringRedisTemplate redisTemplate;

    public void blacklistToken(String token, Long expiration) {
        var key = BLACKLISTED_KEY_BEGINNING + token;
        var duration = Duration.ofMillis(expiration);

        redisTemplate.opsForValue().set(key, BLACKLISTED, duration);
    }

    private static final String BLACKLISTED = "blacklisted";
}
