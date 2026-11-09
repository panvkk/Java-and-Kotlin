package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.*;
import com.kpanov.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final TransferService transferService;
    private final TransferProducer transferProducer;

    @KafkaListener(topics = DEPOSIT_SUCCESS_EVENTS)
    public void handleTransferSuccess(DepositSuccessEvent event) {
        var metadata = event.metadata();

        transferService.processTransferSuccess(metadata.id());
    }

    @KafkaListener(topics = INSUFFICIENT_FUNDS_EVENTS)
    public void handleInsufficientFunds(InsufficientFundsEvent event) {
        transferService.processTransferFailed(event.transferId());
    }

    @KafkaListener(topics = DEDUCTION_FAILED_EVENTS)
    public void handleDeductionFailed(DeductionFailedEvent event) {
        var metadata = event.metadata();

        transferService.processTransferFailed(metadata.id());
    }

    @KafkaListener(topics = DEPOSIT_FAILED_EVENTS)
    public void handleDepositFailed(DepositFailedEvent event) {
        var metadata = event.metadata();

        transferProducer.publishCompensateDeduction(
                new CompensateDeductionEvent(metadata), metadata.id()
        );
        transferService.processTransferFailed(metadata.id());
    }
}