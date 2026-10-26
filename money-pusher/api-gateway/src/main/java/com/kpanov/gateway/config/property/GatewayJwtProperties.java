package com.kpanov.gateway.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.security.jwt")
public record GatewayJwtProperties(
        String secretKey
) { }