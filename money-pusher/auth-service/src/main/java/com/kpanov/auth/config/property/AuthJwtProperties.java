package com.kpanov.auth.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.security.jwt")
public record AuthJwtProperties(
        String secretKey,
        long accessTokenExpiration,
        long refreshTokenExpiration
) { }