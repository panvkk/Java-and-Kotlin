package com.kpanov.gateway.blacklist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final StringRedisTemplate redisTemplate;

    public boolean isTokenBlacklisted(String token) {
        var key = KEY_BEGINNING + token;
        return redisTemplate.hasKey(key);
    }

    private static final String KEY_BEGINNING = "blacklisted_access_token: ";
}
