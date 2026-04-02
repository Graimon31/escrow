package com.escrow.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class HoldRequest {

    @NotNull
    private UUID dealId;

    @NotNull
    private UUID depositorId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
}
