package com.escrow.dispute_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DisputeEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(DisputeEventPublisher.class);

    private final KafkaTemplate<String, DisputeEvent> kafkaTemplate;
    private final String topic;

    public DisputeEventPublisher(KafkaTemplate<String, DisputeEvent> kafkaTemplate,
                                 @Value("${app.kafka.dispute-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(DisputeEvent event) {
        kafkaTemplate.send(topic, event.dealId().toString(), event);
        log.info("PUBLISHED dispute event type={} dealId={} topic={}", event.eventType(), event.dealId(), topic);
    }
}
