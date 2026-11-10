package com.kpanov.gateway.config;

import com.kpanov.gateway.config.property.GatewayJwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(GatewayJwtProperties.class)
public class GatewayConfig {
    @Bean
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }
}