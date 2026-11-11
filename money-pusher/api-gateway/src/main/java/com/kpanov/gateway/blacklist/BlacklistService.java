package com.kpanov.gateway.blacklist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.kpanov.common.util.Constants.BLACKLISTED_KEY_BEGINNING;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Boolean> isTokenBlacklisted(String token) {
        var key = BLACKLISTED_KEY_BEGINNING + token;
        return redisTemplate.hasKey(key);
    }
}
