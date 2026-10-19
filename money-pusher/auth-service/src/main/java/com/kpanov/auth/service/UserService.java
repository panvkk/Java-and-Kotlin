package com.kpanov.auth.service;

import com.kpanov.auth.repository.UserRepository;
import com.kpanov.auth.repository.entity.UserEntity;
import com.kpanov.auth.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(UserEntity user) {
        userRepository.save(user);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.USER_NOT_FOUND_BY_EMAIL.formatted(email))
                );
    }
}
