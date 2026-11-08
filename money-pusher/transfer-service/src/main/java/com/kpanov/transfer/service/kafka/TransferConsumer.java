package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.CompensateDeductionEvent;
import com.kpanov.common.event.transfer.DeductionFailedEvent;
import com.kpanov.common.event.transfer.DepositFailedEvent;
import com.kpanov.common.event.transfer.DepositSuccessEvent;
import com.kpanov.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.TRANSFER_EVENTS;

@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final TransferService transferService;
    private final TransferProducer transferProducer;

    @KafkaListener(topics = TRANSFER_EVENTS)
    public void handleTransferSuccess(DepositSuccessEvent event) {
        var metadata = event.metadata();

        transferService.processTransferSuccess(metadata.id());
    }

    @KafkaListener(topics = TRANSFER_EVENTS)
    public void handleDeductionFailed(DeductionFailedEvent event) {
        var metadata = event.metadata();

        transferService.processTransferFailed(metadata.id());
    }

    @KafkaListener(topics = TRANSFER_EVENTS)
    public void handleDepositFailed(DepositFailedEvent event) {
        var metadata = event.metadata();

        transferProducer.publishCompensateDeduction(
                new CompensateDeductionEvent(metadata), metadata.id()
        );
        transferService.processTransferFailed(metadata.id());
    }
}