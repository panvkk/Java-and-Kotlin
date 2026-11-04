package com.kpanov.gateway.config;

import com.kpanov.gateway.config.property.GatewayJwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayJwtProperties.class)
public class GatewayConfig { }