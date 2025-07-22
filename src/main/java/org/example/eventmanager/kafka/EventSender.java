package org.example.eventmanager.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventSender {

    private static final Logger log = LoggerFactory.getLogger(EventSender.class);

    private final KafkaTemplate<Integer, KafkaEvent> kafkaTemplate;

    public EventSender(KafkaTemplate<Integer, KafkaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(KafkaEvent eventKafka) {
        log.info("Sending event: event={}", eventKafka);
        var result = kafkaTemplate.send(
                "events-topic",
                eventKafka.getEventId(),
                eventKafka
        );

        result.thenAccept(sendResult -> log.info("Send successful"));
    }
}
