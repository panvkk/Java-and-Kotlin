package com.kpanov.auth.repository;

import com.kpanov.auth.repository.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {
}
