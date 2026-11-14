package com.kpanov.clicker.service.kafka;

import com.kpanov.clicker.repository.LedgerRepository;
import com.kpanov.clicker.service.BalanceService;
import com.kpanov.common.event.transfer.*;
import com.kpanov.common.model.TransferSagaPhase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.clicker.util.Messages.PROCESSED_SAGA_PHASE;
import static com.kpanov.common.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final BalanceService balanceService;
    private final LedgerRepository ledgerRepository;

    @Transactional
    @KafkaListener(topics = TRANSFER_REQUEST_EVENTS)
    public void handleTransferRequest(TransferRequestEvent event) {
        if(event == null || event.metadata() == null) {
            log.error("Dropping transfer-request event: metadata is missing");
            return;
        }

        var metadata = event.metadata();

        var insertedRows = insertPhaseIfAbsent(metadata, TransferSagaPhase.DEDUCT);
        if(insertedRows == 0) {
            log.warn(PROCESSED_SAGA_PHASE.formatted(TransferSagaPhase.DEDUCT.toString(), metadata.id()));
            return;
        }

        balanceService.deductMoney(
                metadata.id(),
                metadata.initiatorEmail(),
                metadata.targetEmail(),
                metadata.transferAmount()
        );
    }

    @KafkaListener(topics = DEPOSIT_REQUEST_EVENTS)
    public void handleDepositRequest(DepositRequestEvent event) {
        if(event == null || event.metadata() == null) {
            log.error("Dropping transfer-request event: metadata is missing");
            return;
        }

        var metadata = event.metadata();

        var insertedRows = insertPhaseIfAbsent(metadata, TransferSagaPhase.DEPOSIT);
        if(insertedRows == 0) {
            log.warn(PROCESSED_SAGA_PHASE.formatted(TransferSagaPhase.DEPOSIT.toString(), metadata.id()));
            return;
        }

        balanceService.depositMoney(
                metadata.id(),
                metadata.initiatorEmail(),
                metadata.targetEmail(),
                metadata.transferAmount()
        );
    }

    @KafkaListener(topics = COMPENSATE_DEDUCTION_EVENTS)
    public void handleCompensateDeduction(CompensateDeductionEvent event) {
        if(event == null || event.metadata() == null) {
            log.error("Dropping compensate-deduction event: metadata is missing");
            return;
        }

        var metadata = event.metadata();

        var insertedRows = insertPhaseIfAbsent(metadata, TransferSagaPhase.COMPENSATE);
        if(insertedRows == 0) {
            log.warn(PROCESSED_SAGA_PHASE.formatted(TransferSagaPhase.COMPENSATE.toString(), metadata.id()));
            return;
        }

        balanceService.compensateDeduction(metadata.initiatorEmail(), metadata.transferAmount());
    }

    private int insertPhaseIfAbsent(TransferMetadata metadata, TransferSagaPhase phase) {
        return ledgerRepository.insertPhaseIfAbsent(
                metadata.id(),
                phase,
                metadata.initiatorEmail(),
                metadata.targetEmail(),
                metadata.transferAmount()
        );
    }
}
