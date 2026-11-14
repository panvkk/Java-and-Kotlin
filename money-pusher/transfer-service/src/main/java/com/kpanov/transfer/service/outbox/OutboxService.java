package com.kpanov.transfer.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpanov.transfer.repository.OutboxRepository;
import com.kpanov.transfer.repository.entity.OutboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository repository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String topic, String key, Object event) {
        try {
            var now = Instant.now(clock);
            repository.save(OutboxEntity.builder()
                    .aggregateId(key)
                    .eventType(event.getClass().getName())
                    .topic(topic)
                    .payload(objectMapper.writeValueAsString(event))
                    .createdAt(now)
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Cannot serialize outbox event: " + event.getClass(), e
            );
        }
    }
}