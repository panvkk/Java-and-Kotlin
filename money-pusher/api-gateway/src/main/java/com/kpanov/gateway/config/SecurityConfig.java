package com.kpanov.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.kpanov.common.util.Constants.WHITE_LIST_URLS;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange( exchange ->
                            exchange
                                    .pathMatchers(
                                            WHITE_LIST_URLS.toArray(String[]::new)
                                    ).permitAll()
                                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                    .anyExchange().permitAll()
                )
                .build();
    }
}
