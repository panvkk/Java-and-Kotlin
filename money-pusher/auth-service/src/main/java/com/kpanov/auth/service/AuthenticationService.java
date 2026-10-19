package com.kpanov.auth.service;

import com.kpanov.auth.repository.entity.UserEntity;
import com.kpanov.auth.service.model.AuthenticationRequest;
import com.kpanov.auth.service.model.AuthenticationResponse;
import com.kpanov.auth.service.model.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kpanov.auth.util.Messages;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userService.getUserByEmail(request.email());

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        tokenService.saveToken(user, refreshToken);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserEntity.builder()
                .nickname(request.nickname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userService.createUser(user);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        tokenService.saveToken(user, refreshToken);
        return new AuthenticationResponse(accessToken, refreshToken);
    }



    public AuthenticationResponse refreshToken(String token) {
        final var userEmail = jwtService.extractUsername(token);
        if(userEmail == null) {
            throw new BadCredentialsException(Messages.INVALID_REFRESH_TOKEN);
        }

        final var user = userService.getUserByEmail(userEmail);

        if(!jwtService.isTokenValid(token, user)) {
            throw new BadCredentialsException(Messages.INVALID_REFRESH_TOKEN);
        }

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.rotateTokens(user, token, refreshToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }
}
