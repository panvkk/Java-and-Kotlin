package com.kpanov.auth.service;

import com.kpanov.auth.config.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final StringRedisTemplate redisTemplate;

    private final ApplicationConfig applicationConfig;
    private final long refreshTokenExpiration = applicationConfig.getRefreshTokenExpiration();

    public void saveToken(String token, Integer userId) {
        var key = KEY_BEGINNING + token;
        redisTemplate.opsForValue().set(key, userId.toString(), refreshTokenExpiration);
    }

    public void rotateTokens(
            String oldToken,
            String newToken,
            Integer userId
    ) {
        deleteToken(oldToken);

        saveToken(newToken, userId);
    }

    public void deleteToken(String token) {
        var key = KEY_BEGINNING + token;
        redisTemplate.delete(key);
    }

    public boolean isTokenExists(String token) {
        var key = KEY_BEGINNING + token;
        return redisTemplate.hasKey(key);
    }

    private static final String KEY_BEGINNING = "refresh_token: ";
}