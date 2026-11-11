package com.kpanov.clicker.service.kafka;

import com.kpanov.clicker.service.ClickerService;
import com.kpanov.common.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.USER_CREATED_EVENTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {
    private final ClickerService clickerService;

    @KafkaListener(topics = USER_CREATED_EVENTS)
    public void handleUserCreated(UserCreatedEvent event) {
        if(event == null) {
            log.error("Dropping empty user-created event");
            return;
        }

        clickerService.createNewUserBalance(event.userId(), event.nickname(), event.email());
    }
}
