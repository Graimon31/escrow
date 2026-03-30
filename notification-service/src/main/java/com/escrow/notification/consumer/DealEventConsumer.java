package com.escrow.notification.consumer;

import com.escrow.notification.dto.DealEventMessage;
import com.escrow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DealEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "deal-events", groupId = "notification-service")
    public void handleDealEvent(DealEventMessage event) {
        log.info("Received deal event: type={}, dealId={}", event.getType(), event.getDealId());
        try {
            processEvent(event);
        } catch (Exception e) {
            log.error("Failed to process deal event: {}", event, e);
        }
    }

    private void processEvent(DealEventMessage event) {
        String dealIdShort = event.getDealId().toString().substring(0, 8);

        switch (event.getType()) {
            case "DEAL_CREATED" -> notifyUser(
                    event.getBeneficiaryId(), "DEAL_CREATED",
                    "New Deal Created",
                    "A new escrow deal (" + dealIdShort + "...) has been created with you as the beneficiary for " + event.getAmount() + " " + event.getCurrency() + ".",
                    event.getDealId()
            );

            case "DEAL_SUBMITTED" -> notifyUser(
                    event.getBeneficiaryId(), "DEAL_SUBMITTED",
                    "Deal Awaiting Your Agreement",
                    "Deal " + dealIdShort + "... has been submitted and requires your agreement.",
                    event.getDealId()
            );

            case "DEAL_AGREED" -> notifyUser(
                    event.getDepositorId(), "DEAL_AGREED",
                    "Deal Agreed",
                    "The beneficiary has agreed to deal " + dealIdShort + ".... You can now proceed with funding.",
                    event.getDealId()
            );

            case "DEAL_DECLINED" -> notifyUser(
                    event.getDepositorId(), "DEAL_DECLINED",
                    "Deal Declined",
                    "The beneficiary has declined deal " + dealIdShort + "....",
                    event.getDealId()
            );

            case "DEAL_FUNDED" -> notifyUser(
                    event.getBeneficiaryId(), "DEAL_FUNDED",
                    "Deal Funded",
                    "Deal " + dealIdShort + "... has been funded. You can proceed with fulfillment.",
                    event.getDealId()
            );

            case "DEAL_DELIVERED" -> notifyUser(
                    event.getDepositorId(), "DEAL_DELIVERED",
                    "Delivery Reported",
                    "The beneficiary has marked deal " + dealIdShort + "... as delivered. Please review and confirm.",
                    event.getDealId()
            );

            case "DEAL_COMPLETED" -> {
                notifyUser(event.getDepositorId(), "DEAL_COMPLETED",
                        "Deal Completed",
                        "Deal " + dealIdShort + "... has been completed. Funds have been released to the beneficiary.",
                        event.getDealId());
                notifyUser(event.getBeneficiaryId(), "DEAL_COMPLETED",
                        "Deal Completed — Funds Released",
                        "Deal " + dealIdShort + "... is complete. Funds of " + event.getAmount() + " " + event.getCurrency() + " have been released to you.",
                        event.getDealId());
            }

            case "DEAL_REFUNDED" -> {
                notifyUser(event.getDepositorId(), "DEAL_REFUNDED",
                        "Deal Refunded",
                        "Deal " + dealIdShort + "... has been refunded. Funds of " + event.getAmount() + " " + event.getCurrency() + " have been returned to you.",
                        event.getDealId());
                notifyUser(event.getBeneficiaryId(), "DEAL_REFUNDED",
                        "Deal Refunded",
                        "Deal " + dealIdShort + "... has been refunded to the depositor.",
                        event.getDealId());
            }

            case "DEAL_DISPUTED" -> {
                notifyUser(event.getDepositorId(), "DEAL_DISPUTED",
                        "Deal Disputed",
                        "Deal " + dealIdShort + "... has been marked as disputed. An operator will review.",
                        event.getDealId());
                notifyUser(event.getBeneficiaryId(), "DEAL_DISPUTED",
                        "Deal Disputed",
                        "Deal " + dealIdShort + "... has been marked as disputed. An operator will review.",
                        event.getDealId());
            }

            case "DISPUTE_RESOLVED_RELEASE" -> {
                notifyUser(event.getDepositorId(), "DISPUTE_RESOLVED",
                        "Dispute Resolved",
                        "The dispute on deal " + dealIdShort + "... has been resolved. Funds will be released to the beneficiary.",
                        event.getDealId());
                notifyUser(event.getBeneficiaryId(), "DISPUTE_RESOLVED",
                        "Dispute Resolved — Funds Released",
                        "The dispute on deal " + dealIdShort + "... has been resolved in your favor. Funds will be released to you.",
                        event.getDealId());
            }

            case "DISPUTE_RESOLVED_REFUND" -> {
                notifyUser(event.getDepositorId(), "DISPUTE_RESOLVED",
                        "Dispute Resolved — Funds Refunded",
                        "The dispute on deal " + dealIdShort + "... has been resolved. Funds will be refunded to you.",
                        event.getDealId());
                notifyUser(event.getBeneficiaryId(), "DISPUTE_RESOLVED",
                        "Dispute Resolved",
                        "The dispute on deal " + dealIdShort + "... has been resolved. Funds will be refunded to the depositor.",
                        event.getDealId());
            }

            case "DEAL_CANCELLED" -> {
                notifyUser(event.getDepositorId(), "DEAL_CANCELLED",
                        "Deal Cancelled",
                        "Deal " + dealIdShort + "... has been cancelled.",
                        event.getDealId());
                notifyUser(event.getBeneficiaryId(), "DEAL_CANCELLED",
                        "Deal Cancelled",
                        "Deal " + dealIdShort + "... has been cancelled.",
                        event.getDealId());
            }

            default -> log.warn("Unknown deal event type: {}", event.getType());
        }
    }

    private void notifyUser(UUID userId, String type, String title, String message, UUID dealId) {
        if (userId == null) return;
        notificationService.createNotification(userId, type, title, message, dealId);
    }
}
