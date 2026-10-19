package com.kpanov.auth.repository;

import com.kpanov.auth.repository.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {

    Optional<TokenEntity> findByToken(String token);

    void deleteByToken(String token);

    boolean existsByToken(String token);
}
