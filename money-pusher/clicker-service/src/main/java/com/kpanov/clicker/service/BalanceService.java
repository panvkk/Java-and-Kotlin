package com.kpanov.clicker.service;

import com.kpanov.clicker.repository.BalanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository repository;

    @Transactional
    public void deductMoney(
            Long transferId,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {
        var initiatorBalanceEntity = repository.getBalanceWithLock(initiatorEmail);

        if(initiatorBalanceEntity.isPresent()) {
            var balanceRemain = initiatorBalanceEntity.get().getBalance() - transferAmount;

            if(balanceRemain >= 0) {

            }
        }
    }
}
