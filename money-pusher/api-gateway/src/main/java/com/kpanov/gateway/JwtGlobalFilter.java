package com.kpanov.gateway;

import com.kpanov.common.security.JwtUtils;
import com.kpanov.common.security.TokenType;
import com.kpanov.gateway.blacklist.BlacklistService;
import com.kpanov.gateway.config.property.GatewayJwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.kpanov.common.util.Constants.AUTH_HEADER_BEGINNING;

@Component
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter {
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final GatewayJwtProperties jwtProperties;
    private final BlacklistService blacklistService;
    private JwtUtils jwtUtils;

    private JwtUtils getJwtUtils() {
        if(jwtUtils == null) {
            jwtUtils = new JwtUtils(jwtProperties.secretKey());
        }
        return jwtUtils;
    }


    private final List<String> EXCLUDED_URLS = List.of(
            "/",
            "/index.html",
            "/api/v1/auth/**",

            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    private boolean shouldNotFilter(String path) {
        return EXCLUDED_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        var path = request.getURI().getPath();

        if(shouldNotFilter(path)) {
            return chain.filter(exchange);
        }

        final var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final String token;
        final String userEmail;
        final String tokenTypeStr;

        if(authHeader == null || !authHeader.startsWith(AUTH_HEADER_BEGINNING)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        token = authHeader.substring(AUTH_HEADER_BEGINNING.length());
        userEmail = jwtUtils.extractUsername(token);
        tokenTypeStr = jwtUtils.extractTokenType(token);

        // везде кроме refreshToken() используется быстрый accessToken
        if(userEmail == null || !Objects.equals(tokenTypeStr, TokenType.ACCESS.toString())) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        if(!jwtUtils.isTokenValid(token) || blacklistService.isTokenBlacklisted(token)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(MUTATED_HEADER, userEmail)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    private static final String MUTATED_HEADER = "X-User-Name";
}
