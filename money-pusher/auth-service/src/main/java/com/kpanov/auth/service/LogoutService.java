package com.kpanov.auth.service;

import com.kpanov.auth.config.property.JwtProperties;
import com.kpanov.auth.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final TokenService tokenService;
    private final BlacklistService blacklistService;
    private final JwtProperties jwtProperties;

    public void logout(String accessToken, String refreshToken) {
        if(!tokenService.isTokenExists(refreshToken)) {
            throw new BadCredentialsException(Messages.INVALID_REFRESH_TOKEN);
        }

        var accessTokenExpiration = jwtProperties.accessTokenExpiration();
        blacklistService.blacklistToken(accessToken, accessTokenExpiration);

        tokenService.deleteToken(refreshToken);
    }
}
