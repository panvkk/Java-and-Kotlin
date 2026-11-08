package com.kpanov.clicker.service;

import com.kpanov.clicker.repository.BalanceRepository;
import com.kpanov.clicker.repository.entity.BalanceEntity;
import com.kpanov.clicker.service.kafka.TransferProducer;
import com.kpanov.common.event.transfer.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository repository;
    private final TransferProducer transferProducer;

    @Transactional
    public void deductMoney(
            Long transferId,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {
        var initiatorBalance = repository.getBalanceWithLock(initiatorEmail);

        if(initiatorBalance.isPresent()) {
            var entity = initiatorBalance.get();
            var balanceRemain = entity.getBalance() - transferAmount;

            if(balanceRemain >= 0) {
                repository.updateBalance(initiatorEmail, balanceRemain);

                transferProducer.publishDeductionSuccess(
                        new DeductionSuccessEvent(
                                new TransferMetadata(
                                        transferId, initiatorEmail, targetEmail, transferAmount
                                )
                        ),
                        transferId
                );
            } else {
                transferProducer.publishInsufficientFunds(
                        new InsufficientFundsEvent(transferId, initiatorEmail), transferId
                );
            }
        } else {
            transferProducer.publishDeductionFailed(
                    new DeductionFailedEvent(
                            new TransferMetadata(
                                    transferId, initiatorEmail, targetEmail, transferAmount
                            )
                    ),
                    transferId
            );
        }
    }

    @Transactional
    public void compensateDeduction(
            String initiatorEmail,
            Long transferAmount
    ) {
        var initiatorBalance = repository.getBalanceWithLock(initiatorEmail);

        if(initiatorBalance.isPresent()) {
            var entity = initiatorBalance.get();
            var newBalance = entity.getBalance() + transferAmount;

            repository.updateBalance(initiatorEmail, newBalance);
        } else {
            // TODO такого и быть не должно быть, но надо обработать как-то хзз
        }
    }

    @Transactional
    public void depositMoney(
            Long transferId,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {
        var targetBalance = repository.getBalanceWithLock(targetEmail);

        if(targetBalance.isPresent()) {
            var entity = targetBalance.get();
            var newBalance = entity.getBalance() + transferAmount;

            repository.updateBalance(targetEmail, newBalance);

            transferProducer.publishDepositSuccess(
                    new DepositSuccessEvent(
                            new TransferMetadata(
                                    transferId, initiatorEmail, targetEmail, transferAmount
                            )
                    ),
                    transferId
            );

        } else {
            transferProducer.publishDeductionFailed(
                    new DeductionFailedEvent(
                            new TransferMetadata(
                                    transferId, initiatorEmail, targetEmail, transferAmount
                            )
                    ),
                    transferId
            );
        }
    }
}
