package com.kpanov.auth.service;

import com.kpanov.auth.repository.TokenRepository;
import com.kpanov.auth.repository.entity.TokenEntity;
import com.kpanov.auth.repository.entity.TokenType;
import com.kpanov.auth.repository.entity.UserEntity;
import com.kpanov.auth.service.model.AuthenticationRequest;
import com.kpanov.auth.service.model.AuthenticationResponse;
import com.kpanov.auth.service.model.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository; // точно ли authService должен использовать tokenRepo

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .role(request.role())
                .build();

        userService.saveUser(user);
        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, token);
    }

    private void saveUserToken(UserEntity user, String token) {
        var tokenEntity = TokenEntity.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(tokenEntity);
    }

    public void refreshToken() {

    }
}
