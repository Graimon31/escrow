package com.escrow.deal.messaging;

import com.escrow.deal.entity.Deal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DealEventProducer {

    private static final String TOPIC = "deal-events";

    private final KafkaTemplate<String, DealEventMessage> kafkaTemplate;

    public void publish(String eventType, Deal deal) {
        DealEventMessage message = new DealEventMessage(
                eventType,
                deal.getId(),
                deal.getDepositorId(),
                deal.getBeneficiaryId(),
                deal.getAmount(),
                deal.getCurrency(),
                LocalDateTime.now()
        );

        kafkaTemplate.send(TOPIC, deal.getId().toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} for deal {}", eventType, deal.getId(), ex);
                    } else {
                        log.info("Published {} for deal {}", eventType, deal.getId());
                    }
                });
    }
}
