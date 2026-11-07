package com.kpanov.transfer.service.kafka;

import com.kpanov.common.event.TransferRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.TRANSFER_EVENTS;

@Service
@RequiredArgsConstructor
public class TransferProducer {
    private final KafkaTemplate<Long, TransferRequestEvent> kafkaTemplate;

    public void publishTransferRequest(
            Long id,
            String initiatorEmail,
            String targetEmail,
            Long transferAmount
    ) {
        var event = new TransferRequestEvent(id, initiatorEmail, targetEmail, transferAmount);
        kafkaTemplate.send(TRANSFER_EVENTS, id, event);
    }
}
