package com.kpanov.clicker.service.outbox;

import com.kpanov.clicker.repository.OutboxRepository;
import com.kpanov.clicker.repository.entity.OutboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional(propagation = Propagation.MANDATORY)
    public void enqueue(String topic, String key, Object event) {
        outboxRepository.save(OutboxEntity.builder()
                .aggregateId(key)
                .createdAt(Instant.now(clock))
                .eventType(event.getClass().getName())
                .payload(objectMapper.writeValueAsString(event))
                .topic(topic)
                .build()
        );
    }
}
