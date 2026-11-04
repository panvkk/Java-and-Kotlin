package com.kpanov.auth.service;

import com.kpanov.auth.config.property.AuthJwtProperties;
import com.kpanov.common.jwt.JwtUtils;
import com.kpanov.common.jwt.TokenType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final Clock clock;
    private final AuthJwtProperties jwtProperties;
    private JwtUtils jwtUtils;

    private JwtUtils getJwtUtils() {
        if(jwtUtils == null) {
            jwtUtils = new JwtUtils(jwtProperties.secretKey(), clock);
        }
        return jwtUtils;
    }

    private static final String TOKEN_TYPE_CLAIM = "token_type";

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> claims,
            UserDetails userDetails
    ) {
        var expiration = jwtProperties.accessTokenExpiration();
        claims.put(TOKEN_TYPE_CLAIM, TokenType.ACCESS.name());

        return buildToken(claims, userDetails, expiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        var expiration = jwtProperties.refreshTokenExpiration();

        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TokenType.REFRESH.name());

        return buildToken(claims, userDetails, expiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        Date now = Date.from(Instant.now(clock));
        var tokenExpiration = new Date(now.getTime() + expiration);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(tokenExpiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        var username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !getJwtUtils().isTokenValid(token);
    }

    public String extractUsername(String token) {
        return getJwtUtils().extractUsername(token);
    }

    public Date extractExpiration(String token) {
        return getJwtUtils().extractExpiration(token);
    }

    public TokenType extractTokenType(String token) {
        String tokenTypeStr = getJwtUtils().extractTokenType(token);
        return TokenType.valueOf(tokenTypeStr);
    }

    private Key getSigningKey() {
        var secretKey = jwtProperties.secretKey();
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
