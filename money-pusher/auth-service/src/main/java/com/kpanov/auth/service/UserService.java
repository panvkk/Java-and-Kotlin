package com.kpanov.auth.service;

import com.kpanov.auth.repository.UserRepository;
import com.kpanov.auth.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public final UserRepository userRepository;

    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }
}
