package com.kpanov.clicker.service.kafka;

import com.kpanov.clicker.service.BalanceService;
import com.kpanov.common.event.transfer.CompensateDeductionEvent;
import com.kpanov.common.event.transfer.DeductionSuccessEvent;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Service
@RequiredArgsConstructor
public class TransferConsumer {
    private final BalanceService balanceService;

    @KafkaListener(topics = TRANSFER_REQUEST_EVENTS)
    public void handleTransferRequest(TransferRequestEvent event) {
        var metadata = event.metadata();
        balanceService.deductMoney(
                metadata.id(),
                metadata.initiatorEmail(),
                metadata.targetEmail(),
                metadata.transferAmount()
        );
    }

    @KafkaListener(topics = DEDUCTION_SUCCESS_EVENTS)
    public void handleDeductionSuccess(DeductionSuccessEvent event) {
        var metadata = event.metadata();
        balanceService.depositMoney(
                metadata.id(),
                metadata.initiatorEmail(),
                metadata.targetEmail(),
                metadata.transferAmount()
        );
    }

    @KafkaListener(topics = COMPENSATE_DEDUCTION_EVENTS)
    public void handleCompensateDeduction(CompensateDeductionEvent event) {
        var metadata = event.metadata();

        balanceService.compensateDeduction(metadata.initiatorEmail(), metadata.transferAmount());
    }
}
