package com.escrow.deal.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealEventMessage {
    private String type;
    private UUID dealId;
    private UUID depositorId;
    private UUID beneficiaryId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
}
