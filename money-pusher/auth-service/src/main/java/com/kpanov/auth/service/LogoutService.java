package com.kpanov.auth.service;

import com.kpanov.auth.config.property.AuthJwtProperties;
import com.kpanov.auth.util.Messages;
import com.kpanov.common.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final TokenService tokenService;
    private final BlacklistService blacklistService;
    private final AuthJwtProperties jwtProperties;
    private JwtUtils jwtUtils;

    private JwtUtils getJwtUtils() {
        if(jwtUtils == null) {
            jwtUtils = new JwtUtils(jwtProperties.secretKey());
        }
        return jwtUtils;
    }

    public void logout(
            String accessToken,
            String refreshToken,
            String userEmail
    ) {
        validateRefreshToken(refreshToken);

        validateAccessToken(accessToken, userEmail);

        var accessTokenExpiration = jwtProperties.accessTokenExpiration();
        blacklistService.blacklistToken(accessToken, accessTokenExpiration);

        tokenService.deleteToken(refreshToken);
    }

    private void validateRefreshToken(String refreshToken) {
        if(!tokenService.isTokenExists(refreshToken)) {
            throw new BadCredentialsException(Messages.INVALID_REFRESH_TOKEN);
        }
    }

    private void validateAccessToken(String accessToken, String userEmail) {
        if(accessToken == null) {
            throw new BadCredentialsException(Messages.INVALID_ACCESS_TOKEN);
        }
        var tokenEmail = getJwtUtils().extractUsername(accessToken);
        if(!getJwtUtils().isTokenExpired(accessToken) || !Objects.equals(tokenEmail, userEmail)) {
            throw new BadCredentialsException(Messages.INVALID_ACCESS_TOKEN);
        }
    }
}
