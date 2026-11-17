package com.kpanov.clicker.service.outbox;

import com.kpanov.clicker.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static com.kpanov.common.util.Constants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final Clock clock;


    @Scheduled(fixedDelayString = "${outbox.relay-interval-ms:1000}")
    @Transactional
    public void relay() {
        var batch = outboxRepository.lockPending(OUTBOX_RELAY_LIMIT);

        for(var row : batch) {
            try {
                var eventType = Class.forName(row.getEventType());
                var event = objectMapper.readValue(row.getPayload(), eventType);

                kafkaTemplate.send(row.getTopic(), row.getAggregateId(),event)
                        .get(SEND_TOPIC_GET_TIMEOUT_SECONDS, TimeUnit.SECONDS);

                outboxRepository.markPublished(row.getId(), Instant.now(clock));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanUp() {
        outboxRepository.deletePublishedBefore(
                Instant.now(clock).minus(OUTBOX_EXPIRATION_DAYS, ChronoUnit.DAYS)
        );
    }
}
