package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.*;
import com.kpanov.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final TransferService transferService;
    private final TransferProducer transferProducer;

    @KafkaListener(topics = DEPOSIT_SUCCESS_EVENTS)
    public void handleTransferSuccess(DepositSuccessEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }

        transferService.processTransferSuccess(metadata.id());
    }

    @KafkaListener(topics = INSUFFICIENT_FUNDS_EVENTS)
    public void handleInsufficientFunds(InsufficientFundsEvent event) {
        if(event == null || event.transferId() == null) {
            log.warn("Dropping insufficient-funds event: transfer id is missing");
            return;
        }

        transferService.processTransferFailed(event.transferId());
    }

    @KafkaListener(topics = DEDUCTION_FAILED_EVENTS)
    public void handleDeductionFailed(DeductionFailedEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }

        transferService.processTransferFailed(metadata.id());
    }

    @KafkaListener(topics = DEPOSIT_FAILED_EVENTS)
    public void handleDepositFailed(DepositFailedEvent event) {
        var metadata = event.metadata();

        if(isInvalid(metadata)) {
            return;
        }

        transferProducer.publishCompensateDeduction(
                new CompensateDeductionEvent(metadata), metadata.id()
        );
        transferService.processTransferFailed(metadata.id());
    }

    private boolean isInvalid(TransferMetadata metadata) {
        if(metadata == null || metadata.id() == null) {
            log.warn("Dropping transfer event: metadata or transfer id is missing");
            return true;
        }

        return false;
    }
}
