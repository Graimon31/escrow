package com.escrow.resolution_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ResolutionEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ResolutionEventPublisher.class);

    private final KafkaTemplate<String, ResolutionEvent> kafkaTemplate;
    private final String topic;

    public ResolutionEventPublisher(KafkaTemplate<String, ResolutionEvent> kafkaTemplate,
                                    @Value("${app.kafka.resolution-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(ResolutionEvent event) {
        kafkaTemplate.send(topic, event.dealId().toString(), event);
        log.info("PUBLISHED resolution event type={} dealId={} topic={}", event.eventType(), event.dealId(), topic);
    }
}
