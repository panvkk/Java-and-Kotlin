package com.kpanov.clicker.repository;

import com.kpanov.clicker.repository.entity.BalanceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<BalanceEntity, Integer> {
    Optional<BalanceEntity> getBalanceEntityByUserEmail(String userEmail);

    @Query(
            value = "SELECT * FROM users_balance_table ORDER BY balance DESC LIMIT :limit",
            nativeQuery = true
    )
    List<BalanceEntity> getTopUsersByBalance(Integer limit);
}