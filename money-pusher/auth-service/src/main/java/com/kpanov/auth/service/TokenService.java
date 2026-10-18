package com.kpanov.auth.service;

import com.kpanov.auth.repository.TokenRepository;
import com.kpanov.auth.repository.entity.TokenEntity;
import com.kpanov.auth.repository.entity.TokenType;
import com.kpanov.auth.repository.entity.UserEntity;
import com.kpanov.auth.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void rotateTokens(UserEntity user, String oldToken, String newToken) {
        var oldTokenEntity = tokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new EntityNotFoundException(Messages.TOKEN_NOT_FOUND));
        tokenRepository.delete(oldTokenEntity);

        var newTokenEntity = TokenEntity.builder()
                .user(user)
                .token(newToken)
                .tokenType(TokenType.BEARER)
                .build();

        tokenRepository.save(newTokenEntity);
    }

    public void saveToken(UserEntity user, String token) {
        var tokenEntity = TokenEntity.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.BEARER)
                .build();
        tokenRepository.save(tokenEntity);
    }
}