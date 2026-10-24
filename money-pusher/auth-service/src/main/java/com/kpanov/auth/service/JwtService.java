package com.kpanov.auth.service;

import com.kpanov.auth.config.property.JwtProperties;
import com.kpanov.auth.service.model.TokenType;
import io.jsonwebtoken.Claims;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final Clock clock;
    private final JwtProperties jwtProperties;

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
                && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public TokenType extractTokenType(String token) {
        return extractClaim(token, ( claims ->
            {
                var tokenTypeStr = claims.get(TOKEN_TYPE_CLAIM, String.class);
                return TokenType.valueOf(tokenTypeStr);
            }
        ));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        var expiration = extractExpiration(token);
        Date now = Date.from(Instant.now(clock));

        return expiration.before(now);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        var secretKey = jwtProperties.secretKey();
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
