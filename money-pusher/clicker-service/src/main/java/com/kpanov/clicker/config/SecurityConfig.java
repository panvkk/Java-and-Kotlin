package com.kpanov.clicker.config;

import com.kpanov.common.security.HeaderAuthenticationFilter;
import com.kpanov.common.security.handler.AppAccessDeniedHandler;
import com.kpanov.common.security.handler.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.kpanov.common.util.Constants.WHITE_LIST_URLS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
        var appAccessDeniedHandler = new AppAccessDeniedHandler();
        var headerFilter = new HeaderAuthenticationFilter();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(
                                        WHITE_LIST_URLS.toArray(String[]::new)
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(appAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );
        return http.build();
    }
}

