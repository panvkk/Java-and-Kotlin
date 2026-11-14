package com.kpanov.transfer.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpanov.transfer.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {

    private final OutboxRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    // TODO обновить под virtual threads
    @Scheduled(fixedDelayString = "${outbox.relay-interval-ms:1000}")
    @Transactional
    public void relay() {
        var batch = repository.lockPending(50);

        for (var row : batch) {
            try {
                var eventClass = Class.forName(row.getEventType());
                var event = objectMapper.readValue(row.getPayload(), eventClass);

                kafkaTemplate.send(row.getTopic(), row.getAggregateId(), event)
                        .get(5, TimeUnit.SECONDS);

                repository.markPublished(row.getId(), Instant.now(clock));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                repository.markFailed(row.getId(), truncate(e.getMessage()));
            }
        }
    }

    private String truncate(String value) {
        return value == null ? null : value.substring(0, Math.min(value.length(), 1000));
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanup() {
        repository.deletePublishedBefore(
                Instant.now(clock).minus(7, ChronoUnit.DAYS)
        );
    }
}