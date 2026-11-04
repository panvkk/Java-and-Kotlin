package com.kpanov.clicker.service.kafka;

import com.kpanov.clicker.service.ClickerService;
import com.kpanov.common.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.USER_EVENTS;

@Service
@RequiredArgsConstructor
public class UserConsumer {
    private final ClickerService clickerService;

    @KafkaListener(topics = USER_EVENTS)
    public void handleUserCreated(UserCreatedEvent event) {
        clickerService.createNewUserBalance(event.userId(), event.nickname(), event.email());
    }
}
