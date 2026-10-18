package com.kpanov.auth.service;

import com.kpanov.auth.repository.UserRepository;
import com.kpanov.auth.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(UserEntity user) {
        userRepository.save(user);
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
