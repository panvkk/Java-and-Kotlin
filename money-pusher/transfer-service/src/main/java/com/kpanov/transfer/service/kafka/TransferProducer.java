package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.CompensateDeductionEvent;
import com.kpanov.common.event.transfer.DepositRequestEvent;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import com.kpanov.transfer.repository.TransferRepository;
import com.kpanov.transfer.repository.entity.TransferSagaPhase;
import com.kpanov.transfer.util.Messages;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransferRepository transferRepository;

    public void publishTransferRequest(TransferRequestEvent event, Long id) {
        kafkaTemplate.send(TRANSFER_REQUEST_EVENTS, id.toString(), event);
    }

    public void publishDepositRequest(DepositRequestEvent event, Long id) {
        kafkaTemplate.send(DEPOSIT_REQUEST_EVENTS, id.toString(), event);
    }

    public void publishCompensateDeduction(CompensateDeductionEvent event, Long id) {
        kafkaTemplate.send(COMPENSATE_DEDUCTION_EVENTS, id.toString(), event);
    }
}
