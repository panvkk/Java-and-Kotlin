package com.kpanov.auth.service;

import com.kpanov.auth.repository.TokenRepository;
import com.kpanov.auth.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {
    private final TokenRepository tokenRepository;

    public void logout(String accessToken, String refreshToken) {
        /** TODO после появления Redis надо добавить accessToken в blacklist с TTL = accessToken expiration */

        if(!tokenRepository.existsByToken(refreshToken)) {
            throw new BadCredentialsException(Messages.INVALID_REFRESH_TOKEN);
        }
        tokenRepository.deleteByToken(refreshToken);
    }
}
