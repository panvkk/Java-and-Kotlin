package com.kpanov.clicker.repository;

import com.kpanov.clicker.repository.entity.BalanceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<BalanceEntity, Integer> {
    Optional<BalanceEntity> getBalanceEntityByUserEmail(String userEmail);

    @Query(
            value = "SELECT * FROM users_balance_table ORDER BY balance DESC LIMIT :limit",
            nativeQuery = true
    )
    List<BalanceEntity> getTopUsersByBalance(@Param("limit") Integer limit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            value = "SELECT * FROM user_balance_table " +
                    "WHERE user_email = :email ",
            nativeQuery = true
    )
    Optional<BalanceEntity> getBalanceWithLock(@Param("email") String email);
}