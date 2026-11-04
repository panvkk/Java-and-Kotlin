package com.kpanov.auth.service.kafka;

import com.kpanov.common.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.kpanov.common.util.Constants.USER_EVENTS;

@Service
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<Integer, UserCreatedEvent> kafkaTemplate;

    public void publishUserCreated(
            Integer userId,
            String nickname,
            String email
    ) {
        var event = new UserCreatedEvent(userId, nickname, email);

        kafkaTemplate.send(USER_EVENTS, userId, event);
    }
}
