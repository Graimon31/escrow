package com.escrow.payment.dto;

import com.escrow.payment.entity.Transaction;
import com.escrow.payment.entity.TransactionStatus;
import com.escrow.payment.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private UUID id;
    private UUID dealId;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(tx.getId(), tx.getDealId(),
                tx.getFromAccountId(), tx.getToAccountId(),
                tx.getAmount(), tx.getType(), tx.getStatus(),
                tx.getCreatedAt(), tx.getCompletedAt());
    }
}
