package com.kpanov.gateway.blacklist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.BLACKLISTED_KEY_BEGINNING;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final StringRedisTemplate redisTemplate;

    public boolean isTokenBlacklisted(String token) {
        var key = BLACKLISTED_KEY_BEGINNING + token;
        return redisTemplate.hasKey(key);
    }
}
