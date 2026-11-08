package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.transfer.CompensateDeductionEvent;
import com.kpanov.common.event.transfer.TransferRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.COMPENSATE_DEDUCTION_EVENTS;
import static com.kpanov.common.util.Constants.TRANSFER_REQUEST_EVENTS;

@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTransferRequest(TransferRequestEvent event, Long id) {
        kafkaTemplate.send(TRANSFER_REQUEST_EVENTS, id.toString(), event);
    }

    public void publishCompensateDeduction(CompensateDeductionEvent event, Long id) {
        kafkaTemplate.send(COMPENSATE_DEDUCTION_EVENTS, id.toString(), event);
    }
}
