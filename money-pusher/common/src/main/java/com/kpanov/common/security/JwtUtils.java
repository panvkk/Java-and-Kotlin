package com.kpanov.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

public class JwtUtils {

    public static final String TOKEN_TYPE_CLAIM = "token_type";

    private final String secretKeyString;
    private final Clock clock;

    public JwtUtils(String secretKeyString, Clock clock) {
        this.secretKeyString = secretKeyString;
        this.clock = clock;
    }

    public JwtUtils(String secretKeyString) {
        this.secretKeyString = secretKeyString;
        this.clock = Clock.systemDefaultZone();
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        Date now = Date.from(Instant.now(clock));
        return expiration.before(now);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private Key getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKeyString);
        return Keys.hmacShaKeyFor(bytes);
    }
}