package com.kpanov.clicker.repository;

import com.kpanov.clicker.repository.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BalanceRepository extends JpaRepository<BalanceEntity, Integer> {
    Optional<Integer> getBalanceByUserId(Integer userId);

    void updateBalance(BalanceEntity balance);
}
